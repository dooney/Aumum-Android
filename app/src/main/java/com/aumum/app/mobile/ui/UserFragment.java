package com.aumum.app.mobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.ui.view.FollowTextView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {
    private UserStore dataStore;

    private String userId;
    private User currentUser;

    protected View userView;
    protected FollowTextView followText;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataStore = UserStore.getInstance(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(UserActivity.INTENT_USER_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.invalid_user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userView = view.findViewById(R.id.user_view);

        followText = (FollowTextView) view.findViewById(R.id.text_follow);
        followText.setFollowListener(new FollowListener(userId));
    }

    @Override
    public void onDestroyView() {
        userView = null;

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dataStore.saveUser(getData());
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_user;
    }

    @Override
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return userView;
    }

    @Override
    protected User loadDataCore(Bundle bundle) throws Exception {
        currentUser = dataStore.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Unauthorized user");
        }
        if (currentUser.getObjectId().equals(userId)) {
            return currentUser;
        } else {
            User user = dataStore.getUserById(userId);
            if (user == null) {
                throw new Exception(getString(R.string.invalid_user));
            }
            return user;
        }
    }

    @Override
    protected void handleLoadResult(User user) {
        setData(user);
        if (currentUser == user) {
            followText.setVisibility(View.INVISIBLE);
            return;
        }
        if (currentUser.getFollowings().contains(user.getObjectId())) {
            followText.update(true);
        }
        return;
    }
}
