package com.mklimek.frameviedoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

public @TargetApi(14)
class TextureViewImpl extends TextureView implements
        Impl,
        MediaPlayer.OnPreparedListener,
        TextureView.SurfaceTextureListener,
        MediaPlayer.OnBufferingUpdateListener {

    private View placeholderView;
    private Uri videoUri;
    private FrameVideoViewListener listener;

    private Surface surface;
    private MediaPlayer mediaPlayer;
    private boolean prepared;
    private boolean startInPrepare;

    public TextureViewImpl(Context context) {
        super(context);
        setSurfaceTextureListener(this);
    }

    public TextureViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    @Override
    public void init(View placeholderView, Uri videoUri) {
        this.placeholderView = placeholderView;
        this.videoUri = videoUri;
        if(prepared){
            release();
        }
        if(surface != null) {
            prepare();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(true);
        if(startInPrepare){
            mp.start();
            startInPrepare = false;
        }
        prepared = true;
        if(listener != null){
            listener.mediaPlayerPrepared(mp);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = new Surface(surface);
        if(!prepared && videoUri != null){
            prepare();
        }
    }

    private void prepare() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getContext(), videoUri);
            mediaPlayer.setSurface(surface);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnInfoListener(new InfoListener(placeholderView));
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.prepare();
        } catch (Exception e) {
            if ( listener != null ) {
                listener.mediaPlayerPrepareFailed( mediaPlayer, e.toString() );
            }
            removeVideo();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        removeVideo();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public void onResume() {
        if(prepared) {
            mediaPlayer.start();
        } else{
            startInPrepare = true;
        }

        if(isAvailable()){
            onSurfaceTextureAvailable(getSurfaceTexture(), 0, 0);
        }
    }

    @Override
    public void onPause() {
        removeVideo();
    }

    private void release() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = null;
        prepared = false;
        startInPrepare = false;
    }

    private void removeVideo(){
        if(placeholderView != null){    
            placeholderView.setVisibility(View.VISIBLE);
        }
        release();
    }

    @Override
    public void setFrameVideoViewListener(FrameVideoViewListener listener) {
        this.listener = listener;
    }

}
