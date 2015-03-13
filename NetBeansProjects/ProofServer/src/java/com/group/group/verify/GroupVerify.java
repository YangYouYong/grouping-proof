/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.group.group.verify;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import com.group.utilities.Utilities;
import java.util.Random;
import org.apache.tomcat.jni.Time;

/**
 *
 * @author 
 */
@WebService(serviceName = "GroupVerify")
public class GroupVerify {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "generatetoken")
    public String generatetoken(@WebParam(name = "groupid") int groupid) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        StringBuilder returnstr = new StringBuilder();
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception ex) {
                
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost/orderproof?user=groupingproof&password=Kxa7aYeqDTRTrTTz");
            stmt = conn.createStatement();
            if (stmt.execute("SELECT * FROM orderproof.group WHERE groupid = '" + groupid + "'")) {
                rs = stmt.getResultSet();
                rs.first();
                String state = rs.getString("groupstate");
                String key = rs.getString("groupkey");
                Random rand = new Random();
                byte[] token = new byte[4];
                rand.nextBytes(token);
                returnstr.append(Utilities.hexStringToByteArray(state.substring(0, 4)));
                returnstr.append(Utilities.XorArray(token, Utilities.hexStringToByteArray(key.substring(0, 4))));
                returnstr.append(Utilities.XorArray(Utilities.hexStringToByteArray(state.substring(4, 8)), Utilities.hexStringToByteArray(key.substring(4, 8))));
                stmt.executeUpdate("INSERT INTO orderproof.proof (groupid, groupkey, groupstate, timestart, timeend, valid, rand) VALUES ('" + groupid + "','" + key + "','" + state + "','" + Time.now() + "',NULL,NULL,'" + Utilities.asHex(token) + "')");
                if (stmt.execute("SELECT * FROM orderproof.proof WHERE proofid = LAST_INSERT_ID()")) {
                    rs = stmt.getResultSet();
                    rs.first();
                    returnstr.append(rs.getString("proofid"));
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
        
        return returnstr.toString();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "verifyproof")
    public String verifyproof(@WebParam(name = "proofid") int proofid, @WebParam(name = "proof") String proof) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        StringBuilder returnstr = new StringBuilder();
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception ex) {
                
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost/orderproof?user=groupingproof&password=Kxa7aYeqDTRTrTTz");
            stmt = conn.createStatement();
            if (stmt.execute("SELECT * FROM orderproof.proof WHERE proofid = '" + proofid + "'")) {
                rs = stmt.getResultSet();
                rs.first();
                int groupid = rs.getInt("groupip");
                String state = rs.getString("groupstate");
                String key = rs.getString("groupkey");
                byte[] token = Utilities.hexStringToByteArray(rs.getString("rand"));
                byte[] expectedproof = new byte[16];
                if (stmt.execute("SELECT * FROM orderproof.products WHERE groupid = '" + groupid + "'")) {
                    rs = stmt.getResultSet();
                    while (rs.next())
                    {
                        String tagkey = rs.getString("tagkey");
                        Utilities.XorArray(expectedproof, Utilities.CalculateResponse(token, Utilities.hexStringToByteArray(tagkey)));
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
        
        return returnstr.toString();
    }
}
