package com.example.photocalendar_app1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photocalendar_app1.DTA.RecyclerView_adapter;
import com.example.photocalendar_app1.DTO.Filter;

import net.alhazmy13.imagefilter.ImageFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class export_calendar extends AppCompatActivity  {


    private static final String TAG = "AAA";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS =111 ;
    private ImageView img, img_user;
    private ImageView img_cam , img_gall, img_save, img_share, img_filter;
    private final static int CAMERA_REQUEST_CODE = 1;
    private final static int GALLERLY_REQUEST_CODE = 11;
    String[] arraylist_namefilter={"gray","Light","Oil","Old","Tv","Avarange","Gaussain"};
    int[] arrayList_image={R.drawable.gray,R.drawable.light,R.drawable.oil,R.drawable.old,R.drawable.tv,R.drawable.average,R.drawable.gaussian};
    private ArrayList<Filter> arrayList_filter;
    public  RecyclerView_adapter recyclerView_adapter;
    RelativeLayout relativeLayout;
    LinearLayout l1,l2,l3;
    int i=1;
    int k=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_calendar);
        img= findViewById(R.id.img_mark);
        img_user= findViewById(R.id.img_user);
        img_save= findViewById(R.id.img_save);
        img_share= findViewById(R.id.img_share);
        img_filter=findViewById(R.id.img_filter);
        img_gall= findViewById(R.id.img_gallerly);
        relativeLayout = findViewById(R.id.real);
        img_cam= findViewById(R.id.img_camara);





        //lay kich thuuoc goc cua man hinh
        int w = getResources().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (w*4)/3);
        RelativeLayout raRelativeLayout= findViewById(R.id.real);
        raRelativeLayout.setLayoutParams(params);
        //


        //get imgae_frame
        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("img_frame");
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
           try {
               int a= bundle.getInt("img_frame");
               Log.d("AAA",""+a);
               img.setImageResource(a);
           }catch (Exception e)
           {
               Toast.makeText(this,"loi", Toast.LENGTH_SHORT).show();
           }
       }

       //ckeck per mission
        if(checkAndRequestPermissions(export_calendar.this))
        {

            choseimage();
        }


        // save imgae
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bm= loadBitmapFromView(relativeLayout);
                Log.d("AAA",""+bm);
                saveImage(bm);
                Toast.makeText( export_calendar.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });


        // share image with other app
        img_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bitmap bitmap= loadBitmapFromView(relativeLayout);
                Uri uri=saveImageshare(bitmap);
                shareImageUri(uri);

            }
        });


        //add filter
        addfilte();

        l1= findViewById(R.id.Linear_storge);
        l2= findViewById(R.id.Liner_filter);
        l3=findViewById(R.id.linear_export);



        img_filter.setOnClickListener(new View.OnClickListener() {
            boolean visilble;
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(l3);
                visilble=!visilble;

                l2.setVisibility(visilble ? View.VISIBLE: View.GONE);
                l1.setVisibility(visilble ? View.GONE: View.VISIBLE);


            }
        });



    }

    private void addfilte() {

        arrayList_filter = new ArrayList<>();
        for (int i=0; i<arrayList_image.length; i++)
        {
            Filter filter =new Filter();
            filter.setFiltername(arraylist_namefilter[i]);
            filter.setImgae_frame(arrayList_image[i]);
            arrayList_filter.add(filter);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView_adapter adapter = new RecyclerView_adapter(this, arrayList_filter);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerView_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bitmap bitmap= loadBitmapFromView(relativeLayout);
              switch (arrayList_filter.get(position).getFiltername())
              {
                  case "gray":
                      img.setImageBitmap(ImageFilter.applyFilter(bitmap,ImageFilter.Filter.GRAY));
                      break;
                  case "Light":
                      img.setImageBitmap(ImageFilter.applyFilter(bitmap,ImageFilter.Filter.LIGHT));
                      break;
                  case "Oil":
                      img.setImageBitmap(ImageFilter.applyFilter(bitmap,ImageFilter.Filter.RELIEF));
                      break;
                  case "Old":
                      img.setImageBitmap(ImageFilter.applyFilter(bitmap,ImageFilter.Filter.OLD));
                      break;
                  case "Tv":
                      img.setImageBitmap(ImageFilter.applyFilter(bitmap,ImageFilter.Filter.TV));
                      break;
                  case "Avarange":
                      img.setImageBitmap(ImageFilter.applyFilter(bitmap,ImageFilter.Filter.AVERAGE_BLUR));
                      break;
                  case "Gaussain":
                      img.setImageBitmap(ImageFilter.applyFilter(bitmap,ImageFilter.Filter.GAUSSIAN_BLUR));
                      break;
                  default:
                      img.setImageBitmap(bitmap);
              }
            }
        });


    }


    //get image from view
    public static Bitmap loadBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("AAA", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    //save as storge
    private void saveImage(Bitmap bitmap1) {
        OutputStream outputStream=null;
        String fileName=String.format("%d.png",System.currentTimeMillis());
        File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                +"/Pictures",fileName);
        try {
           outputStream=new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap1.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // create uri
    private Uri saveImageshare(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.example.photocalendar_app1.fileprovider", file);
        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }


    //Inten share
    private void shareImageUri(Uri uri){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }



    //ckeck permisiion
    private void choseimage() {
    img_cam.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,CAMERA_REQUEST_CODE);
        }
    });


    img_gall.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , GALLERLY_REQUEST_CODE);
        }
    });
}

    public static boolean checkAndRequestPermissions(final Activity context) {
    int WExtstorePermission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int cameraPermission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.CAMERA);
    List<String> listPermissionsNeeded = new ArrayList<>();
    if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.CAMERA);
    }
    if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded
                .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    if (!listPermissionsNeeded.isEmpty()) {
        ActivityCompat.requestPermissions(context, listPermissionsNeeded
                        .toArray(new String[listPermissionsNeeded.size()]),
                REQUEST_ID_MULTIPLE_PERMISSIONS);
        return false;
    }
    return true;
}
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(export_calendar.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(export_calendar.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    choseimage();
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        img_user.setImageBitmap(selectedImage);
                    }
                    break;
                case GALLERLY_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                img_user.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }



}
