package com.aumum.app.mobile.ui.base;

import android.content.Intent;
import android.support.v4.app.SupportV4App;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

/**
 * Base class for all Bootstrap Activities that need fragments.
 */
public class BaseFragmentActivity extends ActionBarActivity {
    //http://blog.shamanland.com/2014/01/nested-fragments-for-result.html
    private static final int[] POW_2 = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096};
    // 16 bits available at all
    private static final int CHAIN_BITS_FOR_INDEX = 3; // adjustable constant, use value 3 or 4
    private static final int CHAIN_BITS_COUNT = 9; // adjustable constant, use value 9 or 12
    private static final int CHAIN_INDEX_MASK = ~(0x80000000 >> (31 - CHAIN_BITS_FOR_INDEX));
    // max allowed depth of fragments
    private static final int CHAIN_MAX_DEPTH = CHAIN_BITS_COUNT / CHAIN_BITS_FOR_INDEX;
    // bits for external usage
    private static final int REQUEST_CODE_EXT_BITS = 16 - CHAIN_BITS_COUNT;
    private static final int REQUEST_CODE_MASK = ~(0x80000000 >> (31 - REQUEST_CODE_EXT_BITS));
    // we have to add +1 for every index
    // because we could not determine 0 index at all
    private static final int FRAGMENT_MAX_COUNT = POW_2[CHAIN_BITS_FOR_INDEX] - 1;

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if ((requestCode & (~REQUEST_CODE_MASK)) != 0) {
            super.startActivityFromFragment(fragment, intent, requestCode);
            return;
        }

        int chain = 0;
        int depth = 0;

        Fragment node = fragment;

        do {
            if (depth > CHAIN_MAX_DEPTH) {
                throw new IllegalStateException("Too deep structure of fragments, max " + CHAIN_MAX_DEPTH);
            }

            int index = SupportV4App.fragmentIndex(node);
            if (index < 0) {
                throw new IllegalStateException("Fragment is out of FragmentManager: " + node);
            }

            if (index >= FRAGMENT_MAX_COUNT) {
                throw new IllegalStateException("Too many fragments inside (max " + FRAGMENT_MAX_COUNT + "): " + node.getParentFragment());
            }

            chain = (chain << CHAIN_BITS_FOR_INDEX) + (index + 1);
            node = node.getParentFragment();
            depth += 1;
        } while (node != null);

        int newCode = (chain << REQUEST_CODE_EXT_BITS) + (requestCode & REQUEST_CODE_MASK);

        super.startActivityForResult(intent, newCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode & 0xffff0000) != 0) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        SupportV4App.activityFragmentsNoteStateNotSaved(this);

        int chain = requestCode >>> REQUEST_CODE_EXT_BITS;
        if (chain != 0) {
            ArrayList<Fragment> active = SupportV4App.activityFragmentsActive(this);
            Fragment fragment;

            do {
                int index = (chain & CHAIN_INDEX_MASK) - 1;
                if (active == null || index < 0 || index >= active.size()) {
                    return;
                }

                fragment = active.get(index);
                if (fragment == null) {
                    break;
                }

                active = SupportV4App.fragmentChildFragmentManagerActive(fragment);
                chain = chain >>> CHAIN_BITS_FOR_INDEX;
            } while (chain != 0);

            if (fragment != null) {
                fragment.onActivityResult(requestCode & REQUEST_CODE_MASK, resultCode, data);
            }
        }
    }
}
