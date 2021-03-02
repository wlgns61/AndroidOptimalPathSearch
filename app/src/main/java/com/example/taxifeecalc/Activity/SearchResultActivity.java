package com.example.taxifeecalc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxifeecalc.Model.FromToInfo;
import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;
import com.example.taxifeecalc.Adapter.OnPathItemClickListener;
import com.example.taxifeecalc.Adapter.PathsListAdapter;

import java.util.ArrayList;

public class SearchResultActivity extends FragmentActivity {

    ArrayList<FromToInfo> paths = new ArrayList<>();
    RecyclerView pathResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paths_search_result);
        setAction();
    }

    public void setAction(){
        setPaths();
        registerAdapter();
        setBackButton();
        setInfo();
    }

    private void setPaths(){
        Intent intent = getIntent();
        paths = (ArrayList<FromToInfo>) intent.getSerializableExtra("paths");
    }

    public void registerAdapter(){

        pathResult = findViewById(R.id.pathList);
        pathResult.addItemDecoration(new DividerItemDecoration(this, 1));
        pathResult.setLayoutManager(new LinearLayoutManager(this));
        PathsListAdapter adapter = new PathsListAdapter(paths);
        pathResult.setAdapter(adapter);

        adapter.setOnItemClicklistener(new OnPathItemClickListener() {
            @Override
            public void onItemClick(PathsListAdapter.PathItemViewHolder holder, View view, int position) {
                FromToInfo fromToInfo = paths.get(position);
                Intent intent = new Intent(SearchResultActivity.this, SearchDetailActivity.class);
                intent.putExtra("path", fromToInfo);
                startActivity(intent);
            }
        });
    }

    public void setBackButton(){
        Button btn = findViewById(R.id.backButton1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setInfo(){
        Intent intent = getIntent();
        boolean isEnd = intent.getBooleanExtra("isEnd", false);

        ArrayList <PlaceCoordination> path = paths.get(0).getPaths();
        TextView start = findViewById(R.id.startInfo);
        TextView end = findViewById(R.id.endInfo);
        TextView via = findViewById(R.id.viaInfo);

        if(isEnd == true){
            if(path.size() >= 3){

                String viaInfo =  "경유지: " + path.get(1).getRepresentName();
                if(path.size() > 3){
                    viaInfo += " 외 " + (path.size() - 3) + "개";
                }
                start.setText("출발지: " + path.get(0).getRepresentName());
                end.setText("목적지: " + path.get(path.size() - 1).getRepresentName());
                via.setText(viaInfo);
            }
            else{
                start.setText("출발지: " + path.get(0).getRepresentName());
                via.setText("경유지: 없음");
                end.setText("목적지: " + path.get(path.size() - 1).getRepresentName());
            }
        }
        else{
            String viaInfo =  "경유지: " + path.get(1).getRepresentName();
            if(path.size() > 2){
                viaInfo += " 외 " + (path.size() - 2) + "개";
            }

            start.setText("출발지: " + path.get(0).getRepresentName());
            end.setText("목적지: 경유지 중 택1");
            via.setText(viaInfo);
        }
    }
}
