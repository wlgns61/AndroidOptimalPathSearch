package com.example.taxifeecalc.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;
import com.example.taxifeecalc.Adapter.OnPlaceItemClickListener;
import com.example.taxifeecalc.Adapter.SearchResultAdapter;

import java.util.ArrayList;

public class ViaEditActivity extends FragmentActivity {

    public ArrayList<PlaceCoordination> viaList;
    RecyclerView viasResult;
    int delpos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.via_edit);
        Intent intent = getIntent();

        viaList = (ArrayList<PlaceCoordination>) intent.getSerializableExtra("vias");
        if(viaList == null) viaList = new ArrayList<>();

        setAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemDelete();
    }

    private void setAction(){

        makeViaList(viaList);
        setAddButton();
        setSubmitButton();
    }

    private void setAddButton(){
        Button searchButton = findViewById(R.id.viaAddButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viaList != null && viaList.size() >= 4){
                    Toast.makeText(ViaEditActivity.this, "경유지는 최대 4개까지 선택가능합니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), ViaSearchActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                PlaceCoordination place = (PlaceCoordination) data.getSerializableExtra("place");
                viaList.add(place);
                makeViaList(viaList);
            }
        }
    }

    private void setSubmitButton(){
        Button submitButton = findViewById(R.id.viaSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("viaList", viaList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void makeViaList(ArrayList<PlaceCoordination> list){

        this.viaList = list;

        if(viaList != null){
            try{
                viasResult = findViewById(R.id.viaList);
                viasResult.setLayoutManager(new LinearLayoutManager(this));
                final SearchResultAdapter adapter = new SearchResultAdapter(this.viaList);
                viasResult.setAdapter(adapter);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    void itemDelete(){

        viasResult = findViewById(R.id.viaList);
        viasResult.setLayoutManager(new LinearLayoutManager(this));
        SearchResultAdapter adapter = new SearchResultAdapter(viaList);
        viasResult.setAdapter(adapter);

        adapter.setOnItemClicklistener(new OnPlaceItemClickListener() {
            @Override
            public void onItemClick(SearchResultAdapter.ItemViewHolder holder, View view, int position) {
                String msg = viaList.get(position).getRepresentName() + "을(를) 삭제하시겠습니까?";
                showDialog("삭제",msg, position);
            }
        });
    }

    void showDialog(String title, String msg, int pos) {

        delpos = pos;

        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(ViaEditActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        viaList.remove(delpos);
                        makeViaList(viaList);
                        itemDelete();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

}
