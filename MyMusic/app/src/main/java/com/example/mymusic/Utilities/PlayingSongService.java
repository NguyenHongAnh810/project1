package com.example.mymusic.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.example.mymusic.Activity.MainActivity;
import com.example.mymusic.R;

public class PlayingSongService extends LifecycleService {
    private final int NOTIFICATION_ID = 1;
    private NotificationCompat.Builder builder = null;
    private NotificationChannel channel = null;
    private NotificationManager manager = null;
    public static Handler uiHandler = null;
    private RemoteViews notificationLayout = null;

    /**
     * Tạo service để hiện notifcation lên thanh thông báo
     * **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("MY_CHANNEL_ID", "MY_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        /**
         * Thiết lập giao diện và các chức năng bấm cho thông báo
         * **/
        notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification);
        // Thiết lập các hành động khi bấm vào các nút
        notificationLayout.setOnClickPendingIntent(R.id.prevNotification, onNotificationClick(R.id.prevNotification));
        notificationLayout.setOnClickPendingIntent(R.id.playNotification, onNotificationClick(R.id.playNotification));
        notificationLayout.setOnClickPendingIntent(R.id.nextNotification, onNotificationClick(R.id.nextNotification));
        notificationLayout.setOnClickPendingIntent(R.id.imgLarge, onNotificationClick(R.id.imgLarge));
        notificationLayout.setOnClickPendingIntent(R.id.nameSongNotification, onNotificationClick(R.id.nameSongNotification));

        /**
         * Xây dựng thông báo
         * **/
        builder = new NotificationCompat.Builder(PlayingSongService.this, "MY_CHANNEL_ID");
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setCustomContentView(notificationLayout);
        builder.setSmallIcon(getApplicationContext().getApplicationInfo().icon);

        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);

        /**
         * Lắng nghe những thay đổi của bài hát và cập nhật trên thanh thông báo
         * Lắng nghe thay đổi thông báo và cập nhật bài hát
         * **/
        MainActivity.playingSongModel.isPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (SongManagement.mediaPlayer == null) return;
                if (aBoolean) {
                    if (!SongManagement.mediaPlayer.isPlaying())
                        SongManagement.mediaPlayer.start();
                    notificationLayout.setImageViewResource(R.id.playNotification, R.drawable.ic_pause_no_circle_black);
                } else {
                    if (SongManagement.mediaPlayer.isPlaying())
                        SongManagement.mediaPlayer.pause();
                    notificationLayout.setImageViewResource(R.id.playNotification, R.drawable.ic_play_no_circle_black);
                }
                Notification notification = builder.build();
                manager.notify(NOTIFICATION_ID, notification);
            }
        });

        MainActivity.playingSongModel.getPosCurrentSong().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer < 0 || integer >= SongManagement.listSong.size()) return;
                if (SongManagement.currentPos != integer) {
                    SongManagement.currentPos = integer;
                    if (SongManagement.mediaPlayer != null) {
                        SongManagement.mediaPlayer.stop();
                        SongManagement.mediaPlayer.release();
                    }
                    SongManagement.mediaPlayer = null;
                    SongManagement.mediaPlayer = MediaPlayer.create(MainActivity.getInstance(), SongManagement.getInstance().getSong(integer).getFile());
                    SongManagement.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (!MainActivity.playingSongModel.isReversing().getValue()) {
                                MainActivity.playingSongModel.nextSong();
                            }
                        }
                    });
                    if (MainActivity.playingSongModel.isPlaying().getValue()) {
                        SongManagement.mediaPlayer.start();
                    }
                }
                notificationLayout.setTextViewText(R.id.nameSongNotification,
                                SongManagement.getInstance().getSong(SongManagement.currentPos).getTitle());
                notificationLayout.
                        setTextViewText(R.id.nameArtistNotification,
                                SongManagement.getInstance().getSong(SongManagement.currentPos).getArtist());


                Notification notification = builder.build();
                manager.notify(NOTIFICATION_ID, notification);
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Action khi bấm vào các view trên thanh thông báo -> gửi tới receive
     * **/
    private PendingIntent onNotificationClick(@IdRes int id) {
        // Khi bấm vào, gửi broadcast về hàm MyReceiver -> thực hiện thay đổi trên giao diện
        Intent intent = new Intent(PlayingSongService.this, MyReceiver.class);
        intent.putExtra(MyReceiver.IMAGE_VIEW_CLICKED, id);
        return PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}