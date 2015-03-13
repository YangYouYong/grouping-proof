/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package yokingtest;
import java.security.*;
/**
 *
 * @author 
 */
public class Tag {
 private String key = "";
        private String ID = "";
        private int STATE = 0;
        private int COUNT = 0;
        private int LOCKED = 0;
        //void
        public String BeingProof(String time)
        {
            if (LOCKED > 0) return "";
            STATE = 1;
            COUNT++;
            if (COUNT == Integer.MAX_VALUE)
            {
                LOCKED = 1;
            }
            return Hash(key + time + COUNT);
        }

        //public String ReturnProof(String mac)
        //{
//            STATE = 2;
  //          return Hash(key + mac);
    //    }

        public String GetID()
        {
            return ID;
        }

        public Tag(String key, String ID)
        {
            this.key = key;
            this.ID = ID;
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
