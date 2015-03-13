/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupingproofcreate;

import javax.smartcardio.*;
import java.util.Random;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.*;
import com.google.zxing.common.BitMatrix;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        StringBuilder proof = new StringBuilder();
        CardTerminals terminalList;
        TerminalFactory factory = TerminalFactory.getDefault();
        terminalList = factory.terminals();

        try {
            CardTerminal MyReader = terminalList.list().get(0);
            int proofCount = 0;
            Random rand = new Random();
            byte[] challenge = new byte[8];
            byte[] previousHash = new byte[32];
            rand.nextBytes(challenge);
            proof.append(asHex(challenge));
            while (proofCount < 2) {
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
                proof.append(uidhex);
                System.out.println(uidhex);
                System.err.println(CardApduResponse.toString());

                if (proofCount == 0) {
                    byte[] CHALLENGE = {(byte) 0x88,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
                        challenge[0], challenge[1], challenge[2], challenge[3], challenge[4], challenge[5], challenge[6], challenge[7],
                        (byte) 0x20
                    };

                    GetData = new CommandAPDU(CHALLENGE);
                    CardApduResponse = channel.transmit(GetData);
                    System.err.println(CardApduResponse.toString());
                    previousHash = CardApduResponse.getData();
                    proofCount = 1;
                } else {
                    byte[] CHALLENGE2 = {(byte) 0x88,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
                        previousHash[0], previousHash[1], previousHash[2], previousHash[3], previousHash[4], previousHash[5], previousHash[6], previousHash[7],
                        previousHash[8], previousHash[9], previousHash[10], previousHash[11], previousHash[12], previousHash[13], previousHash[14], previousHash[15],
                        previousHash[16], previousHash[17], previousHash[18], previousHash[19], previousHash[20], previousHash[21], previousHash[22], previousHash[23],
                        previousHash[24], previousHash[25], previousHash[26], previousHash[27], previousHash[28], previousHash[29], previousHash[30], previousHash[31],
                        (byte) 0x20
                    };

                    GetData = new CommandAPDU(CHALLENGE2);
                    CardApduResponse = channel.transmit(GetData);
                    previousHash = CardApduResponse.getData();
                    System.err.println(CardApduResponse.toString());
                    proofCount = 2;
                }
                card.disconnect(true);
                if (proofCount < 2) {
                    System.err.println("Please remove card and place next one on");
                    MyReader.waitForCardAbsent(0);
                }
            }
            String readerproof = asHex(previousHash);
            proof.append(readerproof);
            //System.out.println(proof.toString());
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(proof.toString(), BarcodeFormat.QR_CODE, 200, 200);
            int width = matrix.getWidth();
            int height = matrix.getHeight();

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // retrieve image
            for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean black = matrix.get(x,y);
                bi.setRGB(x, y, black ? 0x000000 : 0xFFFFFF);
              }
            }
            
            File outputfile = new File("saved.png");
            ImageIO.write(bi, "png", outputfile);
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
}
