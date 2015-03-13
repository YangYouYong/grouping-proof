/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classicapplet1;

import javacard.framework.*;

public class LightweightTag extends Applet {

    public final static byte CLA = (byte) 0x90;
    public final static byte INITTAG = (byte) 0x30;
    public final static byte REPAIRSTATE = (byte) 0x31;
    public final static byte SELECTGROUP = (byte) 0x20;
    public final static byte SELECTTAGGROUP = (byte) 0x21;
    public final static byte GENPROOF = (byte) 0x40;
    public final static short GROUPKEY1SIZE = (short) 4;
    public final static short GROUPSTATESIZE = (short) 2;
    public final static short GROUPSTATEKEY2SIZE = (short) 2;
    public final static short RANDOM1SIZE = (short) 2;
    public final static short RANDOM2SIZE = (short) 2;
    public byte[] groupstate; // 4 bytes
    public byte[] key; // 2 bytes
    public byte[] groupkey; // 6 bytes
    public byte[] groupid;
    public byte[] functiontrap; // 6 bytes
    public byte[] EPC; // 12 bytes

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
        new LightweightTag();
    }

    /**
     * Only this class's install method should create the applet object.
     */
    protected LightweightTag() {
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
        byte buffer[] = apdu.getBuffer();

        if (buffer[ISO7816.OFFSET_CLA] == CLA) {
            switch (buffer[ISO7816.OFFSET_INS]) {
                case INITTAG:
                    break;
                case SELECTGROUP:
                    if (Util.arrayCompare(buffer, ISO7816.OFFSET_CDATA, groupid, (short) 0, (short) groupid.length) == 0) // arraycompare
                    {
                        send(apdu, groupstate, (short)0, GROUPSTATESIZE);
                    } // halt card;
                    break;
                case GENPROOF:
                    byte[] state = new byte[GROUPSTATEKEY2SIZE];
                    Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, state, (short) 0, GROUPSTATEKEY2SIZE);
                    XorArray(state, groupkey, (short) 0, GROUPKEY1SIZE, GROUPSTATEKEY2SIZE);
                    if (Util.arrayCompare(state, (short) 0, groupstate, GROUPKEY1SIZE, GROUPSTATEKEY2SIZE) == 1) {
                        byte[] random = new byte[GROUPKEY1SIZE];
                        Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, random, (short) 0, GROUPKEY1SIZE);
                        XorArray(random, groupkey, (short) 0, (short) 0, RANDOM1SIZE);
                        UpdateKeysandState(random, GROUPKEY1SIZE, RANDOM2SIZE);
                        XorArray(random, key, (short) 0, (short) 0, RANDOM1SIZE);
                        send(apdu, random, (short) 0, GROUPKEY1SIZE);
                    }
                    break;
                default:
                    ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        }
    }

    private void send(APDU apdu, byte[] bytes, short offset, short length) {
        byte buffer[] = apdu.getBuffer();
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) (3));
        apdu.sendBytes((short) 0, (short) 3);
        apdu.sendBytesLong(bytes, offset, length);
    }

    public byte[] XorArray(byte[] arrone, byte[] arrtwo, short offsetone, short offset2, short length) {
        if (arrone.length == arrtwo.length) {
            byte[] arrreturn = new byte[arrone.length];
            for (int i = 0; i < arrreturn.length; i++) {
                arrreturn[i] = (byte) (arrone[i + offsetone] ^ arrtwo[i + offset2]);
            }
            return arrreturn;
        }
        return null;
    }
    
    public void UpdateKeysandState(byte[] random, short offset, short length)
    {
        function(random, groupkey);
        function(random, key);
        function(random, groupstate);
    }
    
    public void function(byte[] a, byte[] b)
    {
        if (a.length >= b.length && functiontrap.length >= b.length)
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte)((a[i] & b[i]) ^ functiontrap[i]);
        }
    }
}
