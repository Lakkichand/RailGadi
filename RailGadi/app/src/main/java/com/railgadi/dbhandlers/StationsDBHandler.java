package com.railgadi.dbhandlers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.javadocmd.simplelatlng.Geohasher;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.railgadi.R;
import com.railgadi.beans.NearByStationBean;
import com.railgadi.beans.StationNameCodeBean;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StationsDBHandler {

    private static final String DB_NAME = StationDBcreate.STATION_DB_NAME;
    private static final String STATION_TABLE_NAME = "StationsDb";

    private Context context;
    private SQLiteDatabase db;

    public StationsDBHandler(Context context) {
        this.context = context;

        this.db = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    public String[] getLatlngByCode(String code) {

        String[] latlang = new String[2];

        Cursor c = null;
        try {
            c = db.rawQuery("select lat, lng from " + STATION_TABLE_NAME + " where code='" + code + "'", null);

            if (c == null || c.getCount() < 0) {
                return null;
            }

            while (c.moveToNext()) {
                latlang[0] = getOriginalText(c.getString(0));
                latlang[1] = getOriginalText(c.getString(1));
            }

            return latlang;

        } catch (Exception e) {
            return null;
        } finally {
            c.close();
        }
    }


    public String getStationNameFromCode(String code) {

        Cursor c = null;

        try {
            String q = "select name from " + STATION_TABLE_NAME + " where code='" + code + "'";

            c = db.rawQuery(q, null);

            if (c == null || c.getCount() <= 0) {
                return null;
            }

            while (c.moveToNext()) {

                String name = c.getString(0);

                return name;
            }

        } catch (Exception e) {

            return null;
        } finally {

            c.close();
        }
        return null;
    }


    public String getGeoHashOfStation(String stationCode) {

        Cursor c = null;

        try {

            String q = "select images from " + STATION_TABLE_NAME + " where code='" + stationCode + "'";

            c = db.rawQuery(q, null);

            if (c == null || c.getCount() <= 0) {
                return null;
            }

            while (c.moveToNext()) {

                String geo = c.getString(0);

                return geo;
            }
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
        }

        return null;
    }


    public List<StationNameCodeBean> getDataWithSearchTerm(String searchTerm) {

        List<StationNameCodeBean> list = new ArrayList<>();
        Cursor c = null;

        try {

            String sql = "";

            sql += "SELECT code,name FROM " + STATION_TABLE_NAME;
            sql += " WHERE name LIKE '" + searchTerm + "%'";
            sql += " OR code Like '" + searchTerm + "%'";
            sql += " LIMIT 0,5";

            c = db.rawQuery(sql, null);

            if (c == null || c.getCount() <= 0) {
                return null;
            }

            while (c.moveToNext()) {

                String code = c.getString(0);
                String name = c.getString(1);

                list.add(new StationNameCodeBean(code, name, 0));
            }

        } catch (Exception e) {
            list = null;
        } finally {
            c.close();
        }


        return list;
    }



    /*

    private String getCipherText(String plainText) throws Exception {

        byte[] sharedVector = {0x01, 0x02, 0x03, 0x05, 0x07, 0x0B, 0x0D, 0x11};

        String EncText = "";
        byte[] keyArray = new byte[24];
        byte[] temporaryKey;
        String key = context.getResources().getString(R.string.db_encryption_key);
        byte[] toEncryptArray = null;

        toEncryptArray = plainText.getBytes("UTF-8");
        MessageDigest m = MessageDigest.getInstance("MD5");
        temporaryKey = m.digest(key.getBytes("UTF-8"));

        if (temporaryKey.length < 24) // DESede require 24 byte length key
        {
            int index = 0;
            for (int i = temporaryKey.length; i < 24; i++) {
                keyArray[i] = temporaryKey[index];
            }
        }

        Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(sharedVector));
        byte[] encrypted = c.doFinal(toEncryptArray);
        EncText = Base64.encodeToString(encrypted, 1);

        return EncText;
    }

    */


    private String getOriginalText(String cipherText) throws Exception {

        byte[] sharedVector = {0x01, 0x02, 0x03, 0x05, 0x07, 0x0B, 0x0D, 0x11};

        String RawText = "";
        byte[] keyArray = new byte[24];
        byte[] temporaryKey;
        String key = context.getResources().getString(R.string.db_encryption_key);
        byte[] toEncryptArray = null;

        MessageDigest m = MessageDigest.getInstance("MD5");
        temporaryKey = m.digest(key.getBytes("UTF-8"));

        if (temporaryKey.length < 24) // DESede require 24 byte length key
        {
            int index = 0;
            for (int i = temporaryKey.length; i < 24; i++) {
                keyArray[i] = temporaryKey[index];
            }
        }

        Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(sharedVector));
        byte[] decrypted = c.doFinal(Base64.decode(cipherText, 1));

        RawText = new String(decrypted, "UTF-8");

        return RawText;
    }


    public List<NearByStationBean> getAllStationNearByMe(String myGeo) {

        LatLng current = Geohasher.decode(myGeo);

        List<NearByStationBean> allNearBy = new ArrayList<>();

        String initial = myGeo.charAt(0) + "" + myGeo.charAt(1);

        String q = " SELECT code,name,lat,lng FROM " + STATION_TABLE_NAME + " WHERE images LIKE '" + initial + "%'";

        Cursor c = db.rawQuery(q, null);

        if (c.getCount() <= 0) {
            return null;
        }

        while (c.moveToNext()) {

            try {

                String code = c.getString(0);
                String name = c.getString(1);
                String lat = getOriginalText(c.getString(2));
                String lng = getOriginalText(c.getString(3));

                if (code != null && name != null && lat != null && lng != null) {

                    NearByStationBean bean = new NearByStationBean();
                    bean.setStationCode(code);
                    bean.setStationName(name);
                    bean.setLatitude(Double.valueOf(lat));
                    bean.setLongitude(Double.valueOf(lng));

                    LatLng station = new LatLng(bean.getLatitude(), bean.getLongitude());

                    double distance = LatLngTool.distance(current, station, LengthUnit.KILOMETER);

                    bean.setDistance(distance);

                    allNearBy.add(bean);
                }
            } catch (Exception e) {

            }
        }

        c.close();

        if (allNearBy.size() <= 0) {
            return null;
        }
        return allNearBy;
    }

}