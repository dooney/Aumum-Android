package com.aumum.app.mobile.ui.account;

import android.content.DialogInterface;
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
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.contact.InviteContactsActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.user.UpdateAvatarActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.greenhalolabs.emailautocompletetextview.EmailAutoCompleteTextView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;

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

    @InjectView(R.id.text_city) protected TextView cityButton;
    @InjectView(R.id.text_area) protected TextView areaButton;
    @InjectView(R.id.et_about) protected EditText aboutText;

    private String userId;
    private String screenName;
    private String email;
    private int city;
    private int area;
    private String about;

    private Validator validator;
    private SafeAsyncTask<Boolean> task;
    private final TextWatcher watcher = validationTextWatcher();

    public static final String INTENT_USER_ID = "userId";
    private static final int INVITE_CONTACTS_REQ_CODE = 100;

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
        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(CompleteProfileActivity.this, Constants.Options.CITY_OPTIONS,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        city = i;
                        cityButton.setText(Constants.Options.CITY_OPTIONS[i]);
                        cityButton.setTextColor(getResources().getColor(R.color.black));
                        updateUIWithValidation();
                    }
                });
            }
        });
        areaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityButton.getText() == null) {
                    Toaster.showShort(CompleteProfileActivity.this, R.string.error_city_first);
                    return;
                }
                final String areaOptions[] = Constants.Options.AREA_OPTIONS.get(city);
                DialogUtils.showDialog(CompleteProfileActivity.this, areaOptions,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                area = i;
                                areaButton.setText(areaOptions[i]);
                                areaButton.setTextColor(getResources().getColor(R.color.black));
                                updateUIWithValidation();
                            }
                        });
            }
        });
        validator = new Validator(this);
        validator.setValidationListener(this);

        Animation.flyIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_save));
        menuItem.setActionView(R.layout.menuitem_button_save);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        saveButton = (Button) view.findViewById(R.id.b_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailText.getText().length() > 0) {
                    validator.validate();
                } else {
                    save();
                }
            }
        });
        updateUIWithValidation();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.IMAGE_PICKER_IMAGE_REQ_CODE &&
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
            String imagePath = data.getStringExtra(UpdateAvatarActivity.INTENT_IMAGE_PATH);
            if (imagePath != null) {
                String imageUri = "file://" + imagePath;
                ImageLoaderUtils.displayImage(imageUri, avatarImage);
            }
        } else if (requestCode == INVITE_CONTACTS_REQ_CODE) {
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
                cityButton.getText().length() > 0 &&
                areaButton.getText().length() > 0;
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
        if (emailText.getText().length() > 0) {
            email = emailText.getText().toString();
        }
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
                if (email != null && restService.getEmailRegistered(email)) {
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
                startInviteContactsActivity();
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
        startActivityForResult(intent, Constants.RequestCode.IMAGE_PICKER_IMAGE_REQ_CODE);
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

    @Override
    public void onBackPressed() {
        Toaster.showShort(this, R.string.info_save_profile_before_back);
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

    private void startInviteContactsActivity() {
        final Intent intent = new Intent(this, InviteContactsActivity.class);
        intent.putExtra(InviteContactsActivity.INTENT_SHOW_SKIP, true);
        startActivityForResult(intent, INVITE_CONTACTS_REQ_CODE);
    }
}
