package com.example.mymusic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.Models.PlayingSongModel;
import com.example.mymusic.Models.Song;
import com.example.mymusic.R;
import com.example.mymusic.Utilities.SongManagement;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private Context context;
    private PlayingSongModel playingSongModel;

    public SongAdapter(Context context) {
        this.context = context;
        playingSongModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PlayingSongModel.class);
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.a_song, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song song = SongManagement.listSong.get(position);
        holder.nameSong.setText(song.getTitle());
        holder.nameArtist.setText(song.getArtist());
        Handler handler = new Handler(context.getMainLooper());

        // Khi bấm vào item trong danh sách nhạc
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiệu ứng bấm vào sẽ có màu xanh
                holder.itemView.setBackgroundColor(Color.BLUE);

                // Chuyển sang bài hát đó (thay đổi posCurrentSong -> hàm onChanged được gọi)
                playingSongModel.getPosCurrentSong().setValue(SongManagement.getInstance().getPosRoot(position));

                // màu xanh xuất hiện trong 0,05s rồi về bình thường
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.setBackgroundColor(Color.WHITE);
                    }
                }, 50);
            }
        });
    }

    @Override
    public int getItemCount() {
        return SongManagement.listSong.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameSong, nameArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSong = itemView.findViewById(R.id.nameSong);
            nameArtist = itemView.findViewById(R.id.nameArtist);
        }
    }
}
