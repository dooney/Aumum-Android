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
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.browser.BrowserActivity;
import com.aumum.app.mobile.ui.credit.CreditPurchaseActivity;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Inject CreditRuleStore creditRuleStore;

    private User currentUser;
    private SafeAsyncTask<Boolean> task;

    private ScrollView scrollView;
    private View mainView;
    private AvatarImageView avatarImage;
    private View screenNameLayout;
    private TextView screenNameText;
    private View creditLayout;
    private TextView creditText;
    private TextView creditInfoText;
    private View emailLayout;
    private TextView emailText;
    private View cityLayout;
    private TextView cityText;
    private View areaLayout;
    private TextView areaText;
    private View tagsLayout;
    private TextView tagsText[];
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
        creditLayout = view.findViewById(R.id.layout_credit);
        creditText = (TextView) view.findViewById(R.id.text_credit);
        creditInfoText = (TextView) view.findViewById(R.id.text_credit_info);
        creditInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCreditInfoActivity();
            }
        });
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
        tagsText = new TextView[3];
        tagsText[0] = (TextView) view.findViewById(R.id.text_tag1);
        tagsText[1] = (TextView) view.findViewById(R.id.text_tag2);
        tagsText[2] = (TextView) view.findViewById(R.id.text_tag3);
        tagsLayout = view.findViewById(R.id.layout_tags);
        tagsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUserTagListActivity();
            }
        });
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
                                if (currentUser.getAbout() == null) {
                                    updateCredit(CreditRule.ADD_ABOUT);
                                }
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
                                creditText.setText(String.valueOf(currentUser.getCredit()));
                            }
                }).show();
            }
        });
        aboutText = (TextView) view.findViewById(R.id.text_about);
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
        } else if (requestCode == Constants.RequestCode.GET_USER_TAG_LIST_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            final ArrayList<String> userTags =
                    data.getStringArrayListExtra(UserTagListActivity.INTENT_USER_TAGS);
            updateTags(userTags);
        } else if (requestCode == Constants.RequestCode.CREDIT_PURCHASE_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            int credit = data.getIntExtra(CreditPurchaseActivity.INTENT_CURRENT_CREDIT,
                    currentUser.getCredit());
            creditText.setText(String.valueOf(credit));
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
            creditText.setText(String.valueOf(user.getCredit()));
            emailText.setText(user.getEmail());
            cityText.setText(user.getCity());
            areaText.setText(user.getArea());
            updateTagsUI(user.getTags());
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
                        startCreditPurchaseActivity();
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
                if (currentUser.getAvatarUrl() == null) {
                    updateCredit(CreditRule.ADD_AVATAR);
                }
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
            protected void onSuccess(Boolean success) throws Exception {
                creditText.setText(String.valueOf(currentUser.getCredit()));
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

    private void updateTagsUI(final List<String> tags) {
        for (int i = 0; i < tagsText.length; i++) {
            tagsText[i].setText("");
            tagsText[i].setVisibility(View.GONE);
        }
        for (int i = 0; i < tags.size(); i++) {
            tagsText[i].setText(tags.get(i));
            tagsText[i].setVisibility(View.VISIBLE);
        }
    }

    private void updateTags(final List<String> tags) {
        final String text = getString(R.string.label_confirm_user_tags);
        new TextViewDialog(getActivity(), text, new ConfirmDialog.OnConfirmListener() {
            @Override
            public void call(Object value) throws Exception {
                restService.updateUserTags(currentUser.getObjectId(), tags);
                if (currentUser.getTags().size() == 0) {
                    updateCredit(CreditRule.ADD_TAGS);
                }
                currentUser.setTags(tags);
                userStore.save(currentUser);
            }

            @Override
            public void onException(String errorMessage) {
                Toaster.showShort(getActivity(), errorMessage);
            }

            @Override
            public void onSuccess(Object value) {
                updateTagsUI(tags);
                creditText.setText(String.valueOf(currentUser.getCredit()));
            }
        }).show();
    }

    private void updateCredit(int seq) {
        final CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            final int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toaster.showShort(getActivity(), getString(R.string.info_got_credit,
                            creditRule.getDescription(), credit));
                }
            });
        }
    }

    private void startSettingsActivity() {
        final Intent intent = new Intent(getActivity(), SettingsActivity.class);
        getActivity().startActivityForResult(intent, Constants.RequestCode.SETTINGS_REQ_CODE);
    }

    private void startUserTagListActivity() {
        final Intent intent = new Intent(getActivity(), UserTagListActivity.class);
        startActivityForResult(intent, Constants.RequestCode.GET_USER_TAG_LIST_REQ_CODE);
    }

    private void startCreditPurchaseActivity() {
        final Intent intent = new Intent(getActivity(), CreditPurchaseActivity.class);
        startActivityForResult(intent, Constants.RequestCode.CREDIT_PURCHASE_REQ_CODE);
    }

    private void startCreditInfoActivity() {
        final Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.INTENT_URL, Constants.Link.CREDIT);
        startActivity(intent);
    }
}
