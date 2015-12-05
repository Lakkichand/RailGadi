package com.railgadi.dbhandlers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.railgadi.beans.TrainDataBean;
import com.railgadi.comparators.TrainNameComparator;
import com.railgadi.comparators.TrainNumberComparator;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vijay on 02-12-2015.
 */

public class TrainDBHandler {


    private static final String DB_NAME = TrainDBcreate.TRAIN_DB_NAME;
    private static final String TRAIN_TABLE_NAME = "trainlist";

    private Context context;
    private SQLiteDatabase db;

    public TrainDBHandler(Context context) {

        this.context = context;
        this.db = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }


    public List<TrainDataBean> getSearchedItemList(String searchTerm) {

        Cursor c = null;

        searchTerm = searchTerm.toUpperCase() ;

        boolean isNumber = false ;

        try {

            String sql = "";

            //Select , trainName from trainlist where UPPER(trainNum) like 'JAI%' OR UPPER (trainName) like '%JAI%' order by trainName;

            if (UtilsMethods.isStartWithDigit(searchTerm)) {
                sql += "SELECT * FROM " + TRAIN_TABLE_NAME;
                sql += " WHERE UPPER(num) LIKE '" + searchTerm + "%'";
                sql += " OR UPPER(num) LIKE '%" + searchTerm + "%'";

                isNumber = true ;
            } else {
                sql += "SELECT * FROM " + TRAIN_TABLE_NAME;
                sql += " WHERE UPPER(name) LIKE '" + searchTerm + "%'";
                sql += " OR UPPER(name) LIKE '%" + searchTerm + "%'";

                isNumber = false ;
            }

            /*if(UtilsMethods.isStartWithDigit(searchTerm)) {
                sql += "SELECT * FROM " + TRAIN_TABLE_NAME;
                sql += " WHERE num LIKE '" + searchTerm + "%'";
                sql += " LIMIT 0,10";
            } else {
                sql += "SELECT * FROM " + TRAIN_TABLE_NAME;
                sql += " WHERE name LIKE '" + searchTerm + "%'";
                sql += " LIMIT 0,10";
            }*/

            c = db.rawQuery(sql, null);

            if (c == null || c.getCount() <= 0) {
                return null;
            }

            List<TrainDataBean> list = new ArrayList<>();

            while (c.moveToNext()) {

                String number = c.getString(0);
                String name = c.getString(1);

                TrainDataBean bean = new TrainDataBean();
                bean.setTrainName(name);
                bean.setTrainNumber(number);

                list.add(bean);
            }

            if(isNumber) {
                Collections.sort(list, new TrainNumberComparator());
            } else {
                Collections.sort(list, new TrainNameComparator());
            }

            return list;

        } catch (Exception e) {

            return null;
        } finally {

            c.close();
        }

        /*List<TrainDataBean> recordsList = new ArrayList<>();

        // select query
        String sql = "";
        sql += "SELECT * FROM " + TRAIN_TABLE_NAME;
        sql += " WHERE " + fieldObjectName + " LIKE '" + searchTerm + "%'";
        sql += " LIMIT 0,5";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                String objectName = cursor.getString(cursor.getColumnIndex(fieldObjectName));
                TrainDataBean TrainDataBean = new TrainDataBean(objectName);

                // add to list
                recordsList.add(TrainDataBean);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return the list of records
        return recordsList;

        */
    }

}
