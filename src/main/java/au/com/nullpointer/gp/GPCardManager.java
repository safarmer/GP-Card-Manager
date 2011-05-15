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

import au.com.nullpointer.gp.der.CardData;
import au.com.nullpointer.gp.der.CardRecognitionData;
import au.com.nullpointer.gp.der.DecodingException;
import au.com.nullpointer.gp.scp.SecureChannel;
import au.com.nullpointer.gp.scp.SecureChannelProtocol01;
import au.com.nullpointer.gp.scp.SecureChannelProtocol02;
import au.com.nullpointer.gp.scp.SecureChannelProtocol03;
import au.com.nullpointer.ifd.InterfaceDevice;

/**
 * @author shane
 * 
 */
public class GPCardManager {
    public final static int INSTALL_FOR_LOAD = 0x01;
    public final static int INSTALL_FOR_INSTALL = 0x02;
    public final static int INSTALL_FOR_MAKE_SELECTABLE = 0x04;

    public final static int TAG_IIN = 0x0042;
    public final static int TAG_CIN = 0x0045;
    public final static int TAG_CARD_DATA = 0x0066;
    public final static int TAG_KEY_TEMPLATE = 0x00E0;
    public final static int TAG_SEQUENCE = 0x00C1;
    public final static int TAG_CONFIRMATION = 0x00C2;

    public final static int LOAD_BLOCK_SIZE = 250;

    private InterfaceDevice ifd;
    private CardData card;
    private SecureChannel sc;

    /**
     * @param ifd
     */
    public GPCardManager(InterfaceDevice ifd) {
        this.ifd = ifd;
    }

    /**
     * 
     */
    public void select() {
        ifd.transmit(0x00, 0xa4, 0x04, 0x00, Integer.valueOf(0));
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
     * @param aid
     * @param withObjects
     */
    public void delete(byte[] aid, boolean withObjects) {
        int p2 = withObjects ? 0x80 : 0x00;
        ifd.transmit(0x80, 0xe4, 0x00, p2, aid);
    }

    public void getStatus() {

    }

    public void setStatus() {

    }

    public void putKey() {

    }
}
