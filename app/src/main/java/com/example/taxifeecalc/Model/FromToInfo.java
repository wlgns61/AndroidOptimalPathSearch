package com.example.taxifeecalc.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class FromToInfo implements Cloneable, Serializable {

    int distance;
    int time;
    int fee;
    String name;
    ArrayList<PlaceCoordination> paths;

    public FromToInfo(int fee, int distance, int time) {
        this.distance = distance;
        this.fee = fee;
        this.time = time;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getFee() {
        return fee;
    }
    public void setFee(int fee) {
        this.fee = fee;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<PlaceCoordination> getPaths(){
        return paths;
    }
    public  void setPaths(ArrayList paths){
        this.paths = paths;
    }

    //단위 원
    public String getStringFee(){
        String strfee = "";
        int fee = this.fee;
        fee /= 10;
        fee *= 10;
        strfee = Integer.toString(fee);
        return strfee;
    }
    //단위 km
    public String getStringDist(){
        String strdist = "";
        Float dist = Float.valueOf(this.distance);
        dist /= 1000;
        strdist = dist.toString();
        return strdist;
    }
    //단위 분
    public String getStringTime(){
        String strtime = "";

        int time = this.time;
        time /= 1000; //초로 변환
        time /= 60;   //분으로 변환

        int h = time / 60;
        int m = time % 60;

        strtime = (h)+"시간 " + (m) + "분";
        return strtime;
    }

    @Override
    public String toString() {

        String pathsStr = "";
        for(int i=0;i<paths.size();i++){
            pathsStr += paths.get(i).getRepresentName();
            if(i < paths.size() - 1)
                pathsStr += ", ";
        }

        return "FromToInfo{" +
                "distance=" + distance +
                ", time=" + time +
                ", fee=" + fee +
                ", name='" + name + '\'' +
                ", paths=" + "{" + pathsStr + "} " +
                '}';
    }
}
