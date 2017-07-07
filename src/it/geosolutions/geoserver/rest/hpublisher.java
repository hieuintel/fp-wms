/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.decoder.RESTLayerGroupList;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerGroupEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSImageMosaicEncoder;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author HieuIntel
 */
public class hpublisher {

    /**
     * @param args the command line arguments
     */
    //public static String RESTURL = "http://192.168.10.132:8080/geoserver";
    public static String RESTURL = "http://165.132.138.68:8080/geoserver";
    public static String RESTUSER = "admin";
    public static String RESTPW = "geoserver";//"geoserver";

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
        // TODO code application logic here
        //publish_dbdata2layer_ndvi("ndvi_2000");
        for (int i = 10; i < 16; i++) {
            int year = 2000 + i;
            String y = String.valueOf(year);
            publish_dbdata2layer(y);
        }
       
    }

    public static void publish_dbdata2layer(String dbname) throws MalformedURLException, FileNotFoundException {

        GeoServerRESTReader reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
        //Check worskspace
        boolean exists = reader.existsWorkspace(dbname);
        if (!exists) {
            //Create worskspace
            boolean created = publisher.createWorkspace(dbname);
            if (created) {
                publish_layers(reader, publisher, dbname);
            }
        } else {
            publish_layers(reader, publisher, dbname);
        }
    }

    public static void publish_dbdata2layer_ndvi(String dbname) throws MalformedURLException, FileNotFoundException {

        GeoServerRESTReader reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
        String path = "C:/Agri/" + dbname;
        File[] directories = new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        for (int i = 0; i < directories.length; i++) {
            String ndviname = directories[i].getName();
            System.out.println(directories[i].getName());
            boolean exists = reader.existsWorkspace(dbname);
            if (!exists) {
                //Create worskspace
                boolean created = publisher.createWorkspace(dbname);
                if (created) {
                    exists = reader.existsWorkspace(ndviname);
                    if (!exists) {
                        //Create worskspace
                        created = publisher.createWorkspace(ndviname);
                        if (created) {
                            publish_NDVIlayers(reader, publisher, dbname, ndviname);
                        }
                    } else {
                        publish_NDVIlayers(reader, publisher, dbname, ndviname);
                    }
                }
            } else {
                exists = reader.existsWorkspace(ndviname);
                if (!exists) {
                    //Create worskspace
                    boolean created = publisher.createWorkspace(ndviname);
                    if (created) {
                        publish_NDVIlayers(reader, publisher, dbname, ndviname);
                    }
                } else {
                    publish_NDVIlayers(reader, publisher, dbname, ndviname);
                }
            }
        }

//        //Check worskspace
    }

    public static void publish_layers(GeoServerRESTReader reader, GeoServerRESTPublisher publisher, String dbname) 
            throws FileNotFoundException {
        String path = "C:/Agri/" + dbname;
        List<File> listfile = scsi.file.Core.get_FileinFolder(path);

        for (int i = 0; i < listfile.size(); i++) {
            String storeName = scsi.file.Core.get_fileNameWithOutExt(listfile.get(i));
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

    public static void publish_NDVIlayers(GeoServerRESTReader reader, GeoServerRESTPublisher publisher, String dbname, String ndviname) throws FileNotFoundException {
        String path = "C:/Agri/" + dbname + "/" + ndviname;
        List<File> listfile = scsi.file.Core.get_FileinFolder(path);
        GSLayerGroupEncoder encoder = new GSLayerGroupEncoder();
        encoder.setName(ndviname);
        encoder.setWorkspace(ndviname);
        final GSLayerEncoder layerEnc = new GSLayerEncoder();
        layerEnc.setDefaultStyle("ndvi");

        for (int i = 0; i < listfile.size(); i++) {
            String storeName = scsi.file.Core.get_fileNameWithOutExt(listfile.get(i));
            //boolean check = publisher.removeCoverageStore(dbname, name,true,GeoServerRESTPublisher.Purge.ALL);
            //System.out.println(check);
            try {
                String on = "<coverageName";
                String off = "/>";
                String xml = scsi.text.Core.read_TextFile(listfile.get(i).getPath());
                String result = xml.substring(xml.indexOf(on) + on.length(), xml.indexOf(off));
                String st[] = result.trim().split("=");
                String nameLayer = st[1].substring(1, st[1].length() - 1);

                if (reader.existsCoveragestore(ndviname, storeName) || reader.existsLayer(storeName, nameLayer)) {
                    System.out.println("Published sublayer " + ndviname + ":" + storeName);
                    if (publisher.configureLayer(ndviname, nameLayer, layerEnc)) {
                        System.out.println("Set style for sublayer " + ndviname + ":" + storeName);
                    }

                } else {
                    boolean pub = publisher.publishImageMosaicJDBC(ndviname, storeName, listfile.get(i));
                    if (pub) {
                        System.out.println("Created and Published sublayer " + ndviname + ":" + storeName);
                        if (publisher.configureLayer(ndviname, nameLayer, layerEnc)) {
                            System.out.println("Set style for sublayer " + ndviname + ":" + storeName);
                        }
                    } else {
                        System.out.println("Faile Published sublayer " + ndviname + ":" + storeName);
                    }
                }
                encoder.addLayer(ndviname + ":" + nameLayer);
                //System.out.println(n);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(hpublisher.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Thread.sleep(10);
        }
        //Publish group layer
        boolean exists = reader.existsCoveragestore(dbname, ndviname);
        if (!exists) {
            boolean pub = publisher.createLayerGroup(ndviname, ndviname, encoder);
            if (pub) {
                System.out.println("Published layer " + ndviname);
                publisher.configureLayer(dbname, ndviname, layerEnc);
            } else {
                System.out.println("Faile Published layer " + ndviname);
            }
        }

    }

    public static String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }

}
