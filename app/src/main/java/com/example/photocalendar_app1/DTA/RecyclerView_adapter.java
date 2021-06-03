package com.example.photocalendar_app1.DTA;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photocalendar_app1.DTO.Filter;
import com.example.photocalendar_app1.R;
import com.example.photocalendar_app1.export_calendar;

//import net.alhazmy13.imagefilter.ImageFilter;

import org.jetbrains.annotations.NotNull;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerView_adapter extends RecyclerView.Adapter<RecyclerView_adapter.viewholder> {


    private ArrayList<com.example.photocalendar_app1.DTO.Filter> arrayList_filter;
    private Context context;
    private  OnItemClickListener onItemClickListener;


    public RecyclerView_adapter(export_calendar context, ArrayList<com.example.photocalendar_app1.DTO.Filter> arrayList_filter,OnItemClickListener onItemClickListener) {
        this.arrayList_filter = arrayList_filter;
        this.context = context;
        this.onItemClickListener=onItemClickListener;

    }

    @NonNull
    @NotNull
    @Override
    public viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item,parent,false);

        return new viewholder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView_adapter.viewholder holder, int position) {
        Filter filter= arrayList_filter.get(position);
        Glide.with(context)
                .asBitmap()
                .load(arrayList_filter.get(position)).into(holder.circleImageView);

       holder.text_namefilter.setText(filter.getFiltername());
    }


    @Override
    public int getItemCount() {
        return arrayList_filter.size();
    }

    public  class  viewholder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        CircleImageView circleImageView;
        TextView text_namefilter;
       OnItemClickListener onItemClickListener;
        public viewholder(@NonNull @NotNull View itemView , final OnItemClickListener onItemClickListener) {
            super(itemView);
            circleImageView= itemView.findViewById(R.id.image_view);
            text_namefilter= itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick((getAdapterPosition()));
        }
    }
    public interface OnItemClickListener{

        void onItemClick(int position);
    }
}
