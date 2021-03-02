package com.example.taxifeecalc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;

public class PlaceDecideActivity extends FragmentActivity implements OnMapReadyCallback {

    @NonNull NaverMap myNaverMap;

    PlaceCoordination place;
    String btntext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_decide);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.placeMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.placeMap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        getInfo();
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        myNaverMap = naverMap;
        myNaverMap.setCameraPosition(setCameraPosition());
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);
        myNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        anyAction();
    }

    private void anyAction(){
        setCameraPosition();
        createMarker();
        setPlaceInfo();
        buttonClick();
    }

    private void getInfo(){
        Intent intent = getIntent();
        this.place = (PlaceCoordination) intent.getSerializableExtra("place");
        this.btntext = intent.getStringExtra("text");
    }

    private CameraPosition setCameraPosition(){
        CameraPosition cameraPosition =
                new CameraPosition(new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude())), 16);
        return cameraPosition;
    }

    private void createMarker(){
        Marker marker = new Marker();
        marker.setPosition(new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude())));
        marker.setMap(myNaverMap);
    }

    private void setPlaceInfo(){

        TextView title = findViewById(R.id.decideTitle);
        TextView address = findViewById(R.id.decideAddress);

        if(place.getTitle().length() > 0) title.setText(place.getTitle());
        else title.setText(place.getAddress());
        address.setText(place.getAddress());
    }

    private void buttonClick(){

        Button btn = findViewById(R.id.decideButton);
        btn.setText(btntext+"로 설정");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("decide", place);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
