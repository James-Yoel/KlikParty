package umn.ac.id.uas_mobileandroid_2021_a3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    CardView bookParty, bookMC, bookEquipment;
    private SliderAdapter mAdapter;
    private SliderIndicator mIndicator;
    private BannerSlider bannerSlider;
    private LinearLayout mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bannerSlider = view.findViewById(R.id.sliderView);
        mLinearLayout = view.findViewById(R.id.pagesContainer);
        bookParty = view.findViewById(R.id.cardBookParty);
        bookMC = view.findViewById(R.id.cardBookMC);
        bookEquipment = view.findViewById(R.id.cardBookEquipment);
        bookParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), BookParty.class));
            }
        });
        bookMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity().getApplicationContext(), BookMc.class);
                next.putExtra("prev", "Main");
                startActivity(next);
            }
        });
        bookEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity().getApplicationContext(), BookEquipment.class);
                next.putExtra("prev", "Main");
                startActivity(next);
            }
        });
        setupSlider();
        return view;
    }

    private void setupSlider() {
        bannerSlider.setDurationScroll(800);
        List<Fragment> fragments = new ArrayList<>();

        //link image
        fragments.add(Main.newInstance("https://firebasestorage.googleapis.com/v0/b/uas-mobileandroid-2021-a3.appspot.com/o/banner%2Fbanner_1.jpg?alt=media&token=733959d8-f195-4b16-bd5c-7d7c952eab39"));
        fragments.add(Main.newInstance("https://firebasestorage.googleapis.com/v0/b/uas-mobileandroid-2021-a3.appspot.com/o/banner%2FbookParty_KlikParty.jpg?alt=media&token=b2be4e95-6617-40c1-98e1-051c826a075d"));
        fragments.add(Main.newInstance("https://firebasestorage.googleapis.com/v0/b/uas-mobileandroid-2021-a3.appspot.com/o/banner%2FbookMc_KlikParty.jpg?alt=media&token=524ac545-cea3-4888-9abe-2c50b65235bc"));
        fragments.add(Main.newInstance("https://firebasestorage.googleapis.com/v0/b/uas-mobileandroid-2021-a3.appspot.com/o/banner%2FbookEquipment_KlikParty.jpg?alt=media&token=50c00cd3-dc39-424c-942d-71b91e654ce5"));

        mAdapter = new SliderAdapter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,fragments);
        bannerSlider.setAdapter(mAdapter);
        mIndicator = new SliderIndicator(getContext(), mLinearLayout, bannerSlider, R.drawable.indicator_slider);
        mIndicator.setPageCount(fragments.size());
        mIndicator.show();
    }
}
