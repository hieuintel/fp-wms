/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Module kết nối tới các hệ quản trị 
public class Connect {

    //Connect to PostgreSQL
    public static Connection getConnectPostgreSQL(String connectString, String userName, String passWord) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectString, userName, passWord);
            System.out.println("OK connecting to PostgreSQL");
        } catch (SQLException ex) {
            System.err.println("Can not connect to PostgreSQL");
        }
        return connection;
    }
    
    

}
