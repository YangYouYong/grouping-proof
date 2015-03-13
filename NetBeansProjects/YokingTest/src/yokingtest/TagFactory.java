/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package yokingtest;
import java.util.Hashtable;
import java.util.Random;
import java.security.*;
/**
 *
 * @author 
 */
public class TagFactory {
 private Hashtable tags = new Hashtable();
        private Random rand = new Random();
        private int lastID = 0;
        public Tag CreateTag()
        {
            String key = rand.nextLong() + "";
            String ID = ++lastID + "";
            Tag tag = new Tag(key, ID);
            tags.put(ID, key);
            return tag;
        }
        public boolean VerifyProof(String one, String two, String mac, String timestamp)
        {
            String keyA = (String)tags.get(one);
            String keyB = (String)tags.get(two);

            String hash1 = Hash(keyA + timestamp);
            String hash2 = Hash(keyB + timestamp);
            byte[] hash3 = ByteHash(keyA + hash2);
            byte[] hash4 = ByteHash(keyB + hash1);
            byte[] finalMac = new byte[hash3.length];
            for (int i=0; i< finalMac.length; i++)
            {
                finalMac[i] = (byte)(hash3[i]^hash4[i]);
            }
            String finalmac = new String(finalMac);
            if (finalmac == mac)
            {
                return true;
            }
            return false;
        }
        public String Hash(String tohash)
        {
            return new String(ByteHash(tohash));
        }
        public byte[] ByteHash(String tohash)
        {
            try
            {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] sha1hash;
                md.update(tohash.getBytes("iso-8859-1"), 0, tohash.length());
                sha1hash = md.digest();
                //return new String(sha1hash);
                return sha1hash;
            } catch (Exception e)
            {
                return null;
            }
            
        }
}
