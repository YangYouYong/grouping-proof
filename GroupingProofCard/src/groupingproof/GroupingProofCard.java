/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package groupingproof;

import javacard.framework.*;
import javacard.security.MessageDigest;

/**
 *
 * @author 
 */
public class GroupingProofCard extends Applet {
    //CLASS
    public final static byte USERCLA = (byte) 0x90;
    public final static byte ADMINCLA = (byte) 0x91;

    //INS
    public final static byte INITTAG = (byte) 0x00;
    public final static byte SETGROUP = (byte) 0x01;
    public final static byte GETEPC = (byte) 0x00;
    public final static byte SELECTGROUP = (byte) 0x01;
    public final static byte AUTHENTICATETAG = (byte) 0x02;
    public final static byte PROVEGROUP = (byte) 0x03;

    public final static byte UNINIT = (byte)0x00;
    public final static byte INIT = (byte)0x01;
    public final static byte GROUPSET = (byte)0x02;
    public final static byte GROUPSELECTED = (byte)0x03;
    public final static byte SILENCED = (byte)0x04;
    public final static byte CARDOVERFLOW = (byte)0x05;
    
    byte[] tagKey;
    //byte[] silenceKey;
    byte[] groupKey;
    byte[] epc;
    byte[] groupid;
    byte[] groupstate;
    byte[] sequence;

    byte[] rand;
    byte[] mdash;
    byte[] m;

    byte state;

    /**
     * Installs this applet.
     * 
     * @param bArray
     *            the array containing installation parameters
     * @param bOffset
     *            the starting offset in bArray
     * @param bLength
     *            the length in bytes of the parameter data in bArray
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new GroupingProofCard();
    }

    /**
     * Only this class's install method should create the applet object.
     */
    protected GroupingProofCard() {
        register();
        state = UNINIT;
        tagKey = new byte[4];
        groupKey = new byte[4];
        epc = new byte[12];
        groupid = new byte[4];
        groupstate = new byte[2];
        sequence = new byte[4];
    }

    /**
     * Processes an incoming APDU.
     * 
     * @see APDU
     * @param apdu
     *            the incoming APDU
     */
    public void process(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        
        if (buffer[ISO7816.OFFSET_CLA] == ADMINCLA) {
            switch (buffer[ISO7816.OFFSET_INS]) {
                case INITTAG:
                    if (state == UNINIT)
                    {
                        Util.arrayCopy(epc, (short)0, buffer, ISO7816.OFFSET_CDATA, (short)12);
                        Util.arrayCopy(tagKey, (short)0, buffer, ISO7816.OFFSET_CDATA, (short)4);
                    } else {
                        ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
                    }
                    break;
                case SETGROUP:
                    if (state == INIT)
                    {
                        // check tagkey
                        //Util.arrayCompare(tagKey, INIT, buffer, INIT, INIT)
                        //Initial state, group key, group id
                        Util.arrayCopy(groupstate, (short)0, buffer, ISO7816.OFFSET_CDATA, (short)2);
                        Util.arrayCopy(groupKey, (short)0, buffer, ISO7816.OFFSET_CDATA, (short)4);
                        Util.arrayCopy(groupid, (short)0, buffer, ISO7816.OFFSET_CDATA, (short)4);
                        sequence[0] = 0;
                        sequence[1] = 0;
                        sequence[2] = 0;
                        sequence[3] = 0;
                    } else {
                        ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
                    }
                    break;
                default:
                    ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        } else if (buffer[ISO7816.OFFSET_CLA] == USERCLA) {
            switch (buffer[ISO7816.OFFSET_INS]) {
                case PROVEGROUP:
                    if (state == GROUPSELECTED)
                    {
                    if (Util.arrayCompare(buffer, ISO7816.OFFSET_CDATA, groupid, (short) 0, (short) groupid.length) == 0) // arraycompare
                    {
                        state = GROUPSELECTED;

                        //function1() // Use half keep half
                        //function1()

                        //Util.arrayCompare(buffer, INIT, m, INIT, INIT);
                        function2(m);
                        increment(sequence);
                        //send(apdu, buffer, (short)0, GROUPSTATESIZE);
                    } else {

                    }
                    } else {
                        ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
                    }
                    break;
                case SELECTGROUP:
                    if (state == GROUPSET)
                    {
                    if (Util.arrayCompare(buffer, ISO7816.OFFSET_CDATA, groupid, (short) 0, (short) groupid.length) == 0) // arraycompare
                    {
                        state = GROUPSELECTED;
                        send(apdu, groupstate, (short)0, (short)2, buffer);
                    } else {

                    }
                    } else {
                        ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
                    }
                    break;
                case GETEPC:
                    if (state == INIT || state == GROUPSET || state == CARDOVERFLOW)
                    {
                        send(apdu, epc, (short)0, (short)0, buffer);
                    } else {
                        ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
                    }
                    break;
                default:
                    ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        } else {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
    }

    private void send(APDU apdu, byte[] bytes, short offset, short length, byte[] buffer) {
        apdu.setOutgoing();
        //apdu.setOutgoingLength((short) (3));
        //apdu.sendBytes((short) 0, (short) 3);
        apdu.sendBytesLong(bytes, offset, length);
    }


    private void function(byte[] rand)
    {
        MessageDigest md = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);
        //md.update(rand, sequence, sequence)
        md.doFinal(tagKey, sequence, sequence, epc, sequence);
    }

    private void increment(byte[] array)
    {
        array[0]++;
        for (int i=0; i< array.length; i++)
        {
            if (array[i] == 0)
            {
                if (i < array.length - 1)
                {
                    array[i+1]++;
                } else {
                    state = CARDOVERFLOW;
                    ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
                }
            }
        }
    }

    private void function2(byte[] m2)
    {

        //Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, state, (short) 0, GROUPSTATEKEY2SIZE);
        XorArray(groupstate, m2, (short) 0, (short)4, (short)4);
    }
}
