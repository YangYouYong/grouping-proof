/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package groupingproofcardregister;
import javax.smartcardio.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Random;

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

                Card card = MyReader.connect("*");

                CardChannel channel = card.getBasicChannel();
                CommandAPDU GetData;
                ResponseAPDU CardApduResponse;

                byte[] GETUID = {(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x07};

                GetData = new CommandAPDU(GETUID);
                CardApduResponse = channel.transmit(GetData);
                byte[] UID = CardApduResponse.getData();
                String uidhex = asHex(UID);
                System.err.println(CardApduResponse.toString());

                Statement stmt = null;
                ResultSet rs = null;
                Connection conn = null;
                try {

                conn =
                DriverManager.getConnection("jdbc:mysql://localhost/groupingproof?" +
                                   "user=groupingproof&password=Kxa7aYeqDTRTrTTz");
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM tags WHERE UID = '" + uidhex +"'");

                if (false) // Already Exists
                {
                    card.disconnect(true);
                    MyReader.waitForCardAbsent(0);
                    continue;
                } else {
                    Random rand = new Random();
                    byte[] newkey = new byte[8];
                    rand.nextBytes(newkey);
                    String newkeyhex = asHex(newkey);
                    stmt.executeUpdate("INSERT INTO tags (UID,ENCKEY) VALUES ('" + uidhex +"','" + newkeyhex +"')");

                    /*byte[] KEY = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                              (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                              (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

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
                    byte[] SETKEY = {(byte) 0xFF, (byte) 0xD6, (byte) 0x00,
                                       (byte) 0x03, (byte) 0x10,
                                       newkey[0], newkey[1],newkey[2],newkey[3],newkey[4],newkey[5],
                                       (byte)0xFF, (byte)0x07,(byte)0x80,(byte)0x69,
                                       newkey[0], newkey[1],newkey[2],newkey[3],newkey[4],newkey[5],
                                       };

                    GetData = new CommandAPDU(SETKEY);
                    CardApduResponse = channel.transmit(GetData);
                    System.err.println(CardApduResponse.toString());*/
                    //00 00 0D 87 00 00 00 08 58 43 44 45 46 52 47 45 8E

                     byte[] SETKEY = {(byte) 0x87,
                                       (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
                                       newkey[0], newkey[1],newkey[2],newkey[3],newkey[4],newkey[5],newkey[6],newkey[7]
                                       };

                    GetData = new CommandAPDU(SETKEY);
                    CardApduResponse = channel.transmit(GetData);
                    System.err.println(CardApduResponse.toString());

                }
    // or alternatively, if you don't know ahead of time that
    // the query will be a SELECT...

                //if (stmt.execute("SELECT foo FROM bar")) {
                //rs = stmt.getResultSet();
                //}//

            // Now do something with the ResultSet ....
                }
                catch (SQLException ex){
                    // handle any errors
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }
                finally {
                    // it is a good idea to release
                // resources in a finally{} block
    // in reverse-order of their creation
    // if they are no-longer needed

                    if (rs != null) {
                        try {
                        rs.close();
                        } catch (SQLException sqlEx) { } // ignore

                        rs = null;
                    }

                    if (stmt != null) {
                    try {
                            stmt.close();
                        } catch (SQLException sqlEx) { } // ignore

                        stmt = null;
                    }
                }

                card.disconnect(true);
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
