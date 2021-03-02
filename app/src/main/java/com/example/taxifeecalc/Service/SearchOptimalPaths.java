package com.example.taxifeecalc.Service;

import com.example.taxifeecalc.Model.FromToInfo;
import com.example.taxifeecalc.Model.PlaceCoordination;

import java.util.ArrayList;

public class SearchOptimalPaths {

    PlaceCoordination start;
    PlaceCoordination end;
    ArrayList<PlaceCoordination> vias;

    ArrayList<PlaceCoordination> normalPath;
    ArrayList<FromToInfo> fromToInfos = new ArrayList<>();

    final int min = 0x6FFFFFFF;
    int idx = -1;
    FromToInfo costInfo;

    FromToInfo minimumFee = new FromToInfo(min,min,min);
    FromToInfo minimumTime = new FromToInfo(min,min,min);
    FromToInfo minimumDist = new FromToInfo(min,min,min);
    FromToInfo normal = new FromToInfo(min,min,min);

    public SearchOptimalPaths(PlaceCoordination start, PlaceCoordination end, ArrayList<PlaceCoordination> vias) {

        this.start = start;
        this.end = end;
        this.vias = vias;
        this.idx = -1;

        sequentialPath();
        //출발지와 목적지로만
        if((vias == null || vias.size() == 0) && start != null && end != null){
            try {
                minimumFee  = (FromToInfo)normal.clone();
                minimumFee.setName("최소 비용 탐색");

                minimumDist  = (FromToInfo)normal.clone();
                minimumDist.setName("최소 거리 탐색");

                minimumTime  = (FromToInfo)normal.clone();
                minimumTime.setName("최소 시간 탐색");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        //출발지와 경유지로만
        else if(end == null && start != null && vias != null && vias.size() > 0){
            withViaPath();
        }
        //출발지와 경유지 목적지 모두 선택된 경우
        else if(end != null && start != null && vias != null && vias.size() > 0){
            withEndPath();
        }
    }

    public void sequentialPath(){

        normalPath = new ArrayList<>();
        normalPath.add(start);
        if(vias != null) {
            for (int i = 0; i < vias.size(); i++)
                normalPath.add(vias.get(i));
        }
        if(end != null)
            normalPath.add(end);

        this.normal = makeInfo(normalPath);
        normal.setName("입력 순서 탐색");
    }

    private void withViaPath(){

        Permutation perm = new Permutation(vias.size());
        ArrayList<ArrayList<Integer>> permutation = perm.getPermutation();
        final ArrayList<ArrayList<PlaceCoordination>> paths = new ArrayList<>();

        for(int i=0;i<permutation.size();i++){
            ArrayList elem = new ArrayList(permutation.get(i));
            elem.add(0,0); //출발지
            ArrayList<PlaceCoordination> path = makePath(elem);
            paths.add(path);
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for(int i=0;i<permutation.size();i++){
            threads.add(new Thread(){
                @Override
                public void run() {
                    fromToInfos.add(makeInfo(paths.get(++idx)));
                }
            });
        }
        for(int i=0;i<threads.size();i++){
            Thread thread = threads.get(i);
            thread.start();
        }
        for(int i=0;i<threads.size();i++){
            try{
                threads.get(i).join();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        for(int i=0;i<fromToInfos.size();i++){
            FromToInfo costInfo = fromToInfos.get(i);
            if(minimumFee.getFee() > costInfo.getFee()){
                try {
                    minimumFee = (FromToInfo)costInfo.clone();
                    minimumFee.setName("최소 비용 탐색");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(minimumDist.getDistance() > costInfo.getDistance()){
                try {
                    minimumDist = (FromToInfo)costInfo.clone();
                    minimumDist.setName("최소 거리 탐색");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(minimumTime.getTime() > costInfo.getTime()){
                try {
                    minimumTime = (FromToInfo)costInfo.clone();
                    minimumTime.setName("최소 시간 탐색");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void withEndPath(){

        Permutation perm = new Permutation(vias.size());
        ArrayList<ArrayList<Integer>> permutation = perm.getPermutation();
        final ArrayList<ArrayList<PlaceCoordination>> paths = new ArrayList<>();

        for(int i=0;i<permutation.size();i++){
            ArrayList elem = new ArrayList(permutation.get(i));
            elem.add(0,0); //출발지
            elem.add(vias.size() + 1); //도착지
            ArrayList<PlaceCoordination> path = makePath(elem);
            paths.add(path);
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for(int i=0;i<permutation.size();i++){
            threads.add(new Thread(){
                @Override
                public void run() {
                    fromToInfos.add(makeInfo(paths.get(++idx)));
                }
            });
        }
        for(int i=0;i<threads.size();i++){
            Thread thread = threads.get(i);
            thread.start();
        }
        for(int i=0;i<threads.size();i++){
            try{
                threads.get(i).join();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        for(int i=0;i<fromToInfos.size();i++){

            FromToInfo costInfo = fromToInfos.get(i);
            if(minimumFee.getFee() > costInfo.getFee()){
                try {
                    minimumFee = (FromToInfo)costInfo.clone();
                    minimumFee.setName("최소 비용 탐색");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(minimumDist.getDistance() > costInfo.getDistance()){
                try {
                    minimumDist = (FromToInfo)costInfo.clone();
                    minimumDist.setName("최소 거리 탐색");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(minimumTime.getTime() > costInfo.getTime()){
                try {
                    minimumTime = (FromToInfo)costInfo.clone();
                    minimumTime.setName("최소 시간 탐색");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<PlaceCoordination> makePath(ArrayList<Integer> permIdx){

        ArrayList<PlaceCoordination> path = new ArrayList<>();
        for(int i=0;i<permIdx.size();i++){
            path.add(normalPath.get(permIdx.get(i)));
        }
        return path;
    }

    private FromToInfo makeInfo(ArrayList<PlaceCoordination> path){


        String start = path.get(0).getLongitude() + "," + path.get(0).getLatitude();
        String end = path.get(path.size() - 1).getLongitude() + "," + path.get(path.size() - 1).getLatitude();
        String waypoint = "";

        for(int i=1;i<path.size() - 1;i++){
            PlaceCoordination place = path.get(i);
            String longitude = place.getLongitude();
            String latitude = place.getLatitude();
            waypoint += (longitude + "," + latitude);
            if(i < path.size() - 2)
                waypoint += "|";
        }

        ApiCaller apiCaller = new ApiCaller();
        JsonParser jsonParser = new JsonParser();

        String result = apiCaller.drivingApi(start, end, waypoint);
        FromToInfo fromToInfo = jsonParser.fromToInfo(result);
        fromToInfo.setPaths(path);
        return fromToInfo;
    }

    public FromToInfo getMinimumFee() {
        return minimumFee;
    }
    public FromToInfo getMinimumTime() {
        return minimumTime;
    }
    public FromToInfo getMinimumDist() {
        return minimumDist;
    }
    public FromToInfo getNormal() {
        return normal;
    }
}
