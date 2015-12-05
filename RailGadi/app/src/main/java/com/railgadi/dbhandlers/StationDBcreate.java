package com.railgadi.dbhandlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class StationDBcreate extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH;

    public static String STATION_DB_NAME = "StationsDB" ;
    public static String TRAIN_DB_NAME = "TrainListDB" ;

    private SQLiteDatabase myDataBase;

    private static String PACKAGE;
    private final Context myContext;


    public StationDBcreate(Context context) {

        super(context, STATION_DB_NAME, null, 1);

        this.myContext = context;

        PACKAGE = this.myContext.getPackageName();

        DB_PATH = "/data/data/" + PACKAGE + "/databases/";

    }

    public void createDataBase() throws Exception {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {

            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }
    }

    private boolean checkDataBase() throws Exception {

        SQLiteDatabase checkDB = null;
        //InputStream myInput = myContext.getResources().getAssets().open(STATION_DB_NAME);

        String myPath = DB_PATH + STATION_DB_NAME;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch(Exception e) {
            return false ;
        }


        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getResources().getAssets().open(STATION_DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + STATION_DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + STATION_DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.


}
