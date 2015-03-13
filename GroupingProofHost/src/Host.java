import java.io.*;
import java.util.*;
import gnu.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class Host {
	static ByteArrayOutputStream boas;
	final static byte TEST = 0x13;
	final static byte SETOPT = 0x14;
	final static byte REQA = 0x10;
	final static byte PROVEGROUP = 0x24; // 94
	final static byte SELECTGROUP = 0x28;
	final static byte ERR_GRP_ID = 0x01; // Collision with Groupid
	final static byte ERR_GRP_SEQ = 0x02;// Collision with Sequence number
	final static byte ERR_GRP_INVALID = 0x03; // Group has been tested to be
												// invalid
	final static byte GRP_FOUND = 0x40; // Return groupid and seqence number
	final static byte GRP_VALID = 0x50; // Group has been tested to be valid
	static Enumeration portList;
	static CommPortIdentifier portId;
	static SerialPort serialPort;
	static OutputStream outputStream;
	static InputStream inputStream;
	static boolean outputBufferEmptyFlag = false;

	private static String queryUrl(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;

		try {
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				entity.writeTo(baos);
				String result = baos.toString();
				return result;
			}
		} catch (Exception e) {

		} finally {
		}

		return null;
	}

	public static void main(String[] args) {
		boas = new ByteArrayOutputStream();
		boolean portFound = false;
		String defaultPort = "/dev/ttyUSB0";

		if (args.length > 0) {
			defaultPort = args[0];
		}

		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();

			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				if (portId.getName().equals(defaultPort)) {
					System.out.println("Found port " + defaultPort);

					portFound = true;

					try {
						serialPort = (SerialPort) portId.open("SimpleWrite",
								2000);
					} catch (PortInUseException e) {
						System.out.println("Port in use.");

						continue;
					}

					try {
						outputStream = serialPort.getOutputStream();
						inputStream = serialPort.getInputStream();
					} catch (IOException e) {
					}

					try {
						serialPort.setSerialPortParams(9600,
								SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
					} catch (UnsupportedCommOperationException e) {
						System.err.print(e.getMessage());
					}

					try {
						serialPort.notifyOnOutputEmpty(true);
						serialPort.notifyOnDataAvailable(true);
					} catch (Exception e) {
						System.out.println("Error setting event notification");
						System.out.println(e.toString());
						System.exit(-1);
					}

					boas.write(SETOPT);// TEST);//SELECTGROUP);
					boas.write(0xF4); // test didi
					boas.write(0x0A); // test ana
					boas.write(0x9); // delayc
					boas.write(0xFF); // rxthreashold

					try {
						outputStream.write(boas.toByteArray());
					} catch (IOException e) {
						System.err.print(e.getMessage());
					}
					// boas.close();
					boas = new ByteArrayOutputStream();

					// boas.write(GETUID);
					byte command = SELECTGROUP;
					//byte command = REQA;// REQA;
					boas.write(command);// TEST);//SELECTGROUP);
					try {
						outputStream.write(boas.toByteArray());
					} catch (IOException e) {
						System.err.print(e.getMessage());
					}

					try {
						// serialPort.wait(2000);
						// /serialPort.wait(2000);
						// Thread.sleep(4000);
						char c;
						System.out.println("Response: ");
						int i = 0;
						/*
						 * while ((c = (char) inputStream.read()) >= 0) { if
						 * (c== 0xf0) { System.out.print(1); } else if (c==0x0f)
						 * { System.out.print(0); } if (i%20 == 1) {
						 * System.out.println(""); } i++; }
						 */
						if (command == SELECTGROUP) {
							while (i < 50 && (c = (char) inputStream.read()) != 0x99) {
								System.out.print(Integer.toHexString(c) + " ");
							}
						} else if (command == REQA) {
							while (i < 13
									&& (c = (char) inputStream.read()) >= 0) {
								if (i < 5) {
									System.out.print(Integer.toHexString(c));
								} else if (i == 5) {
									System.out.println(Integer.toHexString(c));
								} else if (i == 12) {
									//
								} else if (i > 5) {
									System.out.print(Integer.toBinaryString(c)
											+ " ");
								}
								i++;
							}
						}
						System.out.println("");
						System.out.println("Finished ");
						/*
						 * String response = queryUrl(
						 * "http://project..com/verify/provegroup.php?g=30&s=1&v=MTU0"
						 * ); //Return group info, m, m2, rand VerifierTriplet
						 * trip = new VerifierTriplet();
						 * trip.parseJSON(response);
						 * 
						 * boas.write(PROVEGROUP); // Prove group command
						 * boas.write(trip.rand); boas.write(trip.m);
						 * boas.write(trip.m2);
						 * outputStream.write(boas.toByteArray
						 * ());serialPort.wait(2000);
						 * 
						 * serialPort.wait(2000); // f2 and verification done on
						 * reader. while ((c = (char) inputStream.read()) > 0) {
						 * System.out.print(c); }
						 */
					} catch (Exception e) {
						e.printStackTrace();
					}

					serialPort.close();
					System.exit(1);
				}
			}
		}

		if (!portFound) {
			System.out.println("port " + defaultPort + " not found.");
		}
	}

}
