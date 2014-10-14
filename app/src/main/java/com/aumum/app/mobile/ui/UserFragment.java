package com.aumum.app.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.authenticator.ApiKeyProvider;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.ui.view.EditProfileTextView;
import com.aumum.app.mobile.ui.view.FollowTextView;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {
    @Inject ApiKeyProvider apiKeyProvider;
    private UserStore dataStore;

    private String userId;
    private User currentUser;

    protected View userView;
    protected TextView userNameText;
    protected TextView partyCountText;
    protected TextView followingCountText;
    protected TextView followedCountText;
    protected FollowTextView followText;
    protected EditProfileTextView editProfileText;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        dataStore = UserStore.getInstance(getActivity());
        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(UserActivity.INTENT_USER_ID);
        if (userId == null) {
            userId = apiKeyProvider.getAuthUserId();
        }
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

        userNameText = (TextView) view.findViewById(R.id.text_user_name);

        followText = (FollowTextView) view.findViewById(R.id.text_follow);
        followText.setFollowListener(new FollowListener(userId));

        editProfileText = (EditProfileTextView) view.findViewById(R.id.text_edit_profile);

        partyCountText = (TextView) view.findViewById(R.id.text_party_count);
        followingCountText = (TextView) view.findViewById(R.id.text_following_count);
        followedCountText = (TextView) view.findViewById(R.id.text_followed_count);
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
        userNameText.setText(user.getUsername());
        if (currentUser == user) {
            followText.setVisibility(View.GONE);
            editProfileText.setVisibility(View.VISIBLE);
        } else {
            editProfileText.setVisibility(View.GONE);
            followText.setVisibility(View.VISIBLE);
        }
        if (currentUser.getFollowings().contains(user.getObjectId())) {
            followText.update(true);
        }
        partyCountText.setText(String.valueOf(user.getParties().size()));
        followingCountText.setText(String.valueOf(user.getFollowings().size()));
        followedCountText.setText(String.valueOf(user.getFollowers().size()));
        return;
    }
}
