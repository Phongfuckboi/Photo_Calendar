package com.example.photocalendar_app1.DTA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.photocalendar_app1.DTO.Abum_frame;
import com.example.photocalendar_app1.R;

import java.util.List;

public class fram_adapter extends BaseAdapter {
    Context context;
    int layout;
    List<Abum_frame> list;
    public  fram_adapter(Context context, int layout, List<Abum_frame> list)
    {
        this.context=context;
        this.layout=layout;
        this.list=list;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public class Viewholder{
        ImageView imageView;
        TextView txtTextView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View holder= convertView;
        LayoutInflater inflater=  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (holder==null)
        {
           holder=inflater.inflate(layout,parent,false);
           Viewholder viewholder= new Viewholder();
           viewholder.imageView=(ImageView) holder.findViewById(R.id.img_frame);
           viewholder.txtTextView=holder.findViewById(R.id.txt_frame);
           holder.setTag(viewholder);
        }

        Viewholder viewholder=(Viewholder) holder.getTag();
        viewholder.imageView.setImageResource(list.get(position).getFrame());
        viewholder.txtTextView.setText(list.get(position).getName_frame());

        return holder;
    }
    private int getImageResForPosition(int position) {
        Abum_frame fram = list.get(position);
        int a=fram.getFrame();
        return  a;

    }

}
