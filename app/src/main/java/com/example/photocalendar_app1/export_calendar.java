package com.example.photocalendar_app1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
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

import com.bumptech.glide.Glide;
import com.example.photocalendar_app1.DTA.ExifUtil;
import com.example.photocalendar_app1.DTA.FileUltils;
import com.example.photocalendar_app1.DTA.RecyclerView_adapter;
import com.example.photocalendar_app1.DTO.Filter;
import com.mukesh.image_processing.ImageProcessor;
import com.yalantis.ucrop.UCrop;

import net.alhazmy13.imagefilter.ImageFilter;

import org.wysaid.nativePort.CGEDeformFilterWrapper;
import org.wysaid.nativePort.CGENativeLibrary;
import org.wysaid.view.ImageGLSurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class export_calendar extends AppCompatActivity  {


    private static final String TAG = "AAA";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS =111 ;
    private ImageView img, img_user;
    private ImageView img_cam , img_gall, img_save, img_share, img_filter;
    private final static int CAMERA_REQUEST_CODE = 1;
    private final static int GALLERLY_REQUEST_CODE = 11;
    private String[] arraylist_namefilter={"none","filter1","filter2","filter3","filter4","filter5","filter6","filter7","filter8","filter9","filter10"
            ,"filter11","filter12","filter13","filter14","filter15","filter16","filter17","filter18","filter19","filter20"};
    private int[] arrayList_image={R.drawable.filternone,R.drawable.filter1,R.drawable.filter2,R.drawable.filter3,R.drawable.filter4,R.drawable.filter5,R.drawable.filter6,R.drawable.filter7,R.drawable.filter8
            ,R.drawable.filter9,R.drawable.filter10,R.drawable.filter11,R.drawable.filter12,R.drawable.filter13,R.drawable.filter14,R.drawable.filter15,R.drawable.filter16
            ,R.drawable.filter17,R.drawable.filter18,R.drawable.filter19,R.drawable.filter20};
    private ArrayList<Filter> arrayList_filter;
    public  RecyclerView_adapter recyclerView_adapter;
    private RelativeLayout relativeLayout,relativeLayout_ckeck;
    private LinearLayout l1,l2;
    RelativeLayout l3;
    private Bitmap bitmap_nochange;
    private SeekBar seekBar_filter;
    private CGENativeLibrary cgeNativeLibrary;
    private CGEDeformFilterWrapper mDeformWrapper;
    Uri imageUri;
    ContentValues values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_calendar);

        //
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
        seekBar_filter.setMax(200);



        //

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
                Toast.makeText(export_calendar.this,"Shared",Toast.LENGTH_SHORT).show();
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
                img.setImageBitmap(bitmap_nochange);
                seekBar_filter.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(l3);
                visilble=!visilble;
                l2.setVisibility(visilble ? View.VISIBLE: View.GONE);
                l1.setVisibility(visilble ? View.GONE: View.VISIBLE);

            }


        });

    }

    //scroll filter.
    private Bitmap seekbarfilter(Bitmap bitmap ,String ruleString)
    {
        seekBar_filter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onProgressChanged(SeekBar seekBar,final int progress, boolean fromUser) {

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
                seekBar_filter.setProgress(100);
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
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString1,1));
                      break;
                  case "filter2":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString2="@adjust hsv 0.3 -0.5 -0.3 0 0.35 -0.2 @curve R(0, 0)(111, 163)(255, 255)G(0, 0)(72, 56)(155, 190)(255, 255)B(0, 0)(103, 70)(212, 244)(255, 255)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString2));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString2,1));
                      break;
                  case "filter3":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString3="@curve R(40, 40)(86, 148)(255, 255)G(0, 28)(67, 140)(142, 214)(255, 255)B(0, 100)(103, 176)(195, 174)(255, 255) @adjust hsv 0.32 0 -0.5 -0.2 0 -0.4";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString3));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString3,1));

                      break;
                  case "filter4":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString4="@curve R(0, 0)(63, 101)(200, 84)(255, 255)G(0, 0)(86, 49)(180, 183)(255, 255)B(0, 0)(19, 17)(66, 41)(97, 92)(137, 156)(194, 211)(255, 255)RGB(0, 0)(82, 36)(160, 183)(255, 255) ";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString4));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString4,1));;
                      break;
                  case "filter5":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString5="@adjust exposure 0.98 ";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString5));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString5,1));
                      break;
                  case "filter6":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString6="#unpack @blur lerp 0.75";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString6));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString6,1));
                      break;
                  case "filter7":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString7="#unpack @dynamic wave 1";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString7));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString7,1));
                      break;
                  case "filter8":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString8="@style haze 0.5 -0.14 1 0.8 1 ";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString8));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString8,1));
                      break;
                  case "filter9":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString9="@curve G(0, 0)(101, 127)(255, 255) @pixblend colordodge 0.937 0.482 0.835 1 20";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString9));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString9,1));
                      break;
                  case "filter10":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString10="@curve R(0, 0)(117, 95)(155, 171)(179, 225)(255, 255)G(0, 0)(94, 66)(155, 176)(255, 255)B(0, 0)(48, 59)(141, 130)(255, 224)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString10));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString10,1));
                      break;
                  case "filter11":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString11=  "@curve R(0, 4)(255, 244)G(0, 0)(255, 255)B(0, 84)(255, 194)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString11));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString11,1));
                      break;
                  case "filter12":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString12= "@curve R(14, 0)(51, 42)(135, 138)(191, 202)(234, 255)G(11, 6)(78, 77)(178, 185)(242, 250)B(11, 0)(22, 10)(72, 60)(171, 162)(217, 209)(255, 255)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString12));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString12,1));
                      break;
                  case "filter13":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString13= "@curve R(17, 0)(37, 18)(75, 52)(238, 255)G(16, 0)(53, 32)(113, 92)(236, 255)B(16, 0)(80, 57)(171, 164)(235, 255)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString13));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString13,1));
                      break;
                  case "filter14":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString14= "@curve G(0, 0)(101, 127)(255, 255) @pixblend colordodge 0.937 0.482 0.835 1 20";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString14));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString14,1));
                      break;
                  case "filter15":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString15="@curve R(0, 0)(96, 61)(154, 177)(255, 255) @pixblend overlay 0.357 0.863 0.882 1 40";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString15));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString15,1));
                      break;
                  case "filter16":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString16=  "@curve R(0, 0)(71, 74)(164, 165)(255, 255) @pixblend overlay 0.357 0.863 0.882 1 40";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString16));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString16,1));
                      break;
                  case "filter17":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString17= "@curve RGB(0,255)(255,0) @style cm mapping0.jpg 80 80 8 3";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString17));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString17,1));
                      break;
                  case "filter18":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString18="@adjust hsv -0.4 -0.64 -1.0 -0.4 -0.88 -0.88 @curve R(0, 0)(119, 160)(255, 255)G(0, 0)(83, 65)(163, 170)(255, 255)B(0, 0)(147, 131)(255, 255)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString18));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString18,1));
                      break;
                  case "filter19":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString19="@adjust hsv -1 -1 -1 -1 -1 -1";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString19));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString19,1));
                      break;
                  case "filter20":
                      seekBar_filter.setVisibility(View.VISIBLE);
                      img.setImageBitmap(bitmap_nochange);
                      String ruleString20="@adjust saturation 0 @curve R(9, 13)(37, 13)(63, 23)(81, 43)(91, 58)(103, 103)(159, 239)(252, 242)G(3, 20)(29, 20)(56, 19)(77, 37)(107, 108)(126, 184)(137, 217)(150, 248)(182, 284)(255, 255)B(45, 17)(78, 51)(96, 103)(131, 202)(255, 255)";
                      img.setImageBitmap(seekbarfilter(loadBitmapFromView(relativeLayout),ruleString20));
                      img.setImageBitmap(CGENativeLibrary.filterImage_MultipleEffects(loadBitmapFromView(relativeLayout),ruleString20,1));
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
        File imagesFolder = new File(getCacheDir(), Constant.PHOTO_CALENDAR_CACHE_DIR); // (get file cach + name folder cache"

        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(export_calendar.this, "com.example.photocalendar_app1.fileprovider", file);
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
    /// save filepath
    /**
     * Private method to create File path for Taken image from Camera
     */
    String mCurrentPhotoPath = "";
    private File createImageFilePath() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        String folderPath = Environment.getExternalStorageDirectory() + "/PhotoCalendar";    // add image taken into our custom folder in Gallery
        File storageDir = new File(folderPath);
        /**
         * @NOTE: from Android 11, we have using this way to get shared/external folder where app can persistent files it own
         * https://stackoverflow.com/questions/60356260/createtempfile-on-android-10-permission-denied
        /**/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (!storageDir.exists()) {
            File wallpaperDirectory = new File(folderPath);
            wallpaperDirectory.mkdirs();
        }


        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //
    /**
     * Get fullPath String of Image in Gallery
     */
    private String getImagePathFromUri(Uri imgUri) {
        //ExifInterface exifInterface = new ExifInterface(new URI(""));
        String filePath = "";

        // Check if image from ExternalStorage (SD card)
        if (FileUltils.isExternalStorageDocument(imgUri)) {
            final String docId = DocumentsContract.getDocumentId(imgUri);
            final String[] split = docId.split(":");
            final String type = split[0];
            // Primary volume
            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {
                // Non-Primary volume
                // get ExternalMediaDirs() added in API 21
                File[] external = getExternalMediaDirs();
                if (external.length > 1) {
                    filePath = external[1].getAbsolutePath();
                    filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1];
                    return filePath;
                }
            }
        } else if (imgUri.getPath() != null && imgUri.getPath().contains("external/images/media")) {
            // Check if image from Gallery of SDCard
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imgUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                ;
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                filePath = cursor.getString(column_index);
            }
            cursor.close();
            return filePath;
        } else if (FileUltils.isDownloadsDocument(imgUri)) {
            String fileName = FileUltils.getFilePath(this, imgUri);
            if (fileName != null) {
                return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
            }

            String id = DocumentsContract.getDocumentId(imgUri);
            if (id.startsWith("raw:")) {
                id = id.replaceFirst("raw:", "");
                File file = new File(id);
                if (file.exists())
                    return id;
            }

            String[] contentUriPrefixesToTry = new String[]{
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads",
                    "content://downloads/all_downloads"
            };
            for (String contentUriPrefix : contentUriPrefixesToTry) {
                try {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                    String path = FileUltils.getDataColumn(this, contentUri, null, null);
                    if (path != null) return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Path could note be retrieved using contentResolver, therefore copy file to accessible cache using streams
            String _fileName = FileUltils.getFileName(export_calendar.this, imgUri);
            File cacheDir = FileUltils.getDocumentCacheDir(export_calendar.this);
            File file = FileUltils.generateFileName(_fileName, cacheDir);
            String destinationPath = null;
            if (file != null) {
                destinationPath = file.getAbsolutePath();
                FileUltils.saveFileFromUri(export_calendar.this, imgUri, destinationPath);
            }
            return destinationPath;
        }

        String wholeID = DocumentsContract.getDocumentId(imgUri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        //TODO: handle error when  user pick image from DCIM folder
        if (id.contains("DCIM")) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + id;
        }

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
    //get bitmap from cache
    private Bitmap getBitmapFromCache(Uri imageUri, String imageAbsolutePath) {
        Bitmap tempBitmap = null;
        if (imageAbsolutePath == null) {
            // Check if image is picked from Google Photo
            if ("com.google.android.apps.photos.contentprovider".equals(imageUri.getAuthority())) {
                InputStream inputStreamBitmap = null;
                try {
                    inputStreamBitmap = getContentResolver().openInputStream(imageUri);
                    tempBitmap = BitmapFactory.decodeStream(inputStreamBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStreamBitmap != null) {
                            inputStreamBitmap.close();
                            imageAbsolutePath = FileUltils.getPathFromInputStreamUri(export_calendar.this, imageUri);
                        }
                    } catch (IOException ignored) {
                    }
                }
            } else {
                imageAbsolutePath = getImagePathFromUri(imageUri);
                tempBitmap = BitmapFactory.decodeFile(imageAbsolutePath, null);
            }
        }
        if (tempBitmap == null) {
            tempBitmap = BitmapFactory.decodeFile(imageAbsolutePath, null);
        }

        // TODO: add check null image
        if (tempBitmap == null) {
            //Toast.makeText(getApplicationContext(), getWrappedContext().getResources().getString(R.string.error_pick_images), Toast.LENGTH_SHORT).show();
        }
        return  tempBitmap;
    }
    //ckeck permisiion
    private void choseimage() {
    img_cam.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File imageFile = null;
            try {
                imageFile = createImageFilePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri photoUri = FileProvider.getUriForFile(export_calendar.this, "com.example.photocalendar_app1.fileprovider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent,CAMERA_REQUEST_CODE);
            }
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

    //check permisssion
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
    //requwest permisson when use deny permmission
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


    //get Uri in
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Calendar c = Calendar.getInstance();
         int m= (int) c.getTimeInMillis();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image"+m, null);

        return Uri.parse(path);
    }
    //get real filepath
    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED ) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                      try{

                          //get bitmap from cache with realpath
                          Bitmap bitmap_img_user=getBitmapFromCache(null,mCurrentPhotoPath);
                          //USe function orientation
                          Bitmap rotate_image=FileUltils.modifyOrientation(export_calendar.this,bitmap_img_user,mCurrentPhotoPath);
                          //use libary set bitamp with quality
                          Glide.with(export_calendar.this).load(rotate_image).override(1000,1000).into(img_user);
                      }
                      catch (Exception e){}

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
