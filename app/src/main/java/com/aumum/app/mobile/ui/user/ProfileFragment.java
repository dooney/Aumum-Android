package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.asking.SearchAskingActivity;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.party.SearchPartyActivity;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.Arrays;

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
        MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showActionDialog();
                }
            }
        });
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
                                userStore.save(currentUser);
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
                                userStore.save(currentUser);
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
                        }).show();
            }
        });
        emailText = (TextView) view.findViewById(R.id.text_email);
        cityLayout = view.findViewById(R.id.layout_city);
        cityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cityOptions[] = Constants.Options.CITY_OPTIONS;
                new ListViewDialog(getActivity(),
                        getString(R.string.label_select_your_city),
                        Arrays.asList(cityOptions),
                        new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        final String city = cityOptions[i];
                        final String text = getString(R.string.label_confirm_city, city);
                        new TextViewDialog(getActivity(), text, new ConfirmDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                restService.updateUserCity(currentUser.getObjectId(), city);
                                currentUser.setCity(city);
                                userStore.save(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                Toaster.showShort(getActivity(), errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                cityText.setText(city);
                            }
                        }).show();
                    }
                }).show();
            }
        });
        cityText = (TextView) view.findViewById(R.id.text_city);
        areaLayout = view.findViewById(R.id.layout_area);
        areaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cityId = Constants.Options.CITY_ID.get(currentUser.getCity());
                startAreaListActivity(cityId);
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
                                userStore.save(currentUser);
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
            String imageUri = data.getStringExtra(UpdateAvatarActivity.INTENT_IMAGE_URI);
            if (imageUri != null) {
                ImageLoaderUtils.displayImage(imageUri, avatarImage);
            }
        } else if (requestCode == Constants.RequestCode.GET_AREA_LIST_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String area = data.getStringExtra(AreaListActivity.INTENT_AREA);
            updateArea(area);
        }
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
        if (user != null) {
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
        }
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_profile_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        showFavoriteDialog();
                        break;
                    case 1:
                        startSettingsActivity();
                        break;
                    default:
                        break;
                }
            }
        }).show();
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
                userStore.save(currentUser);
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

    private void startAreaListActivity(int city) {
        final Intent intent = new Intent(getActivity(), AreaListActivity.class);
        intent.putExtra(AreaListActivity.INTENT_CITY, city);
        startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
    }

    private void updateArea(final String area) {
        final String text = getString(R.string.label_confirm_area, area);
        new TextViewDialog(getActivity(), text, new ConfirmDialog.OnConfirmListener() {
            @Override
            public void call(Object value) throws Exception {
                restService.updateUserArea(currentUser.getObjectId(), area);
                currentUser.setArea(area);
                userStore.save(currentUser);
            }

            @Override
            public void onException(String errorMessage) {
                Toaster.showShort(getActivity(), errorMessage);
            }

            @Override
            public void onSuccess(Object value) {
                areaText.setText(area);
            }
        }).show();
    }

    private void showFavoriteDialog() {
        String options[] = getResources().getStringArray(R.array.label_favorite_types);
        new ListViewDialog(getActivity(),
                getString(R.string.label_select_favorite_type),
                Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
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
                }).show();
    }

    private void startMyFavoritePartiesActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, getString(R.string.label_favorite_parties));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, user.getObjectId());
        intent.putExtra(SearchPartyActivity.INTENT_IS_FAVORITE, true);
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
