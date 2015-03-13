/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.group.group.create;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Random;

/**
 *
 * @author 
 */
@WebService(serviceName = "GroupCreate")
public class GroupCreate {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "createnewgroup")
    public String createnewgroup() {
        //TODO write your implementation code here:

        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        String newgroupid = "";
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception ex) {
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost/orderproof?user=groupingproof&password=Kxa7aYeqDTRTrTTz");
            stmt = conn.createStatement();
            Random rand = new Random();
            byte[] newkey = new byte[8];
            rand.nextBytes(newkey);
            String newkeyhex = asHex(newkey);
            stmt.executeUpdate("INSERT INTO orderproof.group (groupkey,locked) VALUES ('" + newkeyhex + "','0')");
            if (stmt.execute("SELECT * FROM orderproof.group WHERE groupid = LAST_INSERT_ID()")) {
                rs = stmt.getResultSet();
                rs.first();
                //newgroupid = "<groupid>" + Integer.toString(rs.getInt("groupid")) + "</groupid>" + "<groupkey>" + rs.getString("groupkey") + "</groupkey>";
                newgroupid = Integer.toString(rs.getInt("groupid")) + ":" + rs.getString("groupkey");
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }
                stmt = null;
            }
        }

        return newgroupid;
    }
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "addproduct")
    public String addproduct(@WebParam(name = "groupid") int groupid, @WebParam(name = "productepc") String productepc) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        String returnstring = "";
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception ex) {
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost/orderproof?user=groupingproof&password=Kxa7aYeqDTRTrTTz");
            stmt = conn.createStatement();
            if (stmt.executeUpdate("SELECT COUNT(*) FROM orderproof.group WHERE groupid = '" + groupid + "' AND locked = '0'") == 1) {
                if (stmt.execute("SELECT * FROM orderproof.product WHERE epc = '" + productepc + "'")) {
                    rs = stmt.getResultSet();
                    rs.first();
                    if (rs.getInt("groupid") == 0) {
                        stmt.executeUpdate("UPDATE orderproof.product SET groupid = '" + groupid + "' WHERE epc = '" + productepc + "'");
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }
                stmt = null;
            }
        }

        return returnstring;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "completegroup")
    public String completegroup(@WebParam(name = "groupid") String groupid) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        String returnstring = "";
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception ex) {
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost/orderproof?user=groupingproof&password=Kxa7aYeqDTRTrTTz");
            stmt = conn.createStatement();
            if (stmt.executeUpdate("SELECT COUNT(*) FROM orderproof.group WHERE groupid = '" + groupid + "' AND locked = '0'") == 1) {
                stmt.executeUpdate("UPDATE orderproof.group SET locked = '1' WHERE groupid = '" + groupid + "'");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }
                stmt = null;
            }
        }

        return returnstring;
    }
}
