package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.ApiKeyProvider;
import com.aumum.app.mobile.core.ImageUtils;
import com.aumum.app.mobile.core.ReceiveUriScaledBitmapTask;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.view.EditProfileTextView;
import com.aumum.app.mobile.ui.view.FollowTextView;
import com.aumum.app.mobile.ui.view.ProgressDialog;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User>
        implements ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener {
    @Inject ApiKeyProvider apiKeyProvider;
    private UserStore dataStore;
    private Uri outputUri;

    private String userId;
    private String currentUserId;

    private final ProgressDialog progress = ProgressDialog.newInstance(R.string.message_loading);

    private View mainView;
    private ImageView avatarImage;
    private TextView userNameText;
    private TextView partyCountText;
    private TextView followingCountText;
    private TextView followedCountText;
    private FollowTextView followText;
    private EditProfileTextView editProfileText;

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

        avatarImage = (ImageView) view.findViewById(R.id.image_avatar);

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
        mainView = null;

        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            //handleCrop(resultCode, data);
        } else if (requestCode == ImageUtils.GALLERY_INTENT_CALLED && resultCode == Activity.RESULT_OK) {
            Uri originalUri = data.getData();
            if (originalUri != null) {
                showProgress();
                new ReceiveUriScaledBitmapTask(getActivity(), this).execute(originalUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    protected void handleLoadResult(User user) throws Exception {
        if (user != null) {
            setData(user);
            userNameText.setText(user.getUsername());
            if (userId.equals(currentUserId)) {
                avatarImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startImageSelector();
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
            partyCountText.setText(String.valueOf(user.getParties().size()));
            followingCountText.setText(String.valueOf(user.getFollowings().size()));
            followedCountText.setText(String.valueOf(user.getFollowers().size()));
        }
    }

    private void startImageSelector() {
        ImageUtils.getImageFromFragment(this);
    }

    @Override
    public void onUriScaledBitmapReceived(Uri uri) {
        hideProgress();
        startCropActivity(uri);
    }

    private void startCropActivity(Uri originalUri) {
        outputUri = Uri.fromFile(new File(getActivity().getCacheDir(), Crop.class.getName()));
        new Crop(originalUri).output(outputUri).asSquare().start(getActivity());
    }

    private synchronized void showProgress() {
        if (!progress.isAdded()) {
            progress.show(getActivity().getFragmentManager(), null);
        }
    }

    private synchronized void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            progress.dismissAllowingStateLoss();
        }
    }
}
