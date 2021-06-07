package com.example.photocalendar_app1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.mukesh.image_processing.ImageProcessor;

import net.alhazmy13.imagefilter.ImageFilter;

import org.wysaid.nativePort.CGEDeformFilterWrapper;
import org.wysaid.nativePort.CGENativeLibrary;
import org.wysaid.view.ImageGLSurfaceView;

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
    String[] arraylist_namefilter={"none","filter1","filter2","filter3","filter4","filter5","filter6","filter7"};
    int[] arrayList_image={R.drawable.filternone,R.drawable.filter1,R.drawable.filter2,R.drawable.filter3,R.drawable.filter4,R.drawable.filter5,R.drawable.filter6,R.drawable.filter7};
    private ArrayList<Filter> arrayList_filter;
    public  RecyclerView_adapter recyclerView_adapter;
    private RelativeLayout relativeLayout;
    private LinearLayout l1,l2;
    RelativeLayout l3;
    private Bitmap bitmap_nochange;
    SeekBar seekBar_filter;
    CGENativeLibrary cgeNativeLibrary;
    CGEDeformFilterWrapper mDeformWrapper;

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
        l1= findViewById(R.id.Linear_storge);
        l2= findViewById(R.id.Liner_filter);
        l3=findViewById(R.id.linear_export);
        seekBar_filter= findViewById(R.id.seekBar);


        //lay kich thuuoc goc cua man hinh

        int w = getResources().getDisplayMetrics().widthPixels;
        Log.d("AAA","weigth"+w);
//        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (w*4)/3);
//        RelativeLayout raRelativeLayout= findViewById(R.id.real);
//        raRelativeLayout.setLayoutParams(params);
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


        //set image nochange\
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                bitmap_nochange=loadBitmapFromView(relativeLayout);
                Log.d("AAA","a:"+bitmap_nochange);

            }
        });


        // save imgae
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bm= loadBitmapFromView(relativeLayout);
                Log.d("AAA",""+bm);
                saveImage11(bm);
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


        seekBar_filter.setVisibility(View.GONE);



        img_filter.setOnClickListener(new View.OnClickListener() {
            boolean visilble;
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                seekBar_filter.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(l3);
                visilble=!visilble;
                l2.setVisibility(visilble ? View.VISIBLE: View.GONE);
                l1.setVisibility(visilble ? View.GONE: View.VISIBLE);

            }


        });



    }

    private Bitmap seekbarfilter(Bitmap bitmap ,String ruleString)
    {
        seekBar_filter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onProgressChanged(SeekBar seekBar,final int progress, boolean fromUser) {
                seekBar.setMax(200);
                seekBar.setMin(0);
                if(progress<10)
                {
                    img.setImageBitmap(bitmap);
                }
                else {
                    float intensity = progress/100f;
                    img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(bitmap,ruleString,intensity));}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return bitmap;
    }



    //add item Recyclerview and event click filter
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
                Bitmap bitmap=bitmap_nochange;
                Log.d("AAA","aaa:"+bitmap);


              switch (arrayList_filter.get(position).getFiltername())
              {
                  case "none":
                      seekBar_filter.setVisibility(View.GONE);
                      img.setImageBitmap(bitmap);
                      break;
                  case "filter1":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString1="@adjust hsv -0.5 -0.5 -0.5 -0.5 -0.5 -0.5 @curve R(0, 0)(129, 148)(255, 255)G(0, 0)(92, 77)(175, 189)(255, 255)B(0, 0)(163, 144)(255, 255)" ;
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString1));
                      break;
                  case "filter2":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString2="@adjust hsv 0.3 -0.5 -0.3 0 0.35 -0.2 @curve R(0, 0)(111, 163)(255, 255)G(0, 0)(72, 56)(155, 190)(255, 255)B(0, 0)(103, 70)(212, 244)(255, 255)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString2));
                      break;
                  case "filter3":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString3="@curve R(40, 40)(86, 148)(255, 255)G(0, 28)(67, 140)(142, 214)(255, 255)B(0, 100)(103, 176)(195, 174)(255, 255) @adjust hsv 0.32 0 -0.5 -0.2 0 -0.4";

                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString3));
                      break;
                  case "filter4":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString4="@curve R(0, 0)(63, 101)(200, 84)(255, 255)G(0, 0)(86, 49)(180, 183)(255, 255)B(0, 0)(19, 17)(66, 41)(97, 92)(137, 156)(194, 211)(255, 255)RGB(0, 0)(82, 36)(160, 183)(255, 255) ";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString4));
                      break;
                  case "filter5":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString5="@adjust exposure 0.98 ";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString5));
                      break;
                  case "filter6":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString6="#unpack @blur lerp 0.75";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString6));
                      break;
                  case "filter7":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString7="#unpack @dynamic wave 1";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString7));
                      break;
                  default:
                      img.setImageBitmap(bitmap_nochange);
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

//    private void saveImage(Bitmap bitmap1) {
//        OutputStream outputStream=null;
//        String fileName=String.format("%d.png",System.currentTimeMillis());
//        File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                +"/Pictures",fileName);
//        try {
//           outputStream=new FileOutputStream(outFile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        bitmap1.compress(Bitmap.CompressFormat.PNG,100,outputStream);
//        try {
//            outputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    
    //save bitmap to storge
    private void saveImage11(Bitmap bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name));
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, this.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    this.getContentResolver().update(uri, values, null, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name));

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // create uri CONTAIN  a bttmap
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
