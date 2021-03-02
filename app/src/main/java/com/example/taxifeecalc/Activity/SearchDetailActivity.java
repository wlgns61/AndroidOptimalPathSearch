package com.example.taxifeecalc.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxifeecalc.Adapter.PathDetailAdapter;
import com.example.taxifeecalc.Model.FromToInfo;
import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;

public class SearchDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    @NonNull NaverMap naverMap;
    FromToInfo pathInfo;
    ArrayList<PlaceCoordination> path;
    InfoWindow infoWindow;
    RecyclerView pathResult;
    private BottomSheetBehavior behavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_detail_result);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.pathMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.placeMap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        pathInfo = (FromToInfo) intent.getSerializableExtra("path");
        path = pathInfo.getPaths();
        behavior = BottomSheetBehavior.from(findViewById(R.id.pathDetailBottomSheet));
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);
        this.naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        this.naverMap.setCameraPosition(setCameraPosition());
        setText();
        setPath();
        setInfoWindow();
        createMarker();
        registerAdapter();
        setButton();
    }

    public void setInfoWindow(){
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return (CharSequence)infoWindow.getMarker().getTag();
            }
        });
    }

    public void setPath(){
        PathOverlay pathOverlay = new PathOverlay();

        ArrayList<LatLng> coords = new ArrayList<>();
        for(int i=0;i<path.size();i++){
            PlaceCoordination place = path.get(i);
            coords.add(new LatLng(Double.parseDouble(place.getLatitude()),Double.parseDouble(place.getLongitude())));
        }
        pathOverlay.setCoords(coords);
        pathOverlay.setOutlineColor(Color.BLUE);
        pathOverlay.setColor(Color.GREEN);
        pathOverlay.setWidth(6);
        pathOverlay.setOutlineWidth(2);
        pathOverlay.setMap(naverMap);
    }

    private void createMarker(){

        for(int i=0;i<path.size();i++){
            PlaceCoordination place = path.get(i);
            final Marker marker = new Marker();
            marker.setPosition(new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude())));
            String tag= "";
            if(i == 0){
                tag = "출발지";
            }
            else if(i < path.size() - 1){
                marker.setIconTintColor(Color.BLUE);
                tag = "경유지 " + i;
            }
            else {
                marker.setIcon(MarkerIcons.BLACK);
                marker.setIconTintColor(Color.BLUE);
                tag = "목적지";
            }

            place.setMeta(tag);
            marker.setCaptionText(tag);
            marker.setTag(place.getRepresentName());
            marker.setWidth(130);
            marker.setHeight(160);

            marker.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {
                    if (marker.getInfoWindow() == null) {
                        // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                        infoWindow.open(marker);
                    } else {
                        // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                        infoWindow.close();
                    }
                    return true;
                }
            });
            marker.setMap(naverMap);
        }
    }

    private CameraPosition setCameraPosition(){
        PlaceCoordination startPlace = path.get(0);
        PlaceCoordination endPlace = path.get(path.size() - 1);
        Double lat = (Double.parseDouble(startPlace.getLatitude()) + Double.parseDouble(endPlace.getLatitude()))/2;
        Double lon = (Double.parseDouble(startPlace.getLongitude()) + Double.parseDouble(endPlace.getLongitude()))/2;

        CameraPosition cameraPosition =
                new CameraPosition(new LatLng(lat, lon), 10);
        return cameraPosition;
    }

    private void setText(){
        TextView pathfee = findViewById(R.id.pathfee);
        TextView pathtime = findViewById(R.id.pathtime);
        TextView pathdist = findViewById(R.id.pathdist);
        TextView meta = findViewById(R.id.searchTypeMeta);
        pathfee.setText("택시 요금: " + pathInfo.getStringFee() + "원");
        pathtime.setText("시간: " + pathInfo.getStringTime());
        pathdist.setText("거리: " + pathInfo.getStringDist() + "km");
        meta.setText(pathInfo.getName());
    }

    private void setButton(){

        final Button btn = findViewById(R.id.detailPathBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(behavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btn.setText("경로 표시 안함");
                }
                else{
                    downBottomSheet();
                }
            }
        });
    }

    private void registerAdapter(){

        pathResult = findViewById(R.id.pathDetail);
        pathResult.addItemDecoration(new DividerItemDecoration(this, 1));
        pathResult.setLayoutManager(new LinearLayoutManager(this));
        PathDetailAdapter adapter = new PathDetailAdapter(path);
        pathResult.setAdapter(adapter);
    }

    public void downBottomSheet(){
        if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            Button btn = findViewById(R.id.detailPathBtn);
            Integer height = behavior.getPeekHeight();
            behavior.setPeekHeight(height);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            btn.setText("상세 경로 보기");
        }
    }

    @Override
    public void onBackPressed(){
        if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            downBottomSheet();
        else
            finish();
    }

}
