package com.example.photocalendar_app1;

import android.graphics.Bitmap;
import android.widget.SeekBar;

import org.wysaid.view.ImageGLSurfaceView;

public class AdjustConfig {
    public int index;
    public float intensity, slierIntensity = 0.5f;
    public float minValue, originValue, maxValue;

    private Bitmap mBitmap;
    private ImageGLSurfaceView mImageView;
    private SeekBar mSeekBar;

    public AdjustConfig(int _index, float _minValue, float _originValue, float _maxValue) {
        index = _index;
        minValue = _minValue;
        originValue = _originValue;
        maxValue = _maxValue;
        intensity = _originValue;
    }

    protected float calcIntensity(float _intensity) {
        float result;
        if (_intensity <= 0.0f) {
            result = minValue;
        } else if (_intensity >= 1.0f) {
            result = maxValue;
        } else if (_intensity <= 0.5f) {
            result = minValue + (originValue - minValue) * _intensity * 2.0f;
        } else {
            result = maxValue + (originValue - maxValue) * (1.0f - _intensity) * 2.0f;
        }
        return result;
    }

    //_intensity range: [0.0, 1.0], 0.5 for the origin.
    public void setIntensity(float _intensity, boolean shouldProcess) {

        slierIntensity = _intensity;
        intensity = calcIntensity(_intensity);

    }


    AdjustConfig mActiveConfig = null;

    public void setActiveConfig(AdjustConfig config) {
        mActiveConfig = config;
        mSeekBar.setProgress((int) (config.slierIntensity * mSeekBar.getMax()));
    }
}


