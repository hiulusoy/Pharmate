package fragments.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragments.Frag1;
import fragments.Frag2;

import com.example.pharmate.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
     Fragment fragment=null;
     switch (position){
         case 0:
             fragment=new Frag1();
             break;
         case 1:
             fragment=new Frag2();
             break;

     }return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "person";
            case 1:
                return "organization";

        }return null;

    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}