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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.nullpointer.codec.HexString;
import au.com.nullpointer.gp.der.CardRecognitionData;
import au.com.nullpointer.ifd.InterfaceDevice;

/**
 * @author shane
 * 
 */
@RunWith(PowerMockRunner.class)
public class GPCardManagerTest {
    @Mock
    InterfaceDevice ifd;

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#select()}.
     */
    @Test
    public void testSelect() {
        when(ifd.transmit(0x00, 0xa4, 0x04, 0x00, 0x00)).thenReturn(
                HexString.decode("6F 10 84 08 A0 00 00 00 03 00 00 00 A5 04 9F 65 01 FF"));

        GPCardManager cm = new GPCardManager(ifd);
        cm.select();

        verify(ifd, only()).transmit(0x00, 0xa4, 0x04, 0x00, 0x00);
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#getSecureChannel()}.
     */
    @Test
    public void testGetSecureChannel() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#getData(int)}.
     */
    @Test
    public void testGetDataCardRecognition() throws Exception {
        byte[] crd = HexString
                .decode("66 4C 73 4A 06 07 2A 86 48 86 FC 6B 01 60 0C 06 0A 2A 86 48 86 FC 6B 02 02 01 01 63 09 06 07 2A 86 48 86 FC 6B 03 64 0B 06 09 2A 86 48 86 FC 6B 04 02 15 65 0B 06 09 2B 85 10 86 48 64 02 01 03 66 0C 06 0A 2B 06 01 04 01 2A 02 6E 01 02");

        when(ifd.transmit(0x80, 0xca, 0x00, 0x66, 0x00)).thenReturn(crd);

        GPCardManager cm = new GPCardManager(ifd);
        byte[] ret = cm.getData(0x0066);

        verify(ifd, only()).transmit(0x80, 0xca, 0x00, 0x66, 0x00);

        CardRecognitionData data = new CardRecognitionData(ret);

        System.out.println(data);
        assertEquals("2.1.1", data.getGpVersion());
        assertEquals(2, data.getScpVersion());
        assertEquals(0x15, data.getScpIValue());
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#getData(int)}.
     */
    @Test
    public void testGetData() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#storeData(int, byte[])}.
     */
    @Test
    public void testStoreData() {
        byte[] data = "12345".getBytes();
        when(ifd.transmit(0x80, 0xe2, 0x00, 0x42, data)).thenReturn(HexString.decode("0x9000"));

        GPCardManager cm = new GPCardManager(ifd);
        cm.storeData(0x0042, data);

        verify(ifd).transmit(0x80, 0xe2, 0x00, 0x42, data);
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#load(java.util.zip.ZipFile)}.
     */
    @Test
    public void testLoadZipFile() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#load(java.io.File)}.
     */
    @Test
    public void testLoadFile() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#load(byte[])}.
     */
    @Test
    public void testLoadByteArray() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#install(int, byte[])}.
     */
    @Test
    public void testInstall() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#delete(byte[], boolean)}.
     */
    @Test
    public void testDelete() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#getStatus()}.
     */
    @Test
    public void testGetStatus() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#setStatus()}.
     */
    @Test
    public void testSetStatus() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link au.com.nullpointer.gp.GPCardManager#putKey()}.
     */
    @Test
    public void testPutKey() {
        fail("Not yet implemented");
    }

}
