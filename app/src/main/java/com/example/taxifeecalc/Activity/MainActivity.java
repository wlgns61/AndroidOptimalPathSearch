package com.example.taxifeecalc.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taxifeecalc.Model.FromToInfo;
import com.example.taxifeecalc.Service.ApiCaller;
import com.example.taxifeecalc.Service.GPStracker;
import com.example.taxifeecalc.Service.JsonParser;
import com.example.taxifeecalc.Adapter.OnPlaceItemClickListener;
import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;
import com.example.taxifeecalc.Adapter.SearchResultAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource; //지자기, 가속도 센서
    @NonNull NaverMap myNaverMap;

    private BottomSheetBehavior behavior;
    RecyclerView searchResult;

    PlaceCoordination start;
    PlaceCoordination end;
    ArrayList<PlaceCoordination> vias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE); //앱을 실행하였을 때 현재위치에 대한정보가 있는 객체
        setStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                myNaverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        myNaverMap = naverMap;
        UiSettings uiSettings = naverMap.getUiSettings();
        myNaverMap.setLocationSource(locationSource);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);
        myNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        myNaverMap.setCameraPosition(setCamera());
        anyAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 2){
                TextView textView = findViewById(R.id.viaCnt);
                vias = (ArrayList<PlaceCoordination>) data.getSerializableExtra("viaList");
                if(vias == null || vias.size() == 0)
                    textView.setText("경유지 없음");
                else{
                    String text = "경유지 " + vias.size() + "개";
                    textView.setText(text);
                }
                downBottomSheet();
            }
            if(requestCode == 3){
                EditText source = findViewById(R.id.source);
                start = (PlaceCoordination) data.getSerializableExtra("place");
                source.setText(start.getRepresentName());
                source.setFocusable(false);
                downBottomSheet();
            }
            if(requestCode == 4){
                EditText dest = findViewById(R.id.dest);
                end = (PlaceCoordination) data.getSerializableExtra("place");
                dest.setText(end.getRepresentName());
                dest.setFocusable(false);
                downBottomSheet();
            }
            if(requestCode == 5){
                ArrayList<FromToInfo> paths = (ArrayList<FromToInfo>) data.getSerializableExtra("paths");
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                intent.putExtra("paths",paths);
                if(end == null) intent.putExtra("isEnd",false);
                else intent.putExtra("isEnd",true);
                startActivity(intent);
            }
        }
    }

    public void anyAction(){

        manageBottomSheet();
        pathSearch();
        viaEdit();
        setElseButton();
    }

    public void setElseButton(){
        Button setnow = findViewById(R.id.setNow);
        Button noend = findViewById(R.id.noEnd);

        setnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStart();
                myNaverMap.setCameraPosition(setCamera());
                EditText editText = findViewById(R.id.source);
                editText.setFocusable(false);
                editText.setClickable(false);
                downBottomSheet();
            }
        });
        noend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end = null;
                EditText editText = findViewById(R.id.dest);
                editText.setText(null);
                editText.setFocusable(false);
                editText.setClickable(false);
                downBottomSheet();
            }
        });
    }

    private void setStart(){

        GPStracker gps = new GPStracker(getApplicationContext());
        Location location = gps.getLocation();
        ApiCaller apiCaller = new ApiCaller();
        JsonParser jsonParser = new JsonParser();

        Double la = location.getLatitude();
        Double lo = location.getLongitude();
        String startJson =  apiCaller.reverseGeoAPI(la.toString(), lo.toString());
        start = jsonParser.getReverseGeo(startJson,Double.toString(la),Double.toString(lo));
        EditText editText = findViewById(R.id.source);
        editText.setText(start.getRepresentName());
    }

    private CameraPosition setCamera(){

        GPStracker gps = new GPStracker(getApplicationContext());
        Location location = gps.getLocation();
        Double la = location.getLatitude();
        Double lo = location.getLongitude();

        CameraPosition cameraPosition = new CameraPosition( new LatLng(la,lo), 16);
        return cameraPosition;
    }

    public void viaEdit(){

        Button viaButton = findViewById(R.id.viaButton);
        viaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViaEditActivity.class);
                intent.putExtra("vias", vias);
                startActivityForResult(intent, 2);
            }
        });
    }

    public void manageBottomSheet(){

        final EditText source = findViewById(R.id.source);
        final EditText dest = findViewById(R.id.dest);
        View background = findViewById(R.id.map);
        final Button search1 = findViewById(R.id.search1);
        final Button search2 = findViewById(R.id.search2);
        behavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));

        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:{
                        source.setFocusable(false);
                        source.setClickable(false);
                        dest.setFocusable(false);
                        dest.setClickable(false);
                    }
                }
                return false;
            }
        });

        source.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:{
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        source.setFocusableInTouchMode(true);
                        dest.setFocusableInTouchMode(true);
                    }
                }
                return false;
            }
        });

        dest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:{
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        source.setFocusableInTouchMode(true);
                        dest.setFocusableInTouchMode(true);
                    }
                }
                return false;
            }
        });

        //출발지 검색
        search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                String keyword = source.getText().toString();
                if(keyword == null)
                    Toast.makeText(MainActivity.this, "출발지를 입력해 주세요", Toast.LENGTH_SHORT);
                else{
                    search(1, keyword);
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(source.getWindowToken(),0);
            }
        });

        //도착지 검색
        search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                String keyword = dest.getText().toString();
                if(keyword == null)
                    Toast.makeText(MainActivity.this, "출발지를 입력해 주세요", Toast.LENGTH_SHORT);
                else{
                    search(2, keyword);
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dest.getWindowToken(),0);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            downBottomSheet();
        else
            finish();
    }

    public void downBottomSheet(){
        if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            Integer height = behavior.getPeekHeight();
            behavior.setPeekHeight(height);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void search(int type, String keyword){

        ApiCaller apiCaller = new ApiCaller();
        String geoStr = apiCaller.geoSearchApi(keyword);
        String placeStr = apiCaller.placeSearchApi(keyword);

        JsonParser parser = new JsonParser();
        ArrayList placeArr = parser.getPlaces(placeStr);
        ArrayList geoArr = parser.getGeos(geoStr);

        //placeArr로 데이터 통합
        for(int i=0; i < geoArr.size();i++){
            placeArr.add(geoArr.get(i));
        }
        if(placeArr.size() == 0)
            Toast.makeText(MainActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        else
            searchResult(placeArr, type);
    }

    //recycler view를 구성하고 선택한  idx를 반환
    public void searchResult(final ArrayList<PlaceCoordination> placeArr, final int type){

        searchResult = findViewById(R.id.searchResult);
        searchResult.setLayoutManager(new LinearLayoutManager(this));
        final SearchResultAdapter adapter = new SearchResultAdapter(placeArr);
        searchResult.setAdapter(adapter);

        adapter.setOnItemClicklistener(new OnPlaceItemClickListener() {
            @Override
            public void onItemClick(SearchResultAdapter.ItemViewHolder holder, View view, int position) {
                setPaths(type, position, placeArr);
            }
        });
    }

    //이 시점에서 선택 액티비티 실행
    public void setPaths(int type, int pos, ArrayList<PlaceCoordination> placeArr){

        if(type == 1){ //출발지
            PlaceCoordination place = placeArr.get(pos);
            Intent intent = new Intent(MainActivity.this, PlaceDecideActivity.class);
            intent.putExtra("place", place);
            intent.putExtra("text", "출발지");
            startActivityForResult(intent, 3);
        }
        else if(type == 2){ //목적지
            PlaceCoordination place = placeArr.get(pos);
            Intent intent = new Intent(MainActivity.this, PlaceDecideActivity.class);
            intent.putExtra("place", place);
            intent.putExtra("text", "목적지");
            startActivityForResult(intent, 4);
        }
    }

    public void pathSearch(){

        final EditText source = findViewById(R.id.source);
        final EditText dest = findViewById(R.id.dest);
        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start == null){
                    Toast.makeText(MainActivity.this, "출발지를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    source.setFocusable(true);
                }
                else if(end == null && vias == null || end == null && vias.size() == 0){
                    vias = null;
                    Toast.makeText(MainActivity.this, "목적지 또는 경유지를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    dest.setFocusable(true);
                }
                else{
                    if(dest.getText().toString() == null || dest.getText().toString().length() == 0)
                        end = null;
                    if(end == null)
                        showDialog("확인", "목적지가 없는 경우, 최적 경로를 탐색하여 목적지를 설정합니다.");
                    else
                        doPathSearch();
                }
            }
        });
    }

    void showDialog(String title, String msg) {

        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        doPathSearch();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    public void doPathSearch(){

        Toast.makeText(MainActivity.this, "경로 탐색을 시작합니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SearchLoadingActivity.class);
        intent.putExtra("start", start);
        intent.putExtra("end", end);
        intent.putExtra("vias", vias);
        startActivityForResult(intent,5);
    }
}
