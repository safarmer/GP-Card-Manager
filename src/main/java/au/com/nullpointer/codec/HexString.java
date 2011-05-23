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
package au.com.nullpointer.codec;

/**
 * HexString codec class.
 * 
 * @author safarmer
 */
public class HexString {
    private final static char[] HEX = "0123456789abcdef".toCharArray();
    private final static int[] CHAR = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
            0x06, 0x07, 0x08, 0x09, -1, -1, -1, -1, -1, -1, -1, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

    /**
     * Encode a byte array into a Hex string in a relatively efficient manner.
     * 
     * @param data
     *            the byte array to convert to a hex string
     * @return the string representation of the byte data hex encoded
     */
    public static String encode(byte[] data) {
        StringBuffer hex = new StringBuffer(data.length * 2);

        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            hex.append(HEX[(b >> 4) & 0x0f]);
            hex.append(HEX[b & 0x0f]);
        }

        return hex.toString();
    }

    public static byte[] decode(String data) {
        String clean = data.replaceAll("0x", "").replaceAll("[^0-9a-fA-F]", "");

        if ((clean.length() % 2) != 0) {
            clean = "0" + clean;
        }

        byte[] b = new byte[data.trim().length() / 2];
        int off = 0;

        for (int i = 0; i < clean.length(); i += 2) {
            char ch = clean.charAt(i);
            char cl = clean.charAt(i + 1);

            b[off++] = (byte) (CHAR[ch] << 4 | CHAR[cl]);
        }

        return b;
    }
}
