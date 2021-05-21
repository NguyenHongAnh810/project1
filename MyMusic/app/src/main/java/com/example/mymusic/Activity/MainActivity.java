package com.example.mymusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.mymusic.Models.PlayingSongModel;
import com.example.mymusic.Utilities.PlayingSongService;
import com.example.mymusic.R;
import com.example.mymusic.Utilities.SongManagement;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    public static PlayingSongModel playingSongModel;
    private SongManagement songManagement;
    private static MainActivity instance;
    private Intent intent;

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        // Kiểm tra xem máy đã cho phép truy cấp thư mục chưa?
        checkPermission();

        /***
         * Thiết lập danh sách nhạc
         * */
        songManagement = SongManagement.getInstance();
        songManagement.setListSong();


        /**
         * Hàm tạo ViewModel của activiy
         * **/
        playingSongModel = new ViewModelProvider(this).get(PlayingSongModel.class);

        /**
         * Xây dụng bottom navigation
         * **/
        navView = findViewById(R.id.nav_view);

        // Đăng kí hai fragment trên bottom navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.currentSong, R.id.listSong)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        /**
         * Cập nhật thời gian bài hát liên tục
         * **/
        updateTimeSong();

        PlayingSongService.uiHandler = new Handler(getMainLooper());
    }

    private void checkPermission() {
        String[] per = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, per, 1);
        };
    }

    private void updateTimeSong(){
        /**
         * Ta sẽ cho cập nhật thời gian bài hát liên tục (kể cả lúc bài hát dừng)
         * Nếu có sự thay đổi về thời gian, sẽ thay đổi trên LiveData
         * **/
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SongManagement.mediaPlayer != null) {
                    //Gán giá trị cho LiveData currentTime - thay đổi => onChanged được gọi
                    playingSongModel.getCurrentTime().setValue(SongManagement.mediaPlayer.getCurrentPosition());
                }

                /***
                 * Cứ 0.5s (500ms) cập nhật một lần
                 * */
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    public BottomNavigationView getNavView() {
        return navView;
    }

    private void startSV() {
        // Bắt đầu Service
        intent = new Intent(this, PlayingSongService.class);
        startService(intent);
    }
    private void stopSV() {
        if (intent != null) stopService(intent);
    }

    @Override
    protected void onPause() {
        // Nếu ở trạng thái onPause (Activity bị ẩn) -> khởi động service
        startSV();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Nếu activity đang hoạt động (trong giao diện) -> tắt service
        stopSV();
        super.onResume();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        // Nếu hàm activiy bị hủy -> xóa toàn bộ
        if (SongManagement.mediaPlayer != null && SongManagement.mediaPlayer.isPlaying()) {
            SongManagement.mediaPlayer.stop();
        }
        stopSV();
        super.onDestroy();
    }
}