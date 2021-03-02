package com.example.taxifeecalc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxifeecalc.Service.ApiCaller;
import com.example.taxifeecalc.Service.JsonParser;
import com.example.taxifeecalc.Adapter.OnPlaceItemClickListener;
import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;
import com.example.taxifeecalc.Adapter.SearchResultAdapter;

import java.util.ArrayList;

public class ViaSearchActivity extends FragmentActivity {

    public ArrayList<PlaceCoordination> viaList;
    RecyclerView searchResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.via_search);
        setAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1) {
                PlaceCoordination place = (PlaceCoordination) data.getSerializableExtra("place");
                Intent intent = getIntent();
                intent.putExtra("place", place);
                setResult(RESULT_OK, intent);
                this.finish();
            }
        }
    }

    private void setAction(){
        search();
        inputText();
    }

    private void inputText(){

        final EditText editText = findViewById(R.id.via);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
                editText.setEnabled(true);
                return false;
            }
        });

    }

    private void search(){

        Button button = findViewById(R.id.search3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.via);
                String text = editText.getText().toString();
                if(text != null && text.length() > 0){
                    searchAction(text);
                }
                else{
                    Toast.makeText(ViaSearchActivity.this, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchAction(String keyword){

        ApiCaller apiCaller = new ApiCaller();
        String geoStr = apiCaller.geoSearchApi(keyword);
        String placeStr = apiCaller.placeSearchApi(keyword);

        JsonParser parser = new JsonParser();
        ArrayList<PlaceCoordination> placeArr = parser.getPlaces(placeStr);
        ArrayList<PlaceCoordination> geoArr = parser.getGeos(geoStr);

        //placeArr로 데이터 통합
        for(int i=0; i < geoArr.size();i++){
            placeArr.add(geoArr.get(i));
        }
        if(placeArr.size() == 0)
            Toast.makeText(ViaSearchActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();

        makeViaList(placeArr);
    }

    private void makeViaList(ArrayList<PlaceCoordination> list){

        this.viaList = list;

        if(viaList != null && viaList.size() > 0){
            searchResult = findViewById(R.id.viaSearchResult);
            searchResult.setLayoutManager(new LinearLayoutManager(this));
            SearchResultAdapter adapter = new SearchResultAdapter(viaList);
            searchResult.setAdapter(adapter);
            adapter.setOnItemClicklistener(new OnPlaceItemClickListener() {
                @Override
                public void onItemClick(SearchResultAdapter.ItemViewHolder holder, View view, int position) {
                    decide(position);
                }
            });
        }
    }

    private void decide(int pos){

        if(viaList.get(pos) != null){
            PlaceCoordination place = viaList.get(pos);
            Intent intent = new Intent(ViaSearchActivity.this, PlaceDecideActivity.class);
            intent.putExtra("place", place);
            intent.putExtra("text", "경유지");
            startActivityForResult(intent, 1);

        }
    }

}
