/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.agri;
/**
 *
 * @author HieuIntel
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 *
 * @author HieuIntel
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static String GEOSERVERHOST = "165.132.139.249";
    public static String OSUSER = "geoserver";
    public static String OSPASS = "P@$$w0rd00";
    public static String GEOSERVERDATAPATH = "smb://" + GEOSERVERHOST +"/Data2/"; //Apply for GeoServer intall on Windows

    public static String TYPEGEOSERVERCONNECTION = "remote";

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, SmbException, IOException {
        // TODO code application logic here
        if (TYPEGEOSERVERCONNECTION == "remote") {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(GEOSERVERHOST, OSUSER, OSPASS);
            SmbFile dir = new SmbFile(GEOSERVERDATAPATH, auth);
            for (SmbFile f : dir.listFiles()) {
                System.out.println(f.getName());
            }
        } else if (TYPEGEOSERVERCONNECTION == "local") {

        }
    }

}
