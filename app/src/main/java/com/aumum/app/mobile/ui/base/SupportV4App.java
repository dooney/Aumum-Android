package android.support.v4.app;

import java.util.ArrayList;

/**
 * Created by Administrator on 28/09/2014.
 */
public class SupportV4App {
    public static void activityFragmentsNoteStateNotSaved(FragmentActivity activity) {
        activity.mFragments.noteStateNotSaved();
    }

    public static ArrayList<Fragment> activityFragmentsActive(FragmentActivity activity) {
        return activity.mFragments.mActive;
    }

    public static int fragmentIndex(Fragment fragment) {
        return fragment.mIndex;
    }

    public static ArrayList<Fragment> fragmentChildFragmentManagerActive(Fragment fragment) {
        return ((FragmentManagerImpl) fragment.getChildFragmentManager()).mActive;
    }
}
