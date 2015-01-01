package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.asking.SearchAskingActivity;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.party.SearchPartyActivity;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import commons.validator.routines.EmailValidator;
import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends LoaderFragment<User> {

    @Inject RestService restService;
    @Inject UserStore userStore;

    private User currentUser;
    private SafeAsyncTask<Boolean> task;

    private ScrollView scrollView;
    private View mainView;
    private AvatarImageView avatarImage;
    private View screenNameLayout;
    private TextView screenNameText;
    private View emailLayout;
    private TextView emailText;
    private View cityLayout;
    private TextView cityText;
    private View areaLayout;
    private TextView areaText;
    private View aboutLayout;
    private TextView aboutText;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        
        mainView = view.findViewById(R.id.main_view);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameLayout = view.findViewById(R.id.layout_screen_name);
        screenNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(getActivity(), R.layout.dialog_edit_text, R.string.hint_screen_name,
                        new EditTextDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                String screenName = (String) value;
                                if (restService.getScreenNameRegistered((String)value)) {
                                    throw new Exception(getString(R.string.error_screen_name_registered));
                                }
                                restService.updateUserScreenName(currentUser.getObjectId(), screenName);
                                currentUser.setScreenName(screenName);
                                userStore.update(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                Toaster.showShort(getActivity(), errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String screenName = (String) value;
                                screenNameText.setText(screenName);
                            }

                            @Override
                            public void onFailed() {
                                Toaster.showShort(getActivity(), R.string.error_edit_profile);
                            }
                        }).show();
            }
        });
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        emailLayout = view.findViewById(R.id.layout_email);
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(getActivity(), R.layout.dialog_edit_text, R.string.hint_email,
                        new EditTextDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                String email = (String) value;
                                if (!EmailValidator.getInstance().isValid(email)) {
                                    throw new Exception(getString(R.string.error_incorrect_email_format));
                                }
                                if (restService.getEmailRegistered(email)) {
                                    throw new Exception(getString(R.string.error_email_registered));
                                }
                                restService.updateUserEmail(currentUser.getObjectId(), email);
                                currentUser.setEmail(email);
                                userStore.update(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                Toaster.showShort(getActivity(), errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String email = (String) value;
                                emailText.setText(email);
                            }

                            @Override
                            public void onFailed() {
                                Toaster.showShort(getActivity(), R.string.error_edit_profile);
                            }
                        }).show();
            }
        });
        emailText = (TextView) view.findViewById(R.id.text_email);
        cityLayout = view.findViewById(R.id.layout_city);
        cityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cityOptions[] = Constants.Options.CITY_OPTIONS;
                DialogUtils.showDialog(getActivity(), Constants.Options.CITY_OPTIONS,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                final String city = cityOptions[i];
                                final String text = getString(R.string.label_confirm_city, city);
                                new TextViewDialog(getActivity(), text, new ConfirmDialog.OnConfirmListener() {
                                    @Override
                                    public void call(Object value) throws Exception {
                                        restService.updateUserCity(currentUser.getObjectId(), city);
                                        currentUser.setCity(city);
                                        userStore.update(currentUser);
                                    }

                                    @Override
                                    public void onException(String errorMessage) {
                                        Toaster.showShort(getActivity(), errorMessage);
                                    }

                                    @Override
                                    public void onSuccess(Object value) {
                                        cityText.setText(city);
                                    }

                                    @Override
                                    public void onFailed() {
                                        Toaster.showShort(getActivity(), R.string.error_edit_profile);
                                    }
                                }).show();
                            }
                        });
            }
        });
        cityText = (TextView) view.findViewById(R.id.text_city);
        areaLayout = view.findViewById(R.id.layout_area);
        areaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String areaOptions[] = Constants.Options.AREA_OPTIONS.get(currentUser.getCity());
                DialogUtils.showDialog(getActivity(), areaOptions,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                final String area = areaOptions[i];
                                final String text = getString(R.string.label_confirm_area, area);
                                new TextViewDialog(getActivity(), text, new ConfirmDialog.OnConfirmListener() {
                                    @Override
                                    public void call(Object value) throws Exception {
                                        restService.updateUserArea(currentUser.getObjectId(), area);
                                        currentUser.setArea(area);
                                        userStore.update(currentUser);
                                    }

                                    @Override
                                    public void onException(String errorMessage) {
                                        Toaster.showShort(getActivity(), errorMessage);
                                    }

                                    @Override
                                    public void onSuccess(Object value) {
                                        areaText.setText(area);
                                    }

                                    @Override
                                    public void onFailed() {
                                        Toaster.showShort(getActivity(), R.string.error_edit_profile);
                                    }
                                }).show();
                            }
                        });
            }
        });
        areaText = (TextView) view.findViewById(R.id.text_area);
        aboutLayout = view.findViewById(R.id.layout_about);
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(getActivity(), R.layout.dialog_edit_text_multiline, R.string.hint_about,
                        new ConfirmDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                String about = (String) value;
                                restService.updateUserAbout(currentUser.getObjectId(), about);
                                currentUser.setAbout(about);
                                userStore.update(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                Toaster.showShort(getActivity(), errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String about = (String) value;
                                aboutText.setText(about);
                            }

                            @Override
                            public void onFailed() {
                                Toaster.showShort(getActivity(), R.string.error_edit_profile);
                            }
                }).show();
            }
        });
        aboutText = (TextView) view.findViewById(R.id.text_about);
    }

    @Override
    public void onDestroyView() {
        mainView = null;

        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.IMAGE_PICKER_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String imagePath = data.getStringExtra(ImagePickerActivity.INTENT_SINGLE_PATH);
            if (imagePath != null) {
                String imageUri = "file://" + imagePath;
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == Constants.RequestCode.CROP_PROFILE_IMAGE_REQ_CODE &&
                   resultCode == Activity.RESULT_OK) {
            String imageUrl = data.getStringExtra(UpdateAvatarActivity.INTENT_IMAGE_URL);
            if (imageUrl != null) {
                updateAvatar(imageUrl);
            }
            String imagePath = data.getStringExtra(UpdateAvatarActivity.INTENT_IMAGE_PATH);
            if (imagePath != null) {
                String imageUri = "file://" + imagePath;
                ImageLoaderUtils.displayImage(imageUri, avatarImage);
            }
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_profile;
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
        currentUser = userStore.getCurrentUserFromServer();
        return currentUser;
    }

    @Override
    protected void handleLoadResult(final User user) {
        try {
            setData(user);

            avatarImage.getFromUrl(user.getAvatarUrl());
            avatarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startImagePickerActivity();
                }
            });
            screenNameText.setText(user.getScreenName());
            emailText.setText(user.getEmail());
            cityText.setText(user.getCity());
            areaText.setText(user.getArea());
            aboutText.setText(user.getAbout());
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_profile_actions);
        DialogUtils.showDialog(getActivity(), options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                startMyPartiesActivity(currentUser);
                                break;
                            case 1:
                                startMyAskingsActivity(currentUser);
                                break;
                            case 2:
                                String options[] = getResources().getStringArray(R.array.label_favorite_types);
                                DialogUtils.showDialog(getActivity(), options,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                switch (i) {
                                                    case 0:
                                                        startMyFavoritePartiesActivity(currentUser);
                                                        break;
                                                    case 1:
                                                        startMyFavoriteAskingsActivity(currentUser);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        });
                                break;
                            case 3:
                                startSettingsActivity();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void startImagePickerActivity() {
        final Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_ACTION, ImagePickerActivity.ACTION_PICK);
        startActivityForResult(intent, Constants.RequestCode.IMAGE_PICKER_REQ_CODE);
    }

    private void startCropImageActivity(String imageUri) {
        final Intent intent = new Intent(getActivity(), UpdateAvatarActivity.class);
        intent.putExtra(UpdateAvatarActivity.INTENT_TITLE, getString(R.string.title_activity_change_avatar));
        intent.putExtra(UpdateAvatarActivity.INTENT_IMAGE_URI, imageUri);
        startActivityForResult(intent, Constants.RequestCode.CROP_PROFILE_IMAGE_REQ_CODE);
    }

    private void updateAvatar(final String fileUrl) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.updateUserAvatar(currentUser.getObjectId(), fileUrl);
                currentUser.setAvatarUrl(fileUrl);
                userStore.update(currentUser);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    private void startMyPartiesActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, getString(R.string.label_my_parties));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, user.getObjectId());
        startActivity(intent);
    }

    private void startMyFavoritePartiesActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, getString(R.string.label_favorite_parties));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, user.getObjectId());
        intent.putExtra(SearchPartyActivity.INTENT_IS_FAVORITE, true);
        startActivity(intent);
    }

    private void startMyAskingsActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE, getString(R.string.label_my_askings));
        intent.putExtra(SearchAskingActivity.INTENT_USER_ID, user.getObjectId());
        startActivity(intent);
    }

    private void startMyFavoriteAskingsActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE, getString(R.string.label_favorite_askings));
        intent.putExtra(SearchAskingActivity.INTENT_USER_ID, user.getObjectId());
        intent.putExtra(SearchAskingActivity.INTENT_IS_FAVORITE, true);
        startActivity(intent);
    }

    private void startSettingsActivity() {
        final Intent intent = new Intent(getActivity(), SettingsActivity.class);
        getActivity().startActivityForResult(intent, Constants.RequestCode.SETTINGS_REQ_CODE);
    }
}
