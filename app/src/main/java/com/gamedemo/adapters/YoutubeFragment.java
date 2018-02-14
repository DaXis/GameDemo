package com.gamedemo.adapters;

import android.os.Bundle;

import com.gamedemo.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class YoutubeFragment extends YouTubePlayerSupportFragment {

    private String currentVideoID = "video_id";
    private YouTubePlayer activePlayer;

    public static YoutubeFragment newInstance(String url) {

        YoutubeFragment playerYouTubeFrag = new YoutubeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);

        return playerYouTubeFrag;
    }

    private void init() {

        initialize(getActivity().getResources().getString(R.string.API_KEY), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                if (!wasRestored) {
                    activePlayer.loadVideo(getArguments().getString("url"), 0);

                }
            }
        });
    }

    //@Override
    public void onYouTubeVideoPaused() {
        activePlayer.pause();
    }
}
