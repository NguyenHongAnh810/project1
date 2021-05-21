package com.example.mymusic.Fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mymusic.Models.PlayingSongModel;
import com.example.mymusic.R;
import com.example.mymusic.Utilities.SongManagement;

import java.text.SimpleDateFormat;

public class CurrentSongFragment extends Fragment {

    View view;
    TextView txtTitle, txtTimeSong, txtTimeTotal, txtArtist;
    SeekBar skSong;
    ImageView btnPrev, btnPlay, btnNext, btnShuffle, btnReverse;
    ImageView imageHinh;
    ObjectAnimator animation;
    PlayingSongModel playingSongModel;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.current_song_fragment, container, false);

        // gọi ViewModel truyền context là MainActivity
        playingSongModel = new ViewModelProvider(getActivity()).get(PlayingSongModel.class);

        // Khởi tạo các thuộc tính trong Fragment
        initComponent();

        // Khởi tạo hành động cho các thuộc tính
        initListener();

        return view;
    }

    // chuyển totalTime sang dạng phút:giây
    private void setTimeTotal(int totalTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
        txtTimeTotal.setText(dinhDangGio.format(totalTime));
        // gán max của sksong
        skSong.setMax(totalTime);
    }

    // chuyển thời gian hiện tại bài hát sang phút:giây
    private void setCurrentTime(int currentTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
        txtTimeSong.setText(dinhDangGio.format(currentTime));
        skSong.setProgress(currentTime);
    }

    // Khởi tạo
    private void initComponent() {
        // Khởi tạo các View
        txtArtist = view.findViewById(R.id.textViewArtist);
        txtTimeSong = view.findViewById(R.id.textViewTimeSong);
        txtTimeTotal = view.findViewById(R.id.textviewTimeTotal);
        txtTitle = view.findViewById(R.id.textviewTitle);
        skSong = view.findViewById(R.id.seekBarSong);
        btnNext = view.findViewById(R.id.imageButtonNext);
        btnPlay = view.findViewById(R.id.imageButtonPlay);
        btnPrev = view.findViewById(R.id.imageButtonPrev);
        btnShuffle = view.findViewById(R.id.imageButtonShuffle);
        btnReverse = view.findViewById(R.id.imageButtonReverse);
        imageHinh = view.findViewById(R.id.imageView3);

        // Khởi tạo hoạt động xoay đĩa
        animation = ObjectAnimator.ofFloat(imageHinh, View.ROTATION, 0f, 360f);
        animation.setDuration(10000);
        animation.setRepeatMode(ValueAnimator.RESTART);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setInterpolator(new LinearInterpolator());

        // Nếu mà ViewModel != null thì làm
        if (playingSongModel != null) {

            /**
             * Nếu mà LiveData posCurrentSong cập nhật (tức là bài hát bị chuyển)
             * Thì thực hiện hàm onChanged
             * **/
            playingSongModel.getPosCurrentSong().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    //Kiểm tra xem vị trí bài hát có trong khoảng cho phép hay không
                    if (integer < 0 || integer >= SongManagement.listSong.size()) return;

                    /**
                     * Kiểm tra xem vị trí bài hát đang xét có trùng với vị trí posCurrentSong vừa cập nhật hay không
                     * Nếu không trùng thì làm
                     * **/
                    if (SongManagement.currentPos != integer) {
                        SongManagement.currentPos = integer; // Cập nhật lại vị trí bài hát đang phát

                        // Giải phóng bài hát đang phát
                        if (SongManagement.mediaPlayer != null) {
                            SongManagement.mediaPlayer.stop();
                            SongManagement.mediaPlayer.release();
                        }

                        // Cài đặt bài hát mới
                        SongManagement.mediaPlayer = null;
                        SongManagement.mediaPlayer = MediaPlayer.create(getActivity(), SongManagement.getInstance().getSong(integer).getFile());
                        SongManagement.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // Nếu kết thúc bài hát mà không ở chế độ phát lại -> sang bài hát tiếp
                                if (!playingSongModel.isReversing().getValue()) {
                                    playingSongModel.nextSong();
                                }
                            }
                        });

                        // Nếu mà bài hát trước đang phát, thì bài hát vừa cài đặt cũng sẽ phát
                        if (playingSongModel.isPlaying().getValue()) {
                            SongManagement.mediaPlayer.start();
                        }
                    }

                    // set title cho bài hát
                    txtTitle.setText(SongManagement.getInstance().getSong(integer).getTitle());

                    // set tên nghệ sĩ
                    if (SongManagement.getInstance().getSong(integer).getArtist() != null) {
                        txtArtist.setText(SongManagement.getInstance().getSong(integer).getArtist());
                    }

                    // set Tổng thời gian
                    setTimeTotal(SongManagement.mediaPlayer.getDuration());
                }
            });

            /**
             * Nếu LiveData isPlaying cập nhật (tức là play/pause) thì thực hiện trong hàm onChanged
             * **/
            playingSongModel.isPlaying().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    // Nếu mediaPlayer == null -> không làm nữa
                    if (SongManagement.mediaPlayer == null) return;

                    if (aBoolean) {
                        // Nếu mà trạng thái vừa cập nhật aBoolean là true (phát nhạc) thì chạy bài hát
                        if (!SongManagement.mediaPlayer.isPlaying())
                            SongManagement.mediaPlayer.start();

                        // Chạy animation đĩa
                        if (!animation.isStarted()) {
                            animation.start();
                        } else {
                            animation.resume();
                        }
                        // Thay đổi nút play -> pause
                        btnPlay.setImageResource(R.drawable.ic_pause_circle);
                    } else {
                        // Nếu mà trạng thái vừa cập nhật aBoolean là false (dừng phát nhạc) thì dừng bài hát
                        //dừng animation
                        animation.pause();
                        // dừng bài hát
                        if (SongManagement.mediaPlayer.isPlaying())
                            SongManagement.mediaPlayer.pause();

                        //Thay đổi nút pause -> play
                        btnPlay.setImageResource(R.drawable.ic_play_circle);
                    }
                }
            });

            /**
             * Nếu currentTime (thời gian hiện tại được cập nhật) thì thực hiện
             * Lúc nào cũng được cập nhật do hàm updateTimeSong ở MainActivity
             * **/
            playingSongModel.getCurrentTime().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if (SongManagement.mediaPlayer == null) return;
                    // Cập nhật giao diện currentTime
                    setCurrentTime(integer);
                }
            });

            /**
             * Nếu mà isReversing cập nhật (có lặp lại bài hát không)
             * Thực hiện hàm onChanged
             * **/
            playingSongModel.isReversing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (SongManagement.mediaPlayer == null) return;

                    // Thay đổi giao diện
                    if (aBoolean) {
                        btnReverse.setImageResource(R.drawable.ic_reverse_chosen);
                    } else {
                        btnReverse.setImageResource(R.drawable.ic_reverse);
                    }

                    // Thay đổi trong code theo giá trị cập nhật aBoolean
                    SongManagement.mediaPlayer.setLooping(aBoolean);
                }
            });

            /**
             * Nếu mà isShuffling cập nhật (có xáo trộn bài hát không)
             * Thực hiện hàm onChanged
             * **/
            playingSongModel.isShuffling().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (SongManagement.mediaPlayer == null) return;


                    if (aBoolean) {
                        // Nếu có thì xáo trộn bài
                        SongManagement.getInstance().shuffle();
                        btnShuffle.setImageResource(R.drawable.ic_shuffle_chosen);
                    } else {

                        // Nếu không thì khôi phục thứ tụ
                        SongManagement.getInstance().restore();
                        btnShuffle.setImageResource(R.drawable.ic_shuffle);
                    }
                }
            });

            //Khởi tạo bài hát đầu tiên -> ở vị trí 0
            if (playingSongModel.getPosCurrentSong().getValue() == -1) {
                playingSongModel.getPosCurrentSong().setValue(0);
            }
        }
    }

    private void initListener() {

        // Khi bấm button Next bài hát
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playingSongModel == null) return;
                // thực hiện hàm nextSong trong Model
                playingSongModel.nextSong();
            }
        });

        // Khi bấm button Prev
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playingSongModel == null) return;
                // Thực hiện hàm prevSong trong Model
                playingSongModel.prevSong();
            }
        });

        // Khi bấm button Play
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playingSongModel == null) return;

                // Nếu đang phát thì dừng và ngược lại, cập nhật giá trị isPlaying -> hàm onChanged thực hiện sau đó
                playingSongModel.isPlaying().setValue(!playingSongModel.isPlaying().getValue());
            }
        });

        // Khi bấm button Reverse
        btnReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playingSongModel == null) return;
                // Nếu đang lặp lại thì tắt và ngược lại, cập nhật giá trị isReversing -> hàm onChanged thực hiện sau đó
                playingSongModel.isReversing().setValue(!playingSongModel.isReversing().getValue());
            }
        });

        // Khi bấm button Shuffle
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playingSongModel == null) return;
                // Nếu danh sách đang trộn thì khôi phục lại và ngược lại
                // cập nhật giá trị isShuffling -> hàm honChanged thực hiện sau đó
                playingSongModel.isShuffling().setValue(!playingSongModel.isShuffling().getValue());
            }
        });

        // Thao tác trên thanh seekBar
        skSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Khi kéo thanh seekBar, currentTime sẽ đổi -> onChanged thực hiện
                if (playingSongModel == null) return;
                playingSongModel.getCurrentTime().setValue(seekBar.getProgress());

                if (SongManagement.mediaPlayer == null) return;
                SongManagement.mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }
}