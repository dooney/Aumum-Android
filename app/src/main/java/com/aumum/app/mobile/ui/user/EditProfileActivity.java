package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.EditTextDialog;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.ui.view.dialog.TextViewDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import commons.validator.routines.EmailValidator;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 3/06/2015.
 */
public class EditProfileActivity extends BaseActionBarActivity
        implements TuSdkUtils.CameraListener,
                   TuSdkUtils.AlbumListener,
                   TuSdkUtils.CropListener,
                   FileUploadService.FileUploadListener {

    public static final String INTENT_CURRENT_USER = "currentUser";

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject FileUploadService fileUploadService;

    @InjectView(R.id.text_mobile) protected TextView mobileText;
    @InjectView(R.id.text_country) protected TextView countryText;
    @InjectView(R.id.layout_avatar) protected View avatarLayout;
    @InjectView(R.id.image_avatar) protected AvatarImageView avatarImage;
    @InjectView(R.id.layout_screen_name) protected View screenNameLayout;
    @InjectView(R.id.text_screen_name) protected TextView screenNameText;
    @InjectView(R.id.layout_email) protected View emailLayout;
    @InjectView(R.id.text_email) protected TextView emailText;
    @InjectView(R.id.layout_city) protected View cityLayout;
    @InjectView(R.id.text_city) protected TextView cityText;
    @InjectView(R.id.layout_area) protected View areaLayout;
    @InjectView(R.id.text_area) protected TextView areaText;
    @InjectView(R.id.layout_about) protected View aboutLayout;
    @InjectView(R.id.text_about) protected TextView aboutText;

    private User currentUser;
    private SafeAsyncTask<Boolean> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.inject(this);

        final String data = getIntent().getStringExtra(INTENT_CURRENT_USER);
        Gson gson = new Gson();
        currentUser = gson.fromJson(data, new TypeToken<User>(){}.getType());

        mobileText.setText(currentUser.getUsername());
        countryText.setText(currentUser.getCountry());
        avatarImage.getFromUrl(currentUser.getAvatarUrl());
        avatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraOptions();
            }
        });
        screenNameText.setText(currentUser.getScreenName());
        screenNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(EditProfileActivity.this,
                        R.layout.dialog_edit_text,
                        R.string.hint_screen_name,
                        new EditTextDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                String screenName = (String) value;
                                if (restService.getScreenNameRegistered((String) value)) {
                                    throw new Exception(getString(R.string.error_screen_name_registered));
                                }
                                restService.updateUserScreenName(currentUser.getObjectId(), screenName);
                                currentUser.setScreenName(screenName);
                                userStore.save(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                showMsg(errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String screenName = (String) value;
                                screenNameText.setText(screenName);
                            }
                        }).show();
            }
        });
        emailText.setText(currentUser.getEmail());
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(EditProfileActivity.this,
                        R.layout.dialog_edit_text,
                        R.string.hint_email,
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
                                showMsg(errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String email = (String) value;
                                emailText.setText(email);
                            }
                        }).show();
            }
        });
        cityText.setText(currentUser.getCity());
        cityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cityOptions[] = Constants.Options.CITY_OPTIONS;
                new ListViewDialog(EditProfileActivity.this,
                        getString(R.string.label_select_your_city),
                        Arrays.asList(cityOptions),
                        new ListViewDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int i) {
                                final String city = cityOptions[i];
                                final String text = getString(R.string.label_confirm_city, city);
                                new TextViewDialog(EditProfileActivity.this,
                                        text, new ConfirmDialog.OnConfirmListener() {
                                    @Override
                                    public void call(Object value) throws Exception {
                                        restService.updateUserCity(currentUser.getObjectId(), city);
                                        currentUser.setCity(city);
                                        userStore.save(currentUser);
                                    }

                                    @Override
                                    public void onException(String errorMessage) {
                                        showMsg(errorMessage);
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
        areaText.setText(currentUser.getArea());
        areaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cityId = Constants.Options.CITY_ID.get(currentUser.getCity());
                startAreaListActivity(cityId);
            }
        });
        aboutText.setText(currentUser.getAbout());
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(EditProfileActivity.this,
                        R.layout.dialog_edit_text_multiline,
                        R.string.hint_about,
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
                                showMsg(errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String about = (String) value;
                                aboutText.setText(about);
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.GET_AREA_LIST_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String area = data.getStringExtra(AreaListActivity.INTENT_AREA);
            updateArea(area);
        }
    }

    private void startAreaListActivity(int city) {
        final Intent intent = new Intent(this, AreaListActivity.class);
        intent.putExtra(AreaListActivity.INTENT_CITY, city);
        startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
    }

    private void updateArea(final String area) {
        final String text = getString(R.string.label_confirm_area, area);
        new TextViewDialog(this,
                text,
                new ConfirmDialog.OnConfirmListener() {
            @Override
            public void call(Object value) throws Exception {
                restService.updateUserArea(currentUser.getObjectId(), area);
                currentUser.setArea(area);
                userStore.save(currentUser);
            }

            @Override
            public void onException(String errorMessage) {
                showMsg(errorMessage);
            }

            @Override
            public void onSuccess(Object value) {
                areaText.setText(area);
            }
        }).show();
    }

    private void showCameraOptions() {
        String options[] = getResources().getStringArray(R.array.label_camera_actions);
        new ListViewDialog(this, null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                TuSdkUtils.camera(EditProfileActivity.this,
                                        EditProfileActivity.this);
                                break;
                            case 1:
                                TuSdkUtils.album(EditProfileActivity.this,
                                        EditProfileActivity.this);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void updateAvatar(final String avatarUrl) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.updateUserAvatar(currentUser.getObjectId(), avatarUrl);
                currentUser.setAvatarUrl(avatarUrl);
                userStore.save(currentUser);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onUploadSuccess(String remoteUrl) {
        updateAvatar(remoteUrl);
    }

    @Override
    public void onUploadFailure(Exception e) {
        showError(e);
    }

    private void onPhotoResult(ImageSqlInfo imageSqlInfo) {
        TuSdkUtils.crop(this, imageSqlInfo, true, this);
    }

    @Override
    public void onCameraResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
    }

    @Override
    public void onAlbumResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
    }

    @Override
    public void onCropResult(File file) {
        onFileResult(file);
    }

    private void onFileResult(File file) {
        try {
            String fileUri = file.getAbsolutePath();
            fileUploadService.setFileUploadListener(this);
            fileUploadService.upload(fileUri);
        } catch (Exception e) {
            showError(e);
        }
    }
}
