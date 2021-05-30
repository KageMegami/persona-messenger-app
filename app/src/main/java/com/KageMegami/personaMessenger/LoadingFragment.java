package com.KageMegami.personaMessenger;

import android.content.res.AssetManager;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LoadingFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView)view.findViewById(R.id.loadingGif);
        Glide.with(this).load(R.drawable.loading).into(imageView);
    }
}