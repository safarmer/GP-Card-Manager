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

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import au.com.nullpointer.codec.HexString;
import au.com.nullpointer.kms.KeySet;

/**
 * @author shane
 * 
 */
public class Security {
    private Cipher desCbcCipher;
    private Cipher desEcbCipher;
    private SecretKeySpec singledesCMAC;

    private final static byte[] DEFAULT_ICV = new byte[8];
    private final static Logger LOG = Logger.getLogger(Security.class);

    /**
     * @throws GeneralSecurityException
     * 
     */
    public Security() throws GeneralSecurityException {
        desCbcCipher = Cipher.getInstance("DESede/CBC/NoPadding");
        desEcbCipher = Cipher.getInstance("DESede/ECB/NoPadding");
    }

    public SecretKey createSecretKey(byte[] key) {
        if (key.length == 16) {
            key = Arrays.copyOf(key, 24);
        }

        return new SecretKeySpec(key, "DESede");
    }

    public KeySet createSessionKeys(KeySet masterKeys, int sequenceCounter) throws GeneralSecurityException {
        byte[] derivationData = new byte[16];
        derivationData[2] = (byte) ((sequenceCounter >> 8) & 0xff);
        derivationData[3] = (byte) (sequenceCounter & 0xff);

        derivationData[0] = (byte) 0x01;
        derivationData[1] = (byte) 0x82;
        LOG.debug("Input to session S-ENC derivation: " + HexString.encode(derivationData));
        // sessionSENC = encode(ENC, derivationData);
        SecretKey sessionSENC = deriveKey(masterKeys.getEncKey(), derivationData);
        LOG.debug("S-ENC: " + HexString.encode(sessionSENC.getEncoded()));

        // Derivation data used for CMAC-Session-Key creation
        derivationData[0] = (byte) 0x01;
        derivationData[1] = (byte) 0x01;
        LOG.debug("Input to session CMAC derivation: " + HexString.encode(derivationData));
        // sessionCMAC = encode(MAC, derivationData);
        SecretKey sessionCMAC = deriveKey(masterKeys.getMacKey(), derivationData);
        LOG.debug("S-MAC: " + HexString.encode(sessionCMAC.getEncoded()));
        singledesCMAC = new SecretKeySpec(sessionCMAC.getEncoded(), 0, 8, "DES");

        // Derivation data used for DENC-Session-Key creation
        derivationData[0] = (byte) 0x01;
        derivationData[1] = (byte) 0x81;
        LOG.debug("Input to session DEK derivation : " + HexString.encode(derivationData));
        // sessionDEK = encode(KEY, derivationData);
        SecretKey sessionDEK = deriveKey(masterKeys.getDekKey(), derivationData);
        LOG.debug("S-DEK: " + HexString.encode(sessionDEK.getEncoded()));

        return new KeySet(sessionSENC, sessionCMAC, sessionDEK);
    }

    /**
     * 
     * @param keyData
     * @param data
     * @return
     * @throws GeneralSecurityException
     */
    private SecretKey deriveKey(SecretKey key, byte[] data) throws GeneralSecurityException {
        // Cipher cipher = Cipher.getInstance(type + "/CBC/NoPadding");
        IvParameterSpec dps = new IvParameterSpec(DEFAULT_ICV);
        desCbcCipher.init(Cipher.ENCRYPT_MODE, key, dps);

        byte[] result = desCbcCipher.doFinal(data);
        adjustParity(result);

        return createSecretKey(result);
    }

    /**
     * Adjust a DES key to odd parity
     * 
     * @param key
     *            to be adjusted
     */
    public static byte[] adjustParity(byte[] key) {
        for (int i = 0; i < key.length; i++) {
            int akku = (key[i] & 0xFF) | 1;

            for (int c = 7; c > 0; c--) {
                akku = (akku & 1) ^ (akku >> 1);
            }
            key[i] = (byte) ((key[i] & 0xFE) | akku);
        }

        return key;
    }

    public KeySet diversify(KeySet master, byte[] initUpdateResponse) {
        return null;
    }
}
