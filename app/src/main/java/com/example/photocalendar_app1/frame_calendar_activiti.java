package com.example.photocalendar_app1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.photocalendar_app1.DTA.fram_adapter;
import com.example.photocalendar_app1.DTO.Abum_frame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class frame_calendar_activiti extends AppCompatActivity {

    int[] arr_frame ={R.drawable.anh01, R.drawable.anh02,R.drawable.anh03,
            R.drawable.anh04,R.drawable.anh05,R.drawable.anh06,R.drawable.anh07,
            R.drawable.anh08,R.drawable.anh09,R.drawable.anh10,R.drawable.anh11,R.drawable.anh12};
    String[] arr_name_frame={"January","Febuary","March","April","May","June","Junly","August","September","Octorber","November","December"};
    GridView giGridView;
    ArrayList<Abum_frame> arrayList;

    private static String img_user_c;
    private static String img_user_g;


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_calendar_activiti);
        giGridView=(findViewById(R.id.grv_frame));
        LinearLayout ln=findViewById(R.id.linear_pickfame);

        arrayList= new ArrayList<Abum_frame>();
        for(int i = 0; i <arr_frame.length; i++)
        {
            Abum_frame abum_frame= new Abum_frame();
            abum_frame.setFrame(arr_frame[i]);
            abum_frame.setName_frame(arr_name_frame[i]);
            arrayList.add(abum_frame);

        }


        //
        Bitmap bmp =null;
        String filename = getIntent().getStringExtra("img_g");
        img_user_g=filename;
        String filename1 = getIntent().getStringExtra("img_c");
        img_user_c=filename1;
        if(filename!=null) {
            try {
                FileInputStream is = this.openFileInput(filename);
                bmp = BitmapFactory.decodeStream(is);
                Drawable drawable = new BitmapDrawable(this.getResources(), bmp);
                ln.setBackground(drawable);

                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(filename1!=null)
        {
            try {
                FileInputStream is = this.openFileInput(filename1);
                bmp = BitmapFactory.decodeStream(is);
                Drawable drawable = new BitmapDrawable(this.getResources(), bmp);
                giGridView.setBackground(drawable);
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //send data
//       if (filename!=null)
//       {
//           try {
//               img_user_g = ""+filename;
//               FileOutputStream stream = this.openFileOutput(img_user_g, Context.MODE_PRIVATE);;
//               bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//               stream.close();
//               bmp.recycle();
//           } catch (FileNotFoundException e) {
//               e.printStackTrace();
//           } catch (IOException e) {
//               e.printStackTrace();
//           }
//       }
//       else if(filename1!=null)
//       {
//           try {
//               img_user_c = ""+filename;
//               FileOutputStream stream = this.openFileOutput(img_user_c, Context.MODE_PRIVATE);;
//               bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//               stream.close();
//           } catch (FileNotFoundException e) {
//               e.printStackTrace();
//           } catch (IOException e) {
//               e.printStackTrace();
//           }
//       }

       //
        ///
        ///
        ///


        //
        fram_adapter fram_adapter= new fram_adapter(this,R.layout.frame_custom,arrayList);
        fram_adapter.notifyDataSetChanged();
        giGridView.setAdapter(fram_adapter);

        //chọm frame
        pic_frame();

    }


    private void pic_frame() {
        giGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Abum_frame abum_frame= arrayList.get(position);
                int int_img=+abum_frame.getFrame();
                Intent intent= new Intent(frame_calendar_activiti.this, export_calendar.class);
                intent.putExtra("img_frame",int_img);
                intent.putExtra("img_user",img_user_c);
                intent.putExtra("img_user",img_user_g);
                Log.d("AAA",""+img_user_g);
                startActivity(intent);
            }
        });
    }

}