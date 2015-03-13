/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package yokingtest;
import java.util.Hashtable;
import java.util.Random;
import java.util.ArrayList;
import java.security.*;
import java.util.List;

/**
 *
 * @author 
 */
public class Main {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        TagFactory tagFactory = new TagFactory();
        List<Tag> tags = new ArrayList();
        Reader reader = new Reader();
        reader.CreateProof(tags);
    }

}
