package com.example.mymusic.Models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mymusic.Utilities.SongManagement;

public class PlayingSongModel extends ViewModel {
    //postCurrentSong là vị trí hiện tại của bài hát đang hát
    private MutableLiveData<Integer> posCurrentSong;

    //isPlaying là trạng thái đang phát/ dừng của bài hát
    private MutableLiveData<Boolean> isPlaying;

    //isShuffling là trạng thái danh sách có bị trộn không
    private MutableLiveData<Boolean> isShuffling;

    //isReversing là trạng thái bài hát có được lặp lại không
    private MutableLiveData<Boolean> isReversing;

    //currentTime là thời gian hiện tại của bài hát
    private MutableLiveData<Integer> currentTime;

    public PlayingSongModel() {
        super();
        posCurrentSong = new MutableLiveData<>(-1);
        isPlaying = new MutableLiveData<>(false);
        currentTime = new MutableLiveData<>(0);
        isShuffling = new MutableLiveData<>(false);
        isReversing = new MutableLiveData<>(false);
    }

    public MutableLiveData<Integer> getPosCurrentSong() {
        return posCurrentSong;
    }

    public MutableLiveData<Boolean> isPlaying() {
        return isPlaying;
    }

    public MutableLiveData<Boolean> isShuffling() {
        return isShuffling;
    }

    public MutableLiveData<Boolean> isReversing() {
        return isReversing;
    }

    public MutableLiveData<Integer> getCurrentTime() {
        return currentTime;
    }

    public void nextSong() {
        // nextSong là thay đổi posCurrentSong -> onChanged sẽ được gọi
        posCurrentSong.setValue((posCurrentSong.getValue() + 1) % SongManagement.listSong.size());
    }

    public void prevSong() {
        // prevSong là thay đổi posCurrentSong -> onChanged sẽ được gọi
        posCurrentSong.setValue((SongManagement.listSong.size() + posCurrentSong.getValue() - 1) % SongManagement.listSong.size());
    }
}
