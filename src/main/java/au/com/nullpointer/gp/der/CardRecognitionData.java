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

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERApplicationSpecific;

/**
 * @author shane
 * 
 */
public class CardRecognitionData extends CardData {

    /**
     * @throws DecodingException
     * 
     */
    public CardRecognitionData(byte[] encoded) throws DecodingException {
        super(getCardData(encoded));
    }

    private static byte[] getCardData(byte[] encoded) throws DecodingException {
        try {
            DERApplicationSpecific cardData = (DERApplicationSpecific) ASN1Object.fromByteArray(encoded);

            if (cardData.getApplicationTag() != CardData.TAG_CARD_DATA) {
                throw new DecodingException(CardData.TAG_CARD_DATA, cardData.getApplicationTag());
            }

            return cardData.getContents();
        } catch (IOException e) {
            throw new DecodingException("Unable to decode card recognition data", e);
        }
    }
}
