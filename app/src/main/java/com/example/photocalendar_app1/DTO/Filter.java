package com.example.photocalendar_app1.DTO;

import android.graphics.Bitmap;

public class Filter {

    private int imgae_filter;
    private String filtername;
    private boolean check;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getImgae_frame() {
        return imgae_filter;
    }

    public void setImgae_frame(int imgae_filter) {
        this.imgae_filter = imgae_filter;
    }

    public String getFiltername() {
        return filtername;
    }

    public void setFiltername(String filtername) {
        this.filtername = filtername;
    }
}
