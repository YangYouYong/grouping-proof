/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupingproofcardverify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Random;
import java.security.*;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Please Enter Proof:");
        byte[] bytes = new byte[108];
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            System.in.read(bytes);
            byte[] challenge = hexStringToByteArray(new String(bytes).substring(0, 16));
            String uid1hex = new String(bytes).substring(16, 30);
            String uid2hex = new String(bytes).substring(30, 44);
            String proof = new String(bytes).substring(44, 108);
            byte[] previoushash = new byte[32];
            
            try {
                conn =
                        DriverManager.getConnection("jdbc:mysql://localhost/groupingproof?"
                        + "user=groupingproof&password=Kxa7aYeqDTRTrTTz");
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM tags WHERE UID IN ('" + uid1hex + "', '" + uid2hex + "')");
                rs.next();

                byte[] key = hexStringToByteArray(rs.getString("ENCKEY"));
                byte[] hashvalue = new byte[16];
                System.arraycopy(challenge, 0, hashvalue, 0, 8);
                System.arraycopy(key, 0, hashvalue, 8, 8);
                previoushash = ByteHash(hashvalue);

                rs.next();

                key = hexStringToByteArray(rs.getString("ENCKEY"));
                hashvalue = new byte[40];
                System.arraycopy(previoushash, 0, hashvalue, 0, 32);
                System.arraycopy(key, 0, hashvalue, 32, 8);
                previoushash = ByteHash(hashvalue);

            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) {
                    } // ignore

                    rs = null;
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) {
                    } // ignore

                    stmt = null;
                }
            }
            String hexhash = asHex(previoushash);
            if (hexhash.compareTo(proof) == 0) {
                System.err.println("VALID PROOF");
            } else {
                System.err.println("INVALID PROOF");
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /* Code from http://stackoverflow.com/ */
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    public static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    /* Code from http://stackoverflow.com/ */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] ByteHash(byte[] tohash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] sha1hash;
            md.update(tohash, 0, tohash.length);
            sha1hash = md.digest();
            return sha1hash;
        } catch (Exception e) {
            return null;
        }

    }
}
