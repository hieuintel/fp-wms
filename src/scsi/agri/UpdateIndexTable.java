/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.agri;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import scsi.db.Config;
import scsi.db.Connect;
import static scsi.db.Core.execute_SQL;
import static scsi.db.Core.run_SQL;

/**
 *
 * @author HieuIntel
 */
public class UpdateIndexTable {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, SQLException {
        // TODO code application logic here
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        //Date date = formatter.parse("20000101");
        int countryid = 213;
        int year = 2000;
        //String type = "pre";
        String[] listtype = {"max","min","t2m","pre","sho"};
        //int[] listyear = {2000, 2001, 2002, 2003, 2004, 2005, 2006, 2008, 2009, 2011, 2012, 2013, 2014};
//        for (int y = 0; y < listyear.length; y++) {
//            update_IndexbyYearCountry(listyear[y], countryid, type);
//        }

        for (int t = 0; t < listtype.length; t++) {
            update_IndexbyYearCountry(2014, countryid, listtype[t]);
        }

//        for (int i = 0; i < 15; i++) {
//            year = 2000 + i;
//            update_IndexbyYearCountry(year, countryid, type);
//        }
//        update_IndexbyYearCountry(2002, 213, "t2m");
    }

    public static void update_IndexbyYearCountry(int year, int id_0, String type) throws ParseException, SQLException {
        String connectURLPostgreSQL = Config.connectURLPG + year;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        String connectURLIndextable = Config.connectURLPG + "db_Agri";
        Connection connectIndextable = Connect.getConnectPostgreSQL(connectURLIndextable, Config.userPG, Config.pwPG);

        List<Date> listdate = get_listDate(year + "0101", year + "1231");
        for (int i = 0; i < listdate.size(); i++) {
            Date date = listdate.get(i);
            System.out.println(formatter2.format(date) + "_" + type);
            String countryname = "korea";
            if (id_0 == 213) {
                countryname = "korea";
            }
            if (id_0 == 49) {
                countryname = "china";
            }
            if (id_0 == 244) {
                countryname = "usa";
            }
            //Compute value by input parameter
            String sql = "select id_1 from tbl_province where id_0='" + id_0 + "' ORDER BY id_1";
            ResultSet res = run_SQL(connectIndextable, sql);
            while (res.next()) {
                int id_1 = res.getInt("id_1");
                //System.out.println(id_1);
                String sql_getvalue = " SELECT "
                        + " Min((gv).val) As Min,"
                        + " Max((gv).val) As Max,"
                        + " Sum(\"public\".ST_Area((gv).geom) * (gv).val) / Sum(\"public\".ST_Area((gv).geom)) as Mean"
                        + " FROM ("
                        + " SELECT \"public\".ST_DumpAsPolygons(\"public\".ST_Clip(" + countryname + ".\"" + formatter.format(date) + "_" + type + "\".rast, 1, tbl_province.geom, true)) AS gv"
                        + " FROM tbl_province," + countryname + ".\"" + formatter.format(date) + "_" + type + "\""
                        + " WHERE  \"public\".tbl_province.id_0=" + id_0 + " and \"public\".tbl_province.id_1=" + id_1 + ") AS foo ";
                try {
                    ResultSet rescheck = run_SQL(connectPostgreSQL, sql_getvalue);
                    while (rescheck.next()) {
                        Double max = rescheck.getDouble("Max");
                        Double min = rescheck.getDouble("Min");
                        Double mean = rescheck.getDouble("Mean");
                        String code = formatter.format(date) + type + id_0 + id_1;
                        String sql_insert = "INSERT INTO tbl_index (code,id_0, id_1, date,type,min,max,mean) "
                                + " VALUES ('" + code + "','" + id_0 + "','" + id_1 + "','" + formatter2.format(date) + "','" + type + "','" + min + "','" + max + "','" + mean + "')"
                                + " ON CONFLICT (code) DO UPDATE "
                                + " SET min = '" + min + "', "
                                + " max = '" + max + "', "
                                + " mean = '" + mean + "'";
                        //System.out.println(sql_insert);
                        execute_SQL(connectIndextable, sql_insert);
                    }
                } catch (Exception e) {
                    System.out.println(countryname + ".\"" + formatter.format(date) + "_" + type + " error");
                }

            }

            //Update for country level but we can use all of province data to compute for country
//            String id_1 = "null";
//            String sql_getvalue = " SELECT "
//                    + " Min((gv).val) As Min,"
//                    + " Max((gv).val) As Max,"
//                    + " Sum(\"public\".ST_Area((gv).geom) * (gv).val) / Sum(\"public\".ST_Area((gv).geom)) as Mean"
//                    + " FROM ("
//                    + " SELECT \"public\".ST_DumpAsPolygons(\"public\".ST_Clip(" + countryname + ".\"" + formatter.format(date) + "_" + type + "\".rast, 1, tbl_country.geom, true)) AS gv"
//                    + " FROM tbl_country," + countryname + ".\"" + formatter.format(date) + "_" + type + "\""
//                    + " WHERE  \"public\".tbl_country.id_0='" + id_0 + "') AS foo ";
//
//            ResultSet rescheck = run_SQL(connectPostgreSQL, sql_getvalue);
//            while (rescheck.next()) {
//                Double max = rescheck.getDouble("Max");
//                Double min = rescheck.getDouble("Min");
//                Double mean = rescheck.getDouble("Mean");
//                String code = formatter.format(date) + type + id_0 + id_1;
//                String sql_insert = "INSERT INTO tbl_index (code,id_0, id_1, date,type,min,max,mean) "
//                        + " VALUES ('" + code + "','" + id_0 + "','" + id_1 + "','" + formatter2.format(date) + "','" + type + "','" + min + "','" + max + "','" + mean + "')"
//                        + " ON CONFLICT (code) DO UPDATE "
//                        + " SET min = '" + min + "', "
//                        + " max = '" + max + "', "
//                        + " mean = '" + mean + "'";
//                //System.out.println(sql_insert);
//                execute_SQL(connectIndextable, sql_insert);
//            }
        }
        connectPostgreSQL.close();
        connectIndextable.close();
    }

    public static void update_IndexbyYear(int year, int id_0, int id_1, String type) throws ParseException, SQLException {
        String connectURLPostgreSQL = Config.connectURLPG + year;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        String connectURLIndextable = Config.connectURLPG + "db_Agri";
        Connection connectIndextable = Connect.getConnectPostgreSQL(connectURLIndextable, Config.userPG, Config.pwPG);

        List<Date> listdate = get_listDate(year + "0101", year + "1231");
        for (int i = 0; i < listdate.size(); i++) {
            Date date = listdate.get(i);
            System.out.println(formatter2.format(date));
            String countryname = "korea";
            if (id_0 == 213) {
                countryname = "korea";
            }
            if (id_0 == 49) {
                countryname = "china";
            }
            if (id_0 == 244) {
                countryname = "usa";
            }
            //Compute value by input parameter
            String sql_getvalue = " SELECT "
                    + " Min((gv).val) As Min,"
                    + " Max((gv).val) As Max,"
                    + " Sum(\"public\".ST_Area((gv).geom) * (gv).val) / Sum(\"public\".ST_Area((gv).geom)) as Mean"
                    + " FROM ("
                    + " SELECT \"public\".ST_DumpAsPolygons(\"public\".ST_Clip(" + countryname + ".\"" + formatter.format(date) + "_" + type + "\".rast, 1, tbl_province.geom, true)) AS gv"
                    + " FROM tbl_province," + countryname + ".\"" + formatter.format(date) + "_" + type + "\""
                    + " WHERE  \"public\".tbl_province.id_0=" + id_0 + " and \"public\".tbl_province.id_1=" + id_1 + ") AS foo ";

            ResultSet rescheck = run_SQL(connectPostgreSQL, sql_getvalue);
            while (rescheck.next()) {
                Double max = rescheck.getDouble("Max");
                Double min = rescheck.getDouble("Min");
                Double mean = rescheck.getDouble("Mean");
                String code = formatter.format(date) + type + id_0 + id_1;
                String sql_insert = "INSERT INTO tbl_index (code,id_0, id_1, date,type,min,max,mean) "
                        + " VALUES ('" + code + "','" + id_0 + "','" + id_1 + "','" + formatter2.format(date) + "','" + type + "','" + min + "','" + max + "','" + mean + "')"
                        + " ON CONFLICT (code) DO UPDATE "
                        + " SET min = '" + min + "', "
                        + " max = '" + max + "', "
                        + " mean = '" + mean + "'";
                //System.out.println(sql_insert);
                execute_SQL(connectIndextable, sql_insert);
            }
        }
        connectPostgreSQL.close();
        connectIndextable.close();
    }

    public static void get_valueIndex(Date date, int id_0, int id_1, String type) throws SQLException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        String connectURLIndextable = Config.connectURLPG + "db_Agri";
        Connection connectIndextable = Connect.getConnectPostgreSQL(connectURLIndextable, Config.userPG, Config.pwPG);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        String connectURLPostgreSQL = Config.connectURLPG + year;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);

        String countryname = "korea";
        if (id_0 == 213) {
            countryname = "korea";
        }
        if (id_0 == 49) {
            countryname = "china";
        }
        if (id_0 == 244) {
            countryname = "usa";
        }
        //Compute value by input parameter
        String sql_getvalue = " SELECT "
                + " Min((gv).val) As Min,"
                + " Max((gv).val) As Max,"
                + " Sum(\"public\".ST_Area((gv).geom) * (gv).val) / Sum(\"public\".ST_Area((gv).geom)) as Mean"
                + " FROM ("
                + " SELECT \"public\".ST_DumpAsPolygons(\"public\".ST_Clip(" + countryname + ".\"" + formatter.format(date) + "_" + type + "\".rast, 1, tbl_province.geom, true)) AS gv"
                + " FROM tbl_province," + countryname + ".\"" + formatter.format(date) + "_" + type + "\""
                + " WHERE  \"public\".tbl_province.id_0=" + id_0 + " and \"public\".tbl_province.id_1=" + id_1 + ") AS foo ";

        ResultSet rescheck = run_SQL(connectPostgreSQL, sql_getvalue);
        while (rescheck.next()) {
            Double max = rescheck.getDouble("Max");
            Double min = rescheck.getDouble("Min");
            Double mean = rescheck.getDouble("Mean");
            String code = formatter.format(date) + type + id_0 + id_1;
            String sql_insert = "INSERT INTO tbl_index (code,id_0, id_1, date,type,min,max,mean) "
                    + " VALUES ('" + code + "','" + id_0 + "','" + id_1 + "','" + formatter2.format(date) + "','" + type + "','" + min + "','" + max + "','" + mean + "')"
                    + " ON CONFLICT (code) DO UPDATE "
                    + " SET min = '" + min + "', "
                    + " max = '" + max + "', "
                    + " mean = '" + mean + "'";
            //System.out.println(sql_insert);
            execute_SQL(connectIndextable, sql_insert);
        }
    }

    public static List<Date> get_listDate(String datestart, String dateend) throws ParseException {
        List<Date> listdate = new ArrayList();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        Calendar start = Calendar.getInstance();
        start.setTime(formatter.parse(datestart));
        Calendar end = Calendar.getInstance();
        end.setTime(formatter.parse(dateend));

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            // Do your job here with `date`.
            listdate.add(date);
        }

        return listdate;
    }

}
