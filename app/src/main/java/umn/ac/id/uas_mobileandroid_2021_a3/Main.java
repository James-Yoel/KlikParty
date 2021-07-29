package umn.ac.id.uas_mobileandroid_2021_a3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;

public class Main extends Fragment {


    private static final String ARG_PARAM1 = "imgSlider";

    private String imageUrls;

    public Main() {
    }

    public static Main newInstance(String params) {
        Main fragment = new Main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, params);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        imageUrls = getArguments().getString(ARG_PARAM1);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ImageView img = view.findViewById(R.id.bannerImg);
        Glide.with(getActivity())
                .load(imageUrls)
                .placeholder(R.drawable.logo_umn)
                .into(img);
        return view;
    }
}