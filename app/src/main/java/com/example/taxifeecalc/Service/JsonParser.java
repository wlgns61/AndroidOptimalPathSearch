package com.example.taxifeecalc.Service;

import com.example.taxifeecalc.Model.FromToInfo;
import com.example.taxifeecalc.Model.PlaceCoordination;

import org.json.JSONArray;
import org.json.JSONObject;
import kr.hyosang.coordinate.*;


import java.util.ArrayList;

public class JsonParser {

    private static int devidePoint;

    //place의 title의 태그를 제거
    private static String removeTag(String title) throws Exception {
        title = title.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
        return title.replaceAll("&amp;","&");
    }

    public ArrayList getPlaces(String place){

        ArrayList arr = new ArrayList<PlaceCoordination>();

        try{
            JSONObject placeOb = new JSONObject(place);
            JSONArray items = (JSONArray)placeOb.get("items");
            for(int i=0;i<items.length();i++){
                JSONObject tmp = (JSONObject)items.get(i);

                String title = removeTag((String)tmp.get("title"));
                String address = (String)tmp.get("roadAddress");
                String longitude = (String)tmp.get("mapx");
                String latitude = (String)tmp.get("mapy");

                //카텍에서 위도, 경도로 변환
                CoordPoint pt = new CoordPoint(Integer.parseInt(longitude), Integer.parseInt(latitude));
                CoordPoint transCoord = TransCoord.getTransCoord(pt, TransCoord.COORD_TYPE_KTM, TransCoord.COORD_TYPE_WGS84);
                longitude =  Double.toString(transCoord.x);
                latitude =  Double.toString(transCoord.y);
                PlaceCoordination elem = new PlaceCoordination(title, address, longitude, latitude);
                arr.add(elem);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public ArrayList getGeos(String strGeo) {

        ArrayList arr = new ArrayList<PlaceCoordination>();

        try{
            JSONObject geoOb = new JSONObject(strGeo);
            JSONArray addresses = geoOb.getJSONArray("addresses");
            for(int i=0;i<addresses.length();i++){
                JSONObject tmp = (JSONObject)addresses.get(i);

                String title = "";
                String address = (String)tmp.get("roadAddress");
                if(address.length() == 0)
                    address = (String)tmp.get("jibunAddress");
                String longitude = (String)tmp.get("x");
                String latitude = (String)tmp.get("y");

                PlaceCoordination elem = new PlaceCoordination(title, address, longitude, latitude);
                arr.add(elem);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public PlaceCoordination getReverseGeo(String strGeo, String latitude, String longitude) {

        PlaceCoordination place = new PlaceCoordination("현 위치", "현 위치",longitude, latitude);

        try{
            JSONObject geoOb = new JSONObject(strGeo);
            JSONArray addresses = geoOb.getJSONArray("results");
            JSONObject addressJson = (JSONObject)addresses.get(0);
            JSONObject region = addressJson.getJSONObject("region");
            JSONObject land = addressJson.getJSONObject("land");
            JSONObject addition = land.getJSONObject("addition0");

            String area1 = (String) region.getJSONObject("area1").get("name");
            String area2 = (String) region.getJSONObject("area2").get("name");
            String area3 = (String) region.getJSONObject("area3").get("name");
            String area4 = (String) region.getJSONObject("area4").get("name");
            String land1 = (String) land.get("number1");
            String land2 = (String) land.get("number2");

            String address = area1 + " " + area2;
            if(area3 != null && area3.length() > 0)
                address += (" " + area3);
            if(area4 != null && area4.length() > 0)
                address += (" " + area4);
            if(land1 != null && land1.length() > 0)
                address += (" " + land1);
            if(land2 != null && land2.length() > 0)
                address += ("-" + land2);

            String title = (String) addition.get("value");
            if(title == null || title.length() == 0)
                title = address;

            place = new PlaceCoordination(title,address,longitude,latitude);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return place;
    }

    public FromToInfo fromToInfo(String info){

        FromToInfo pathInfo = new FromToInfo(0,0,0);

        try{

            JSONObject infoOb = new JSONObject(info);
            JSONObject route =  infoOb.getJSONObject("route");
            JSONArray traoptimal = route.getJSONArray("traoptimal");
            JSONObject tra =  (JSONObject) traoptimal.get(0);
            JSONObject summary =  tra.getJSONObject("summary");

            int distance = (int)summary.get("distance");
            int fee = (int)summary.get("taxiFare");
            int duration = (int)summary.get("duration");

            pathInfo = new FromToInfo(distance, fee, duration);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return pathInfo;
    }
}
