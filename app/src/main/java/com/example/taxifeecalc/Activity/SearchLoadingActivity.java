package com.example.taxifeecalc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.taxifeecalc.Model.FromToInfo;
import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;
import com.example.taxifeecalc.Service.SearchOptimalPaths;

import java.util.ArrayList;

public class SearchLoadingActivity extends FragmentActivity {
    PlaceCoordination start;
    PlaceCoordination end;
    ArrayList<PlaceCoordination> vias;
    ArrayList<FromToInfo> paths = new ArrayList<>();
    SearchOptimalPaths searchOptimalPaths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setPaths();
    }

    private void setPaths(){

        final Intent intent = getIntent();
        start = (PlaceCoordination)intent.getSerializableExtra("start");
        end = (PlaceCoordination)intent.getSerializableExtra("end");
        vias  = (ArrayList<PlaceCoordination>)intent.getSerializableExtra("vias");

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                searchOptimalPaths = new SearchOptimalPaths(start, end, vias);
                Looper.prepare();
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            FromToInfo normal = searchOptimalPaths.getNormal();
                            FromToInfo minimumFee = searchOptimalPaths.getMinimumFee();
                            FromToInfo minimumDist = searchOptimalPaths.getMinimumDist();
                            FromToInfo minimumTime = searchOptimalPaths.getMinimumTime();
                            if(minimumFee != null) paths.add(minimumFee);
                            if(minimumDist != null) paths.add(minimumDist);
                            if(minimumTime != null) paths.add(minimumTime);
                            if(normal != null) paths.add(normal);
                            if(paths.size() >= 4){
                                Intent retintent = getIntent();
                                retintent.putExtra("paths", paths);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                Looper.loop();
            }
        });
        thread.start();
    }
}
