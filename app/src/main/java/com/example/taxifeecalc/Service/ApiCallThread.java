package com.example.taxifeecalc.Service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCallThread extends AsyncTask<Void, Void, String> {

    Context context;
    private String clientId;
    private String clientSecret;
    private String header_id = "";
    private String header_secret = "";
    private String searchStr = "";
    public StringBuffer response = new StringBuffer();

    public ApiCallThread(String header_id, String header_secret, String clientId, String clientSecret, String searchStr) {
        this.header_id = header_id;
        this.header_secret = header_secret;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.searchStr = searchStr;
    }

    @Override
    protected String doInBackground(Void... voids){

        try{
            URL searchURL = new URL(searchStr);
            HttpURLConnection conn = (HttpURLConnection)searchURL.openConnection();
            BufferedReader br;
            conn.setRequestMethod("GET");
            conn.setRequestProperty(header_id, clientId);
            conn.setRequestProperty(header_secret, clientSecret);
            int responseCode = conn.getResponseCode();

            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
