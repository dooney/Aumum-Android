package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.utils.Ln;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject UserStore dataStore;

    private String userId;
    private String currentUserId;

    private View mainView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        currentUserId = apiKeyProvider.getAuthUserId();
        userId = intent.getStringExtra(UserActivity.INTENT_USER_ID);
        if (userId == null) {
            userId = currentUserId;
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

        mainView = view.findViewById(R.id.main_view);
    }

    @Override
    public void onDestroyView() {
        mainView = null;

        super.onDestroyView();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_user;
    }

    @Override
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected User loadDataCore(Bundle bundle) throws Exception {
        if (userId.equals(currentUserId)) {
            return dataStore.getCurrentUser();
        } else {
            User user = dataStore.getUserByIdFromServer(userId);
            if (user == null) {
                throw new Exception(getString(R.string.invalid_user));
            }
            return user;
        }
    }

    @Override
    protected void handleLoadResult(final User user) {
        try {
            if (user != null) {
                setData(user);
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void startProfileImageActivity(String userId, String avatarUrl) {
        final Intent intent = new Intent(getActivity(), UserProfileImageActivity.class);
        intent.putExtra(UserProfileImageActivity.INTENT_USER_ID, userId);
        intent.putExtra(UserProfileImageActivity.INTENT_AVATAR_URL, avatarUrl);
        startActivityForResult(intent, Constants.RequestCode.PROFILE_IMAGE_REQ_CODE);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.PROFILE_IMAGE_REQ_CODE && resultCode == Activity.RESULT_OK) {
            refresh(null);
        }
    }
}
