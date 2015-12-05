package com.railgadi.utilities;

import com.railgadi.exception.PNRStatusException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
/*
 * Confirm: Very High confirmation chances.
 * Probable: Low confirmation chances.
 * No Chance: Very Low Confirmation chances(< 30% probability).
 * Prediction for tatkal tickets is not available at this moment.
 * RAC is considered as confirmed while predicting
 * Usage:
        GetConfirmChance gcc = new GetConfirmChance();
		HashMap<Integer,String> list=gcc.getPNRConfirmChance("6140468255");// output: Key: 1, value: No Chance
 */

public class GetConfirmChance {
	 public  int passengerCount;
	 public int number;
	 public String confirmTktStatus;
	 public String pnr;
	 public  String Source;
	 public  String TravelClass;
	 public HashMap<Integer, String> hashMap;
	  //public static PnrResponse pnrResponse;
	  
	 public GetConfirmChance(){
		 hashMap = new HashMap<Integer, String>();
	 }

	 public  HashMap<Integer, String> getPNRConfirmChance(String pnr) throws PNRStatusException
	 {
		 String str = Configuration.CNFTKT_URL;
		 this.pnr = pnr;
		 String url =String.format(str,pnr);
		 HttpURLConnection c = null;
		 try{
			 URL u = new URL(url);
		        c = (HttpURLConnection) u.openConnection();
		        c.setRequestMethod("GET");
		        c.setRequestProperty("Content-length", "0");
		        c.setUseCaches(false);
		        c.setAllowUserInteraction(false);
		        c.setConnectTimeout(Configuration.TIMEOUT);
		        c.setReadTimeout(Configuration.TIMEOUT);
		        c.connect();
		        int status = c.getResponseCode();
		        String output="";
		        switch (status) {
		            case 200:
		            case 201:
		                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
		                StringBuilder sb = new StringBuilder();
		                String line;
		                while ((line = br.readLine()) != null) {
		                    sb.append(line+"\n");
		                }
		                br.close();
		                output=sb.toString();
		                //System.out.println(sb);
		               // return sb.toString();
		        }
			 
			 JSONObject localJSONObject1 = new JSONObject(output);
			 
			 String error=localJSONObject1.getString("Error");
			 if ((error != null) && (!error.equals("null")))
				 throw new PNRStatusException("Confirm chance status error:"+error);
			 
			 passengerCount = localJSONObject1.getInt("PassengerCount");
			 //System.out.println("PassengerCount="+passengerCount);
			 JSONArray localJSONArray = localJSONObject1.getJSONArray("PassengerStatus");
			 for (int i = 0; ; i++)
			 {
				 if (i >= localJSONArray.length())
				 {		
					 //break;
					 return hashMap;
				 }
				 JSONObject localJSONObject2 = localJSONArray.getJSONObject(i);
				 String confirmTktStatus = localJSONObject2.getString("ConfirmTktStatus");
				 int number = localJSONObject2.getInt("Number");	
				 hashMap.put(number, confirmTktStatus);
				 //System.out.println(number+"=="+confirmTktStatus);
			 }

		 } 
//		 catch (ClientProtocolException localClientProtocolException)
//		 {
//			 throw new PNRStatusException("ConfirmTKT connection error");
//			 //localClientProtocolException.printStackTrace();
//		 }
		 catch (IOException localIOException)
		 {
			 localIOException.printStackTrace();
		 }
		 catch (JSONException localJSONException)
		 {			 
				 localJSONException.printStackTrace();
		 }		 
		 return hashMap;	    
	 }
}
