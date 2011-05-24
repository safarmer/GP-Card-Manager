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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * @author shane
 * 
 */
public class JCEKeyManager extends KeyManager {
    public final static String DEFAULT_KEYSTORE = System.getProperty("user.home") + "/.kms/keystore.jks";
    public final static String DEFAULT_PASSWORD = "changeit";

    /**
     * @throws KeyManagerException
     */
    protected JCEKeyManager(File keyFile, String passwd) throws KeyManagerException {
        super();
        try {
            store = KeyStore.getInstance("JCEKS");
            store.load(new FileInputStream(keyFile), passwd.toCharArray());
        } catch (KeyStoreException e) {
            throw new KeyManagerException("Could not initialise key store", e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyManagerException("Could not initialise key store. A required algorithm may be unavailable", e);
        } catch (CertificateException e) {
            throw new KeyManagerException("Could not initialise key store. One or more certificates could not be loaded", e);
        } catch (FileNotFoundException e) {
            throw new KeyManagerException("Could not initialise key store. The key store file is not available", e);
        } catch (IOException e) {
            throw new KeyManagerException("Could not initialise key store. COUld not read from key store file", e);
        }
    }

    /**
     * @throws KeyManagerException
     * 
     */
    protected JCEKeyManager() throws KeyManagerException {
        this(new File(DEFAULT_KEYSTORE), DEFAULT_PASSWORD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.nullpointer.kms.KeyManager#loadKeyStore()
     */
    @Override
    protected void loadKeyStore() throws KeyManagerException {
        // TODO Auto-generated method stub

    }

}
