/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.group.rfid;

import javacard.framework.*;
import javacard.security.SecretKey;

/**
 *
 * @author 
 */
public class HeavyToken extends Applet {

    public final static byte USERCLA = (byte) 0x90;
    public final static byte ADMINCLA = (byte) 0x91;
    public final static byte INITTAG = (byte) 0x00;
    public final static byte GETEPC = (byte) 0x00;
    public final static byte SELECTGROUP = (byte) 0x01;
    public final static byte AUTHENTICATETAG = (byte) 0x02;
    public final static byte GROUPINGPROOF = (byte) 0x03;
    SecretKey adminKey;
    byte[] tagKey;
    byte[] silenceKey;
    byte[] groupKey;
    byte[] epc;
    byte[] groupid;
    short counter;
    short sequence;
    boolean silenced;

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
        new HeavyToken();
    }

    /**
     * Only this class's install method should create the applet object.
     */
    protected HeavyToken() {
        register();
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
                    break;
                default:
                    ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        } else if (buffer[ISO7816.OFFSET_CLA] == USERCLA) {
            switch (buffer[ISO7816.OFFSET_INS]) {
                case SELECTGROUP:
                    if (Util.arrayCompare(buffer, ISO7816.OFFSET_CDATA, groupid, (short) 0, (short) groupid.length) == 0) // arraycompare
                    {
                        //send(apdu, groupstate, (short)0, GROUPSTATESIZE);
                    } else {
                        
                    }
                    break;
                case GETEPC:
                    send(apdu, epc, (short)0, (short)0, buffer);
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
        apdu.setOutgoingLength((short) (3));
        apdu.sendBytes((short) 0, (short) 3);
        apdu.sendBytesLong(bytes, offset, length);
    }
    
}
