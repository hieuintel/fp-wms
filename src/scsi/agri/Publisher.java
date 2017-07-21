/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.agri;

import it.geosolutions.geoserver.rest.*;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroupList;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerGroupEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSImageMosaicEncoder;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import static scsi.agri.Main.appendToPane;
import static scsi.agri.Main.getCurrentTimeStamp;

/**
 *
 * @author HieuIntel
 */
public class Publisher {

    /**
     * @param args the command line arguments
     */
    //public static String RESTURL = "http://192.168.10.143:8080/geoserver";
    public static String RESTURL = "http://165.132.139.249:8080/geoserver";
    //public static String RESTURL = "http://165.132.139.249:8081/geoserver";
    public static String RESTUSER = "admin";
    public static String RESTPW = "geoserver";

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
        // TODO code application logic here
//        for (int i = 0; i < 14; i++) {
//            int year = 2000 + i;
//            String y = String.valueOf(year);
//            publish_rasterLayerfromPostGIS(y);
//        }

//        publish_rasterLayerfromPostGIS("2014");
        publish_rasterLayerfromPostGISUImode("E:\\XMLTest");
    }

    public static void publish_rasterLayerfromPostGIS(String dbname) throws MalformedURLException, FileNotFoundException {

        GeoServerRESTReader reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
        //Check worskspace
        boolean exists = reader.existsWorkspace(dbname);
        if (!exists) {
            //Create worskspace
            boolean created = publisher.createWorkspace(dbname);
            if (created) {
                //Publish all raster in database to GeoServer
                publish_layers(reader, publisher, dbname);
            }
        } else {
            //Publish all raster in database to GeoServer
            publish_layers(reader, publisher, dbname);
        }
    }

    public static void publish_layers(GeoServerRESTReader reader, GeoServerRESTPublisher publisher, String dbname)
            throws FileNotFoundException {
        String path = GenerateXMLConfig.path_storeXMLconfig + dbname;
        List<File> listfile = scsi.file.Core.get_FileinFolder(path);

        for (int i = 0; i < listfile.size(); i++) {
            String storeName = scsi.file.Core.get_fileNameWithOutExt(listfile.get(i));
            //Because of each store mosaicjdbc only contain one layer, check exist both of them
            if (reader.existsCoveragestore(dbname, storeName) || reader.existsLayer(dbname, storeName)) {
                System.out.println("Published layer " + storeName);
            } else {
                boolean pub = publisher.publishImageMosaicJDBC(dbname, storeName, listfile.get(i));
                if (pub) {
                    System.out.println("Published new layer " + storeName);
                } else {
                    System.out.println("Failed publish layer " + storeName);
                }
            }
        }
    }

    public static void publish_rasterLayerfromPostGISUImode(String XMLfolder) throws MalformedURLException, FileNotFoundException {
        File file = new File(XMLfolder);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        GeoServerRESTReader reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
        for (String directorie : directories) {
            if (isNumeric(directorie)) {
                //System.out.println(directories[i]);
                String dbname = directorie;
                //Check worskspace
                boolean exists = reader.existsWorkspace(dbname);
                if (!exists) {
                    //Create worskspace
                    boolean created = publisher.createWorkspace(dbname);
                    if (created) {
                        //Publish all raster in database to GeoServer
                        publish_layersUImode(reader, publisher, dbname, XMLfolder);
                    }
                } else {
                    //Publish all raster in database to GeoServer
                    publish_layersUImode(reader, publisher, dbname, XMLfolder);
                }
            }
        }
    }

    public static void publish_layersUImode(GeoServerRESTReader reader, GeoServerRESTPublisher publisher, String dbname, String XMLfolder)
            throws FileNotFoundException {
        String path = XMLfolder + "\\" + dbname;
        List<File> listfile = scsi.file.Core.get_FileinFolder(path);

        for (int i = 0; i < listfile.size(); i++) {
            String storeName = scsi.file.Core.get_fileNameWithOutExt(listfile.get(i));
            //Because of each store mosaicjdbc only contain one layer, check exist both of them
            if (reader.existsCoveragestore(dbname, storeName) || reader.existsLayer(dbname, storeName)) {
                System.out.println("Layer " + storeName + " is existed");
                appendToPane(Main.tbPublishinfo, "Layer " + storeName + " " + getCurrentTimeStamp() + " is existed \n", Color.ORANGE);
            } else {
                boolean pub = publisher.publishImageMosaicJDBC(dbname, storeName, listfile.get(i));
                if (pub) {
                    System.out.println("Published layer " + storeName);
                    appendToPane(Main.tbPublishinfo, "Published new layer " + storeName + " " + getCurrentTimeStamp() + "\n", Color.BLACK);
                } else {
                    System.out.println("Could not publish layer " + storeName);
                    appendToPane(Main.tbPublishinfo, "Could not publish layer " + storeName + " " + getCurrentTimeStamp() + "\n", Color.RED);
                }
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
