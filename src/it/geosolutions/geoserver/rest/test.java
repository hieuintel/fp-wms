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
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HieuIntel
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {
        // TODO code application logic here
        String RESTURL = "http://192.168.10.132:8080/geoserver";
        String RESTUSER = "admin";
        String RESTPW = "geoserver";

        GeoServerRESTReader reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);

        GSLayerGroupEncoder encoder = new GSLayerGroupEncoder();
        encoder.setName("test_layergroup");
        encoder.setWorkspace("topp");
        encoder.addLayer("topp:tasmania_roads");
        encoder.addLayer("topp:tasmania_roads");
        encoder.addLayer("topp:tasmania_water_bodies");

        publisher.createLayerGroup("topp", "test_layergroup", encoder);

//        RESTLayerGroupList allGroups = reader.getLayerGroups();
//        RESTLayerList allLayer = reader.getLayers();
//        for (int i = 0; i < allLayer.size(); i++) {
//            System.out.println(allLayer.get(i).getName());
//        }
//       
//        String storeName = "testAuto";
//        String layerName = "resttestpg";
//        try {
//            //boolean pub = publisher.publishGeoTIFF("SCSI", storeName + "another", "layername", geotiff);
//            final GSLayerEncoder layerEnc = new GSLayerEncoder();
//            layerEnc.setDefaultStyle("raster");
//            
//            final GSImageMosaicEncoder coverageEnc = new GSImageMosaicEncoder();
//            //coverageEnc.setName("hieu");
//            //coverageEnc.setMaxAllowedTiles(Integer.MAX_VALUE);            
//            // xml file path = "country/dbname/"
//            boolean pub = publisher.publishImageMosaicJDBC("SCSI", storeName, new File("src/pg_raster/test.xml"));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
