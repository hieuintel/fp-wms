/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.db;

import java.sql.Connection;

/**
 *
 * @author HieuIntel
 */
public class Config {

    //SCSI PostgreSQL
    public static String hostPG = "165.132.139.131";
    public static String portPG = "5432";
    public static String userPG = "postgres";
    public static String pwPG = "P@$$w0rd00";
    public static String connectURLPG = "jdbc:postgresql://" + hostPG + ":" + portPG + "/";
    public static String defaultDBPG = "2000";
    
    
    //Test PostgreSQL
//    public static String hostPG = "165.132.138.68";
//    public static String portPG = "5432";
//    public static String userPG = "postgres";
//    public static String pwPG = "h";
//    public static String connectURLPG = "jdbc:postgresql://" + hostPG + ":" + portPG + "/";
//    public static String defaultDBPG = "db_test";
}
