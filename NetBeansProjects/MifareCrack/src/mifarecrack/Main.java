/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mifarecrack;
import javax.smartcardio.*;
//import javax.

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CardTerminals terminalList;
        TerminalFactory factory = TerminalFactory.getDefault();
        terminalList = factory.terminals();
        
        try
        {
            CardTerminal MyReader = terminalList.list().get(0);

            while(true)
            {
                MyReader.waitForCardPresent(0);
                //MyReader.
                Card card = MyReader.connect("*");

                CardChannel channel = card.getBasicChannel();
                CommandAPDU GetData;
                ResponseAPDU CardApduResponse;

                byte[] KEY = {(byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                              (byte) 0x26};

                GetData = new CommandAPDU(KEY);
                CardApduResponse = channel.transmit(GetData);
                System.err.println(CardApduResponse.toString());

                /*byte[] KEY = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                              (byte) 0x64, (byte) 0xE1, (byte) 0xC2,
                              (byte) 0x46, (byte) 0x19, (byte) 0x91};

                GetData = new CommandAPDU(KEY);
                CardApduResponse = channel.transmit(GetData);
                System.err.println(CardApduResponse.toString());

                byte[] AUTHENTICATE = {(byte) 0xFF, (byte) 0x86, (byte) 0x00,
                                       (byte) 0x00, (byte) 0x05, (byte) 0x01,
                                       (byte) 0x00, (byte) 0x00,
                                       (byte) 0x60, (byte) 0x00};

                GetData = new CommandAPDU(AUTHENTICATE);
                CardApduResponse = channel.transmit(GetData);
                System.err.println(CardApduResponse.toString());

                GetData = new CommandAPDU(0xFF, 0xB0, 0x00, 0x03, 0x10);
                CardApduResponse = channel.transmit(GetData);
                System.err.println(CardApduResponse.toString());
                byte[] bytes = CardApduResponse.getData();
                System.err.println(asHex(bytes));
                


                card.disconnect(true);*/
                MyReader.waitForCardAbsent(0);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public static String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }
}
