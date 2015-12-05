package com.railgadi.webServices;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpServicesRailGadi {

    private static InputStream inputStream;
    private static HttpClient httpclient;
    private static String result = "";

    public static String GET(String url) {


        try {

            httpclient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = httpclient.execute(httpGet);

            inputStream = response.getEntity().getContent();

            if (inputStream != null) {

                result = convertInputStreamToString(inputStream);

            } else {

                result = "Did not work!";
            }

        } catch (Exception e) {

            result = null;
        }

        return result;
    }


    public static String POST(String url, JSONObject jsonObject) {

        try {

            httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);

            String json = "";

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {

                result = convertInputStreamToString(inputStream);

            } else {

                result = "Did not work!";
            }

        } catch (Exception e) {

            result = null;
        }

        return result;
    }


    public static String DELETE(String urlWithParameters) {

        String response = null ;

        try {

            URL url = new URL(urlWithParameters);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "" ;

            while ((line = br.readLine()) != null) {
                response += line;
            }

            br.close();
            connection.disconnect();

        } catch( Exception e ) {
            return null ;
        }

        return response ;
    }



    public static String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuffer result = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        //result += line;

        inputStream.close();
        return result.toString();

    }

}