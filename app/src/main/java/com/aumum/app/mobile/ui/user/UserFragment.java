package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.EditProfileTextView;
import com.aumum.app.mobile.ui.view.FollowTextView;
import com.aumum.app.mobile.utils.Ln;

import org.w3c.dom.Text;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {
    @Inject ApiKeyProvider apiKeyProvider;
    private UserStore dataStore;

    private String userId;
    private String currentUserId;

    private View mainView;
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private FollowTextView followText;
    private EditProfileTextView editProfileText;
    private TextView areaText;
    private TextView aboutText;
    private TextView followingsCountText;
    private TextView followersCountText;
    private TextView commentsCountText;
    private TextView partyPostCountText;
    private TextView joinedPartyCountText;

    private final int PROFILE_IMAGE_REQ_CODE = 32;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        dataStore = UserStore.getInstance(getActivity());
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
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        followText = (FollowTextView) view.findViewById(R.id.text_follow);
        followText.setFollowListener(new FollowListener(userId));
        editProfileText = (EditProfileTextView) view.findViewById(R.id.text_edit_profile);
        areaText = (TextView) view.findViewById(R.id.text_area);
        aboutText = (TextView) view.findViewById(R.id.text_about);
        followingsCountText = (TextView) view.findViewById(R.id.text_followings_count);
        followersCountText = (TextView) view.findViewById(R.id.text_followers_count);
        commentsCountText = (TextView) view.findViewById(R.id.text_comments_count);
        partyPostCountText = (TextView) view.findViewById(R.id.text_party_post_count);
        joinedPartyCountText = (TextView) view.findViewById(R.id.text_joined_party_count);
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
            return dataStore.getCurrentUser(true);
        } else {
            User user = dataStore.getUserById(userId, true);
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

                avatarImage.getFromUrl(user.getAvatarUrl());
                userNameText.setText(user.getScreenName());
                if (userId.equals(currentUserId)) {
                    avatarImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startProfileImageActivity(userId, user.getAvatarUrl());
                        }
                    });
                    followText.setVisibility(View.GONE);
                    editProfileText.setVisibility(View.VISIBLE);
                } else {
                    editProfileText.setVisibility(View.GONE);
                    followText.setVisibility(View.VISIBLE);
                }
                if (user.getFollowers().contains(currentUserId)) {
                    followText.update(true);
                }
                areaText.setText(Constants.AREA_OPTIONS[user.getArea()]);
                aboutText.setText(user.getAbout());
                followingsCountText.setText(String.valueOf(user.getFollowings().size()));
                followersCountText.setText(String.valueOf(user.getFollowers().size()));
                commentsCountText.setText(String.valueOf(user.getComments().size()));
                partyPostCountText.setText(String.valueOf(user.getPartyPosts().size()));
                joinedPartyCountText.setText(String.valueOf(user.getParties().size()));
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void startProfileImageActivity(String userId, String avatarUrl) {
        final Intent intent = new Intent(getActivity(), UserProfileImageActivity.class);
        intent.putExtra(UserProfileImageActivity.INTENT_USER_ID, userId);
        intent.putExtra(UserProfileImageActivity.INTENT_AVATAR_URL, avatarUrl);
        startActivityForResult(intent, PROFILE_IMAGE_REQ_CODE);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == PROFILE_IMAGE_REQ_CODE && resultCode == Activity.RESULT_OK) {
            refresh(null);
        }
    }
}
