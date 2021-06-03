package com.example.photocalendar_app1.DTA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.photocalendar_app1.R;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private ArrayList<String> arrayList_name=new ArrayList<>();
    private  ArrayList<String>arrayList_image=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
    private void addfilte() {

        arrayList_image.add("https://cloud.githubusercontent.com/assets/4659608/13037677/6060dfe4-d397-11e5-8d50-0cf960914a8b.png");
        arrayList_name.add("Gray");

        arrayList_image.add("https://cloud.githubusercontent.com/assets/4659608/13037676/605da3c4-d397-11e5-93cb-22a4895e59e2.png");
        arrayList_name.add("Relief");

        arrayList_image.add("https://cloud.githubusercontent.com/assets/4659608/13037708/0cb05126-d398-11e5-9b70-09cc415ac791.png");
        arrayList_name.add("Average blur");

        arrayList_image.add("https://cloud.githubusercontent.com/assets/4659608/13037707/0cafb996-d398-11e5-9610-48467208441b.png");
        arrayList_name.add("OIL");

        arrayList_image.add("https://cloud.githubusercontent.com/assets/4659608/13037677/6060dfe4-d397-11e5-8d50-0cf960914a8b.png");
        arrayList_name.add("NEON");

        arrayList_image.add("https://cloud.githubusercontent.com/assets/4659608/13060886/9f6c03a4-d445-11e5-9771-36bf3e20591f.png");
        arrayList_name.add("Pixelate");

        initRecycirleView();



    }

    private void initRecycirleView() {

    }
}