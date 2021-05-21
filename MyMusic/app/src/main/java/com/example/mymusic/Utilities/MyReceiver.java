package com.example.mymusic.Utilities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mymusic.Activity.MainActivity;
import com.example.mymusic.R;

public class MyReceiver extends BroadcastReceiver {
   static final String IMAGE_VIEW_CLICKED = "click_img";
    /**
     * Receive gửi về chương trình khi nhận được hành động trên thanh thông báo
     * Hàm onReceive thực hiện
     * **/
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(IMAGE_VIEW_CLICKED, -1);
        switch (id) {
            case R.id.prevNotification: // bấm vào button prev
                MainActivity.playingSongModel.prevSong();
                break;
            case R.id.playNotification: // bấm vào button play
                MainActivity.playingSongModel.isPlaying().setValue(!MainActivity.playingSongModel.isPlaying().getValue());
                break;
            case R.id.nextNotification: // bấm vào button next
                MainActivity.playingSongModel.nextSong();
                break;
            case R.id.imgLarge:
            case R.id.nameSongNotification:// bấm vào ảnh hoặc tên bài hát
                // Trở về ứng dụng
                Intent it = new Intent(context, MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
                break;
        }
    }
}
