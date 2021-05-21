package com.example.mymusic.Fragments;


import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.Activity.MainActivity;
import com.example.mymusic.Models.PlayingSongModel;
import com.example.mymusic.R;
import com.example.mymusic.Adapter.SongAdapter;
import com.example.mymusic.Utilities.SongManagement;

public class ListSongFragment extends Fragment {
    private LinearLayout ll;
    private View v;
    private TextView nameSong, nameArtist;
    private ImageView prevImg, playImg, nextImg;
    private SeekBar seekBar;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private PlayingSongModel playingSongModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_song, container, false);

        v = view;
        playingSongModel = new ViewModelProvider(getActivity()).get(PlayingSongModel.class);
        initComponent();
        return view;
    }

    private void initComponent() {
        ll = v.findViewById(R.id.ll1);
        nameSong = v.findViewById(R.id.nameSongFragmentDS);
        prevImg = v.findViewById(R.id.prevImg);
        nextImg = v.findViewById(R.id.nextImg);
        playImg = v.findViewById(R.id.playImg);
        seekBar = v.findViewById(R.id.seekBar);
        nameArtist = v.findViewById(R.id.nameArtistFragmentDS);

        recyclerView = v.findViewById(R.id.listSongRV);
        songAdapter = new SongAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Nếu mà vị trí hiện tại của bài hát thay đổi (tức là chuyển bài) thì làm
        playingSongModel.getPosCurrentSong().observe(getViewLifecycleOwner(), new Observer<Integer>() {
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
                    SongManagement.mediaPlayer = MediaPlayer.create(getActivity(), SongManagement.getInstance().getSong(integer).getFile());
                    SongManagement.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (!playingSongModel.isReversing().getValue()) {
                                playingSongModel.nextSong();
                            }
                        }
                    });
                    if (playingSongModel.isPlaying().getValue()) {
                        SongManagement.mediaPlayer.start();
                    }
                }
                seekBar.setMax(SongManagement.mediaPlayer.getDuration());
                nameSong.setText(SongManagement.getInstance().getSong(integer).getTitle());
                if (SongManagement.getInstance().getSong(integer).getArtist() != null) {
                    nameArtist.setText(SongManagement.getInstance().getSong(integer).getArtist());
                }
            }
        });

        // Nếu mà trạng thái của bài nhạc thay đổi (đang phát/dừng) thì làm
        playingSongModel.isPlaying().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (SongManagement.mediaPlayer == null) return;
                if (aBoolean) {
                    if (!SongManagement.mediaPlayer.isPlaying())
                        SongManagement.mediaPlayer.start();

                    playImg.setImageResource(R.drawable.ic_pause_no_circle);
                } else {
                    if (SongManagement.mediaPlayer.isPlaying())
                        SongManagement.mediaPlayer.pause();
                    playImg.setImageResource(R.drawable.ic_play_no_circle);
                }
            }
        });

        // Nếu mà thời gian của bài hát thay đổi (nếu đang phát thì luôn đổi) thì làm
        playingSongModel.getCurrentTime().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (SongManagement.mediaPlayer == null) return;
                seekBar.setProgress(SongManagement.mediaPlayer.getCurrentPosition());
            }
        });

        nextImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playingSongModel.nextSong();
            }
        });

        prevImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playingSongModel.prevSong();
            }
        });

        playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playingSongModel.isPlaying().setValue(!playingSongModel.isPlaying().getValue());
            }
        });

        //Chuyển về bài hát đang phát
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getNavView().setSelectedItemId(R.id.currentSong);
            }
        });
    }

}