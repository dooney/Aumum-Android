package com.aumum.app.mobile.ui.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, null);
    }
}
