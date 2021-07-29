package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class SliderAdapter extends FragmentStatePagerAdapter {

    List<Fragment> mFrags;

    public SliderAdapter(FragmentManager fm, int behavior ,List<Fragment> frags) {
        super(fm, behavior);
        mFrags = frags;
    }

    @Override
    public Fragment getItem(int position) {
        int index = position % mFrags.size();
        return Main.newInstance(mFrags.get(index).getArguments().getString("imgSlider"));
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
