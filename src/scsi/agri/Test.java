/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.agri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

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
//            File fileSource = new File("E:\\Projects\\Yonsei\\Agriculture\\Deploy Argi\\Quick Help.txt");
//            SmbFile smbFileTarget = new SmbFile(GEOSERVERDATAPATH + "/Quick Help.txt", auth);
//            // input and output stream
//            FileInputStream fis = new FileInputStream(fileSource);
//            SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFileTarget);
//            // writing data
//            try {
//                // 16 kb
//                final byte[] b = new byte[16 * 1024];
//                int read = 0;
//                while ((read = fis.read(b, 0, b.length)) > 0) {
//                    smbfos.write(b, 0, read);
//                }
//            } finally {
//                fis.close();
//                smbfos.close();
//            }
        } else if (TYPEGEOSERVERCONNECTION == "local") {

        }
    }

}
