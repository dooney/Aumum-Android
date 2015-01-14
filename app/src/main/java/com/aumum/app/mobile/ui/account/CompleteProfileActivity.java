package com.aumum.app.mobile.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.contact.MobileContactsActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.user.UpdateAvatarActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.greenhalolabs.emailautocompletetextview.EmailAutoCompleteTextView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class CompleteProfileActivity extends ProgressDialogActivity
    implements Validator.ValidationListener {

    @Inject RestService restService;

    private Button saveButton;
    @InjectView(R.id.image_avatar) protected ImageView avatarImage;
    @InjectView(R.id.et_screen_name) protected EditText screenNameText;

    @InjectView(R.id.et_email)
    @Email(messageResId = R.string.error_incorrect_email_format)
    protected EmailAutoCompleteTextView emailText;

    @InjectView(R.id.et_city) protected EditText cityText;
    @InjectView(R.id.et_area) protected EditText areaText;
    @InjectView(R.id.et_about) protected EditText aboutText;

    private String userId;
    private String screenName;
    private String email;
    private String city;
    private String area;
    private String about;

    private Validator validator;
    private SafeAsyncTask<Boolean> task;
    private final TextWatcher watcher = validationTextWatcher();

    public static final String INTENT_USER_ID = "userId";
    private static final int MOBILE_CONTACTS_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.inject(this);

        userId = getIntent().getStringExtra(INTENT_USER_ID);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImagePickerActivity();
            }
        });
        screenNameText.addTextChangedListener(watcher);
        emailText.addTextChangedListener(watcher);
        cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cityOptions[] = Constants.Options.CITY_OPTIONS;
                new ListViewDialog(CompleteProfileActivity.this,
                        getString(R.string.label_select_your_city),
                        Arrays.asList(cityOptions),
                        new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        city = cityOptions[i];
                        cityText.setText(cityOptions[i]);
                    }
                }).show();
            }
        });
        cityText.addTextChangedListener(watcher);
        areaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (city == null) {
                    Toaster.showShort(CompleteProfileActivity.this, R.string.error_city_first);
                    return;
                }
                int cityId = Constants.Options.CITY_ID.get(city);
                startAreaListActivity(cityId);
            }
        });
        areaText.addTextChangedListener(watcher);
        validator = new Validator(this);
        validator.setValidationListener(this);

        Animation.flyIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_save);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        saveButton = (Button) view.findViewById(R.id.b_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        updateUIWithValidation();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.IMAGE_PICKER_REQ_CODE &&
                resultCode == RESULT_OK) {
            String imagePath = data.getStringExtra(ImagePickerActivity.INTENT_SINGLE_PATH);
            if (imagePath != null) {
                String imageUri = "file://" + imagePath;
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == Constants.RequestCode.CROP_PROFILE_IMAGE_REQ_CODE &&
                resultCode == RESULT_OK) {
            String imageUrl = data.getStringExtra(UpdateAvatarActivity.INTENT_IMAGE_URL);
            if (imageUrl != null) {
                updateAvatar(imageUrl);
            }
            String imageUri = data.getStringExtra(UpdateAvatarActivity.INTENT_IMAGE_URI);
            if (imageUri != null) {
                ImageLoaderUtils.displayImage(imageUri, avatarImage);
            }
        } else if (requestCode == Constants.RequestCode.GET_AREA_LIST_REQ_CODE &&
                resultCode == RESULT_OK) {
            String area = data.getStringExtra(AreaListActivity.INTENT_AREA);
            updateArea(area);
        } else if (requestCode == MOBILE_CONTACTS_REQ_CODE) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(screenNameText) &&
                populated(emailText) &&
                cityText.getText().length() > 0 &&
                areaText.getText().length() > 0;
        saveButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void save() {
        if (task != null) {
            return;
        }
        progress.setMessageId(R.string.info_submitting_profile);
        showProgress();
        screenName = screenNameText.getText().toString();
        EditTextUtils.hideSoftInput(screenNameText);
        email = emailText.getText().toString();
        EditTextUtils.hideSoftInput(emailText);
        if (aboutText.getText().length() > 0) {
            about = aboutText.getText().toString();
        }
        EditTextUtils.hideSoftInput(aboutText);
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (restService.getScreenNameRegistered(screenName)) {
                    throw new Exception(getString(R.string.error_screen_name_registered));
                }
                if (restService.getEmailRegistered(email)) {
                    throw new Exception(getString(R.string.error_email_registered));
                }
                User user = new User();
                user.setObjectId(userId);
                user.setScreenName(screenName);
                user.setEmail(email);
                user.setCity(city);
                user.setArea(area);
                user.setAbout(about);
                restService.updateUserProfile(user);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(CompleteProfileActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                startMobileContactsActivity();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    private void startImagePickerActivity() {
        final Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_ACTION, ImagePickerActivity.ACTION_PICK);
        startActivityForResult(intent, Constants.RequestCode.IMAGE_PICKER_REQ_CODE);
    }

    private void startCropImageActivity(String imageUri) {
        final Intent intent = new Intent(this, UpdateAvatarActivity.class);
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
                restService.updateUserAvatar(userId, fileUrl);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(CompleteProfileActivity.this, cause.getMessage());
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
        final Intent intent = new Intent(this, AreaListActivity.class);
        intent.putExtra(AreaListActivity.INTENT_CITY, city);
        startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
    }

    private void updateArea(String value) {
        area = value;
        areaText.setText(value);
    }

    @Override
    public void onBackPressed() {
        Toaster.showShort(this, R.string.info_profile_not_completed_yet);
    }

    @Override
    public void onValidationSucceeded() {
        save();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            Toaster.showShort(this, error.getFailedRules().get(0).getMessage(this));
        }
    }

    private void startMobileContactsActivity() {
        final Intent intent = new Intent(this, MobileContactsActivity.class);
        intent.putExtra(MobileContactsActivity.INTENT_USER_ID, userId);
        intent.putExtra(MobileContactsActivity.INTENT_SHOW_SKIP, true);
        startActivityForResult(intent, MOBILE_CONTACTS_REQ_CODE);
    }
}
