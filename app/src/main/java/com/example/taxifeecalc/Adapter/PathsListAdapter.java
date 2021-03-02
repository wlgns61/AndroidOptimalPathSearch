package com.example.taxifeecalc.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxifeecalc.Model.FromToInfo;
import com.example.taxifeecalc.R;

import java.util.ArrayList;

public class PathsListAdapter extends RecyclerView.Adapter<PathsListAdapter.PathItemViewHolder> implements OnPathItemClickListener {

    private ArrayList<FromToInfo> itemList;
    OnPathItemClickListener listener;
    private int position;

    public PathsListAdapter(ArrayList<FromToInfo> list){
        this.itemList = list;
    }

    public class PathItemViewHolder extends RecyclerView.ViewHolder{

        protected TextView fee;
        protected TextView time;
        protected TextView dist;
        protected TextView searchType;

        public PathItemViewHolder(@NonNull final View itemView){
            super(itemView);
            this.fee = itemView.findViewById(R.id.fee);
            this.time = itemView.findViewById(R.id.time);
            this.dist = itemView.findViewById(R.id.dist);
            this.searchType = itemView.findViewById(R.id.searchType);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null)
                        listener.onItemClick(PathsListAdapter.PathItemViewHolder.this, v, position);
                }
            });
        }

        public void onBind(FromToInfo vo){
            this.fee.setText("택시 요금: " + vo.getStringFee() + "원");
            this.time.setText("시간: " + vo.getStringTime());
            this.dist.setText("거리: " + vo.getStringDist() + "km");
            this.searchType.setText(vo.getName());
        }
    }

    @NonNull
    @Override
    public PathsListAdapter.PathItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_element, parent, false);
        PathsListAdapter.PathItemViewHolder viewHolder = new PathsListAdapter.PathItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PathsListAdapter.PathItemViewHolder holder, final int pos) {
        holder.onBind(itemList.get(pos));
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }
    @Override
    public void onItemClick(PathsListAdapter.PathItemViewHolder holder, View view, int position){
        if(listener != null)
            listener.onItemClick(holder, view, position);
    }
    public void setOnItemClicklistener(OnPathItemClickListener listener){
        this.listener = listener;
    }

    public void setPosition(int position){
        this.position = position;
    }
    public int getPosition(){
        return this.position;
    }
    public ArrayList getItemList(){
        return this.itemList;
    }
    public void addItem(FromToInfo item){
        this.itemList.add(item);
    }
    
}
