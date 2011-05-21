/*
 * Copyright (c) 2011 NullPointer Software
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. 
 */
package au.com.nullpointer.gp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.bouncycastle.asn1.DERApplicationSpecific;

import au.com.nullpointer.gp.der.CardData;
import au.com.nullpointer.gp.der.CardRecognitionData;
import au.com.nullpointer.gp.der.DecodingException;
import au.com.nullpointer.gp.scp.SecureChannel;
import au.com.nullpointer.gp.scp.SecureChannelProtocol01;
import au.com.nullpointer.gp.scp.SecureChannelProtocol02;
import au.com.nullpointer.gp.scp.SecureChannelProtocol03;
import au.com.nullpointer.ifd.InterfaceDevice;
import au.com.nullpointer.iso7816.AID;

/**
 * @author safarmer
 */
public class GPCardManager {
    /** Install mode for INSTALL FOR LOAD. */
    public final static int INSTALL_FOR_LOAD = 0x01;
    /** Install mode for INSTALL FOR INSTALL. */
    public final static int INSTALL_FOR_INSTALL = 0x02;
    /** Install mode for INSTALL FOR MAKE SELECTABLE. */
    public final static int INSTALL_FOR_MAKE_SELECTABLE = 0x04;

    public final static byte DELETE_APPLICATION = 0x0f;
    public final static byte DELETE_KEY_IDENTITFIER = (byte) 0xD0;
    public final static byte DELETE_KEY_VERSION = (byte) 0xD2;

    /** Get data tag for IIN. */
    public final static int TAG_IIN = 0x0042;
    /** Get data tag for CIN. */
    public final static int TAG_CIN = 0x0045;
    /** Get data tag for Card Recognition Data. */
    public final static int TAG_CARD_DATA = 0x0066;
    /** Get data tag for Key Template Data. */
    public final static int TAG_KEY_TEMPLATE = 0x00E0;
    /** Get data tag for default key set sequence counter. */
    public final static int TAG_SEQUENCE = 0x00C1;
    /** Get data tag for confirmation counter. */
    public final static int TAG_CONFIRMATION = 0x00C2;

    /** Maximum load file block size. */
    public final static int LOAD_BLOCK_SIZE = 250;

    /** Interface Device that is used to communicate with smart card. */
    private InterfaceDevice ifd;
    /** The card recognition data for the current card. */
    private CardData card;
    /** The secure channel protocol implementation for the current card. */
    private SecureChannel sc;

    /**
     * @param ifd
     */
    public GPCardManager(InterfaceDevice ifd) {
        this.ifd = ifd;
    }

    /**
     * Select the GlobalPlatform card manager.
     * 
     * @return the response to SELECT from the card.
     */
    public byte[] select() {
        return ifd.transmit(0x00, 0xa4, 0x04, 0x00, Integer.valueOf(0));
    }

    /**
     * @return
     */
    public SecureChannel getSecureChannel() {
        if (sc == null) {
            try {
                if (card == null) {
                    card = new CardRecognitionData(getData(TAG_CARD_DATA));
                }

                switch (card.getScpVersion()) {
                    case 1:
                        sc = new SecureChannelProtocol01();
                        break;

                    case 2:
                        sc = new SecureChannelProtocol02();
                        break;

                    case 3:
                        sc = new SecureChannelProtocol03();
                        break;
                }
            } catch (DecodingException e) {
                throw new IllegalStateException("Unrecognised card. Possibly not a GlobalPlatform card", e);
            }
        }

        return sc;
    }

    /**
     * @param tag
     * @return
     */
    public byte[] getData(int tag) {
        int high = (tag >> 8) & 0xff;
        int low = tag & 0xff;
        return ifd.transmit(0x80, 0xca, high, low, 0x00);
    }

    /**
     * @param tag
     * @param data
     */
    public void storeData(int tag, byte[] data) {
        int high = (tag >> 8) & 0xff;
        int low = tag & 0xff;
        ifd.transmit(0x80, 0xe2, high, low, data);
    }

    public void load(ZipFile zip) {

    }

    /**
     * @param loadFile
     * @throws IOException
     */
    public void load(File loadFile) throws IOException {
        FileInputStream fin = new FileInputStream(loadFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            byte[] buf = new byte[1024];
            int read = 0;

            while ((read = fin.read(buf)) != -1) {
                bos.write(buf, 0, read);
            }

            load(bos.toByteArray());

            bos.close();
        } finally {
            bos.close();
            fin.close();
        }
    }

    /**
     * @param loadFile
     */
    public void load(byte[] loadFile) {
        int remain = loadFile.length;
        int off = 0;
        int p1 = 0;
        int block = 0;
        byte[] data = new byte[LOAD_BLOCK_SIZE];
        int sent = 0;

        while (remain > 0) {
            if (remain <= LOAD_BLOCK_SIZE) {
                p1 = 0x80;
                sent = remain;
            } else {
                sent = LOAD_BLOCK_SIZE;
            }

            ifd.transmit(0x80, 0xe8, p1, block++, data);

            remain -= sent;
            off += sent;
        }
    }

    /**
     * @param mode
     * @param aid
     */
    public void install(int mode, byte[] aid) {
        ifd.transmit(0x80, 0xe6, mode, 0x00, aid, 0x00);
    }

    /**
     * @param aids
     * @param withObjects
     */
    public void delete(AID... aids) {
        delete(false, aids);
    }

    /**
     * @param aid
     * @param withObjects
     */
    public void delete(boolean withRelated, AID... aids) {
        int p2 = withRelated ? 0x80 : 0x00;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        for (AID aid : aids) {
            try {
                bos.write(new DERApplicationSpecific(DELETE_APPLICATION, aid.getAid()).getDEREncoded());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        ifd.transmit(0x80, 0xe4, 0x00, p2, bos.toByteArray());
    }

    public void getStatus() {

    }

    public void setStatus() {

    }

    public void putKey() {

    }
}
