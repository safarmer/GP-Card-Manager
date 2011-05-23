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
package au.com.nullpointer.kms;

import javax.crypto.SecretKey;

/**
 * @author shane
 * 
 */
public class KeySet {
    private SecretKey encKey;
    private SecretKey macKey;
    private SecretKey dekKey;

    public KeySet(SecretKey master) {
        this.encKey = master;
        this.macKey = master;
        this.dekKey = master;
    }

    public KeySet(SecretKey encKey, SecretKey macKey, SecretKey dekKey) {
        this.encKey = encKey;
        this.macKey = macKey;
        this.dekKey = dekKey;
    }

    /**
     * @return the encKey
     */
    public SecretKey getEncKey() {
        return encKey;
    }

    /**
     * @param encKey
     *            the encKey to set
     */
    public void setEncKey(SecretKey encKey) {
        this.encKey = encKey;
    }

    /**
     * @return the macKey
     */
    public SecretKey getMacKey() {
        return macKey;
    }

    /**
     * @param macKey
     *            the macKey to set
     */
    public void setMacKey(SecretKey macKey) {
        this.macKey = macKey;
    }

    /**
     * @return the dekKey
     */
    public SecretKey getDekKey() {
        return dekKey;
    }

    /**
     * @param dekKey
     *            the dekKey to set
     */
    public void setDekKey(SecretKey dekKey) {
        this.dekKey = dekKey;
    }

}
