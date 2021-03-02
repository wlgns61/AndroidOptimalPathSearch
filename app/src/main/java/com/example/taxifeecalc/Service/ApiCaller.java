package com.example.taxifeecalc.Service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.net.URLEncoder;

public class ApiCaller {

    Context context;
    private String clientId;
    private String clientSecret;
    private String searchStr = "";
    private String encode = "";
    private String header_id = "";
    private String header_secret = "";
    private String response = "";

    public String geoSearchApi(String keyword){

        this.header_id = "X-NCP-APIGW-API-KEY-ID";
        this.header_secret = "X-NCP-APIGW-API-KEY";
        this.clientId = "YOUR_CLIENT_ID";
        this.clientSecret = "YOUR_CLIENT_SECRET";
        GPStracker coord = new GPStracker(context);
        String latitude = Double.toString(coord.latitude);
        String longitude = Double.toString(coord.longitude);
        try{
            encode = URLEncoder.encode(keyword, "UTF-8");
            searchStr = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + encode + "coordinate=" + longitude + "," + latitude;
            ApiCallThread thread = new ApiCallThread(header_id,header_secret,clientId,clientSecret,searchStr);
            response = thread.execute().get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public String drivingApi(String source, String dest, String wayPoints){

        this.header_id = "X-NCP-APIGW-API-KEY-ID";
        this.header_secret = "X-NCP-APIGW-API-KEY";
        this.clientId = "YOUR_CLIENT_ID";
        this.clientSecret = "YOUR_CLIENT_SECRET";
        try{
            String enStart = URLEncoder.encode(source, "UTF-8");
            String enGoal = URLEncoder.encode(dest, "UTF-8");
            String enWay = "";
            if(wayPoints != null)
                enWay = URLEncoder.encode(wayPoints, "UTF-8");
            searchStr = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=" + enStart  + "&goal=" + enGoal;
            if(wayPoints != null && wayPoints.length() > 0)
                searchStr += ("&waypoints=" + enWay);
            ApiCallThread thread = new ApiCallThread(header_id,header_secret,clientId,clientSecret,searchStr);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
                response = thread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get(); //병렬처리
            else
                response = thread.execute().get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public String placeSearchApi(String keyword){

        this.header_id = "X-Naver-Client-Id";
        this.header_secret = "X-Naver-Client-Secret";
        this.clientId = "YOUR_CLIENT_ID";
        this.clientSecret = "YOUR_CLIENT_SECRET";
        try{
            encode = URLEncoder.encode(keyword, "UTF-8");
            searchStr = "https://openapi.naver.com/v1/search/local?query=" + encode + "&display=5";
            ApiCallThread thread = new ApiCallThread(header_id,header_secret,clientId,clientSecret,searchStr);
            response = thread.execute().get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public String reverseGeoAPI(String latitude, String longitude){

        this.header_id = "X-NCP-APIGW-API-KEY-ID";
        this.header_secret = "X-NCP-APIGW-API-KEY";
        this.clientId = "YOUR_CLIENT_ID";
        this.clientSecret = "YOUR_CLIENT_SECRET";
        try{
            String lat = URLEncoder.encode(latitude, "UTF-8");
            String lon = URLEncoder.encode(longitude, "UTF-8");
            searchStr = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=" + lon + "," + lat + "&orders=roadaddr&output=json";
            ApiCallThread thread = new ApiCallThread(header_id,header_secret,clientId,clientSecret,searchStr);
            response = thread.execute().get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

}
