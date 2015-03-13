/*
 * MifareApp.java
 */

package mifare;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import javax.smartcardio.*;
import java.nio.ByteBuffer;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
/**
 * The main class of the application.
 */
public class MifareApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new MifareView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of MifareApp
     */
    public static MifareApp getApplication() {
        return Application.getInstance(MifareApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        CardTerminals terminalList;
        TerminalFactory factory = TerminalFactory.getDefault();
        terminalList = factory.terminals();
        boolean READMODE = false;
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

                byte[] KEY = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
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

                if (READMODE)
                {
                    READMODE = false;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(752);

                    for (int i = 1; i <64; i++)
                    {
                        if ((i+1) % 4 == 0)
                        {
                            if (i+1 < 64)
                            {
                                AUTHENTICATE[7] = (byte)(i+1);
                                GetData = new CommandAPDU(AUTHENTICATE);
                                CardApduResponse = channel.transmit(GetData);
                                System.err.println(CardApduResponse.toString());
                            }
                        } else {
                            GetData = new CommandAPDU(0xFF, 0xB0, 0x00, i, 0x10);
                            CardApduResponse = channel.transmit(GetData);
                            System.err.println(CardApduResponse.toString());
                            baos.write(CardApduResponse.getData());
                        }
                    }

                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    BufferedReader br = new BufferedReader(new InputStreamReader(bais));

                    String url = br.readLine();
                    System.out.println(url);
                    if (url.compareTo("") != 0)
                    {
                        String firefox = "C:\\Program Files (x86)\\Mozilla Firefox 4.0 Beta 6\\firefox.exe";
                        String cmd = firefox + " " + url;
                        Process p = Runtime.getRuntime().exec(cmd);
                        p.waitFor();
                    }

                } else {
                    READMODE = true;
                    launch(MifareApp.class, args);
                    WaitForInput();
                    int length = outstring.length();
                    int pos = 0;
                    byte[] DATA = new byte[21];
                    DATA[0] = (byte)0xFF;
                    DATA[1] = (byte)0xD6;
                    DATA[2] = (byte)0x00;
                    DATA[3] = (byte)0x00;
                    DATA[4] = (byte)0x10;

                    for (int i = 1; i<64; i++)
                    {
                        if ((i+1) % 4 == 0)
                        {
                            if (i+1 < 64)
                            {
                                AUTHENTICATE[7] = (byte)(i+1);
                                GetData = new CommandAPDU(AUTHENTICATE);
                                CardApduResponse = channel.transmit(GetData);
                                System.err.println(CardApduResponse.toString());
                            }
                        } else {
                            DATA[3] = (byte)i;
                            
                            for (int j = 5; j < 21; j++)
                            {
                                if (pos >= length)
                                {
                                    DATA[j] = 0;
                                } else {
                                    DATA[j] = (byte)outstring.charAt(pos++);
                                }
                            }

                            GetData = new CommandAPDU(DATA);
                            CardApduResponse = channel.transmit(GetData);
                            System.err.println(CardApduResponse.toString());
                        }
                    }
                }

                card.disconnect(true);
                MyReader.waitForCardAbsent(0);
                Toolkit.getDefaultToolkit().beep();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public static String outstring = "http://www.test.com/\n";

    public void UpdateString(String update) {
        outstring = update + "\n";
        WaitForInput();
    }
    public static Boolean sync = false;
    public static final Object monitor = new Object();
    public static void WaitForInput()
    {
        
        try
        {
            synchronized (monitor) {
                if (sync)
                {
                    sync = false;
                    monitor.notifyAll();
                } else {
                    sync = true;
                    monitor.wait();
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}

