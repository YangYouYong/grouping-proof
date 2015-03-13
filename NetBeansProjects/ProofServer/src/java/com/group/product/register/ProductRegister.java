/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.group.product.register;

import com.group.utilities.Utilities;
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
@WebService(serviceName = "ProductRegister")
public class ProductRegister {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "registernewproduct")
    public String registernewproduct() {
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
            String newkeyhex = Utilities.asHex(newkey);
            stmt.executeUpdate("INSERT INTO orderproof.product (epc,tagkey) VALUES ('" + newkeyhex + "','" + newkeyhex + "')");
            if (stmt.execute("SELECT * FROM orderproof.product WHERE productid = LAST_INSERT_ID()")) {
                rs = stmt.getResultSet();
                rs.first();
                //newgroupid = "<epc>" + rs.getInt("epc") + "</epc><tagkey>" + rs.getInt("tagkey") + "</tagkey>";
                newgroupid = rs.getString("epc") + ":" + rs.getString("tagkey");
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
}
