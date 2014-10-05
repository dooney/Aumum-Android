package com.aumum.app.mobile.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.User;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {

    protected View userView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //userView = (View) view.findViewById(R.id.user_view);
    }

    @Override
    public void onDestroyView() {
        userView = null;

        super.onDestroyView();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_user;
    }

    @Override
    protected boolean readyToShow() {
        return data != null;
    }

    @Override
    protected View getMainView() {
        return userView;
    }

    @Override
    protected User loadDataCore(Bundle bundle) throws Exception {
        return null;
    }

    @Override
    protected void handleLoadResult(User data) {

    }
}
