package com.aumum.app.mobile.ui.circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.asking.AskingActivity;

/**
 * Created by Administrator on 2/03/2015.
 */
public class CircleFragment extends Fragment {

    private View askingLayout;
    private View momentsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_circle, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        askingLayout = view.findViewById(R.id.layout_asking);
        askingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAskingActivity();
            }
        });
        momentsLayout = view.findViewById(R.id.layout_moments);
    }

    private void startAskingActivity() {
        final Intent intent = new Intent(getActivity(), AskingActivity.class);
        startActivity(intent);
    }
}
