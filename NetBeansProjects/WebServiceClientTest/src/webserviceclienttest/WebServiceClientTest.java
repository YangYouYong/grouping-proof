/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webserviceclienttest;

/**
 *
 * @author 
 */
public class WebServiceClientTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int groupid = Integer.parseInt(createnewgroup().split(":")[0]);
        addproduct(groupid,registernewproduct().split(":")[0]);
        addproduct(groupid,registernewproduct().split(":")[0]);
        addproduct(groupid,registernewproduct().split(":")[0]);
    }

    private static String createnewgroup() {
        webserviceclienttest.GroupCreate_Service service = new webserviceclienttest.GroupCreate_Service();
        webserviceclienttest.GroupCreate port = service.getGroupCreatePort();
        return port.createnewgroup();
    }

    private static String addproduct(int groupid, java.lang.String productepc) {
        webserviceclienttest.GroupCreate_Service service = new webserviceclienttest.GroupCreate_Service();
        webserviceclienttest.GroupCreate port = service.getGroupCreatePort();
        return port.addproduct(groupid, productepc);
    }

    private static String registernewproduct() {
        webserviceclienttest.productregister.ProductRegister_Service service = new webserviceclienttest.productregister.ProductRegister_Service();
        webserviceclienttest.productregister.ProductRegister port = service.getProductRegisterPort();
        return port.registernewproduct();
    }

}
