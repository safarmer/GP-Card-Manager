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
package au.com.nullpointer.gp.der;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.com.nullpointer.codec.HexString;

/**
 * @author shane
 * 
 */
public class CardRecognitionDataTest {

    /**
     * Test method for {@link au.com.nullpointer.gp.der.CardData#CardRecognitionData(byte[])}.
     */
    @Test
    public void testCardRecognitionData() throws Exception {
        byte[] crd = HexString.decode("66 4C 73 4A 06 07 2A 86 48 86 FC 6B 01 60 0C 06 0A 2A 86 48 86 FC 6B 02 02 01 01 63 09 06 07 2A"
                + "86 48 86 FC 6B 03 64 0B 06 09 2A 86 48 86 FC 6B 04 02 15 65 0B 06 09 2B 85 10 86 48 64 02 01 03"
                + "66 0C 06 0A 2B 06 01 04 01 2A 02 6E 01 02");

        CardRecognitionData data = new CardRecognitionData(crd);
        System.out.println(data);
        assertEquals("2.1.1", data.getGpVersion());
        assertEquals(2, data.getScpVersion());
        assertEquals(0x15, data.getScpIValue());
    }

}
