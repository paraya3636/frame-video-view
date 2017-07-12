package com.mklimek.frameviedoview;

import android.media.MediaPlayer;
import android.view.View;

public class InfoListener implements MediaPlayer.OnInfoListener {

    private View placeholderView;

    public InfoListener(View placeholderView) {
        this.placeholderView = placeholderView;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            placeholderView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}
