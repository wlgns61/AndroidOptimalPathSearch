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

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder> implements OnPlaceItemClickListener {

    private ArrayList<PlaceCoordination> itemList;
    OnPlaceItemClickListener listener;
    private int position;

    public SearchResultAdapter(ArrayList<PlaceCoordination> list){
        this.itemList = list;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        protected TextView title;
        protected TextView address;

        public ItemViewHolder(@NonNull final View itemView){
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.address = itemView.findViewById(R.id.address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null)
                        listener.onItemClick(ItemViewHolder.this, v, position);
                }
            });
        }

        public void onBind(PlaceCoordination vo){
            if(vo.getTitle().length() > 0) this.title.setText(vo.getTitle());
            else this.title.setText(vo.getAddress());
            this.address.setText(vo.getAddress());
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int pos) {
        holder.onBind(itemList.get(pos));
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }
    @Override
    public void onItemClick(ItemViewHolder holder, View view, int position){
        if(listener != null)
            listener.onItemClick(holder, view, position);
    }
    public void setOnItemClicklistener(OnPlaceItemClickListener listener){
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
    public void addItem(PlaceCoordination item){
        this.itemList.add(item);
    }

}
