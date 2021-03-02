package com.example.taxifeecalc.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxifeecalc.Model.PlaceCoordination;
import com.example.taxifeecalc.R;

import java.util.ArrayList;

public class PathDetailAdapter extends RecyclerView.Adapter<PathDetailAdapter.ItemViewHolder>{

    private ArrayList<PlaceCoordination> itemList;

    public PathDetailAdapter(ArrayList<PlaceCoordination> list){
        this.itemList = list;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        protected TextView title;
        protected TextView address;
        protected TextView meta;

        public ItemViewHolder(@NonNull final View itemView){
            super(itemView);
            this.title = itemView.findViewById(R.id.pathTitle);
            this.address = itemView.findViewById(R.id.pathAddress);
            this.meta = itemView.findViewById(R.id.pathMeta);
        }

        public void onBind(PlaceCoordination vo){
            if(vo.getTitle().length() > 0) this.title.setText(vo.getTitle());
            else this.title.setText(vo.getAddress());
            this.address.setText(vo.getAddress());
            this.meta.setText(vo.getMeta());
        }
    }

    @NonNull
    @Override
    public PathDetailAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_detail_element, parent, false);
        PathDetailAdapter.ItemViewHolder viewHolder = new PathDetailAdapter.ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PathDetailAdapter.ItemViewHolder holder, final int pos) {
        holder.onBind(itemList.get(pos));
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }
}
