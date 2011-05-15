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
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DEREncodable;

/**
 * @author shane
 * 
 */
public class CardData {
    public static final int TAG_CARD_DATA = 6;
    public static final int TAG_CARD_RECOGNITION_DATA = 19;
    public final static ASN1ObjectIdentifier GP_OID = new ASN1ObjectIdentifier("1.2.840.114283");

    private String gpVersion;
    private int scpVersion;
    private int scpIValue;
    private String cardConfig;
    private String chip;

    public CardData(byte[] encoded) throws DecodingException {
        try {
            DERApplicationSpecific cardRecData = (DERApplicationSpecific) ASN1Sequence.fromByteArray(encoded);

            if (cardRecData.getApplicationTag() != TAG_CARD_RECOGNITION_DATA) {
                throw new DecodingException(TAG_CARD_RECOGNITION_DATA, cardRecData.getApplicationTag());
            }

            cardRecData.getDERObject();

            ASN1StreamParser parse = new ASN1StreamParser(cardRecData.getContents());

            DEREncodable der = null;
            while ((der = parse.readObject()) != null) {
                if (der instanceof ASN1ObjectIdentifier) {
                    if (!GP_OID.branch("1").equals(der)) {
                        throw new DecodingException("Not GlobalPlatform card recognition data: " + der);
                    }
                }

                if (der instanceof DERApplicationSpecific) {
                    DERApplicationSpecific as = (DERApplicationSpecific) der;

                    int tag = as.getApplicationTag();

                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) ASN1Object.fromByteArray(as.getContents());

                    switch (tag) {
                        case 0:
                            gpVersion = oid.getId().replace(GP_OID.branch("2").toString() + ".", "");
                            break;

                        case 3:
                            break;
                        case 4:
                            String[] vals = oid.getId().replace(GP_OID.branch("4").toString() + ".", "").split("\\.");
                            scpVersion = Integer.parseInt(vals[0]);
                            scpIValue = Integer.parseInt(vals[1]);
                            break;
                        case 5:
                            cardConfig = oid.getId();
                            break;
                        case 6:
                            chip = oid.getId();
                            break;

                        default:
                            throw new DecodingException("Unknow card recognition data tag: " + tag);
                    }
                }
            }
        } catch (IOException e) {
            throw new DecodingException("Unable to decode card recognition data", e);
        }
    }

    /**
     * @return the gpVersion
     */
    public String getGpVersion() {
        return gpVersion;
    }

    /**
     * @return the scpVersion
     */
    public int getScpVersion() {
        return scpVersion;
    }

    /**
     * @return the scpIValue
     */
    public int getScpIValue() {
        return scpIValue;
    }

    /**
     * @return the cardConfig
     */
    public String getCardConfig() {
        return cardConfig;
    }

    /**
     * @return the chip
     */
    public String getChip() {
        return chip;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CardRecognitionData [gpVersion=" + gpVersion + ", scpVersion=" + scpVersion + ", scpIValue=0x"
                + Integer.toHexString(scpIValue) + ", cardConfig=" + cardConfig + ", chip=" + chip + "]";
    }
}
