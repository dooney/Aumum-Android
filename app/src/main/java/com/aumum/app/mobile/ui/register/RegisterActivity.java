package com.aumum.app.mobile.ui.register;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.crop.CropImageActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.greenhalolabs.emailautocompletetextview.EmailAutoCompleteTextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

import static com.aumum.app.mobile.ui.splash.SplashActivity.KEY_ACCOUNT_EMAIL;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_SIGN_IN;

public class RegisterActivity extends ProgressDialogActivity
        implements FileUploadService.OnFileUploadListener {
    @Inject RestService restService;
    @Inject ChatService chatService;

    @InjectView(R.id.et_email) protected EmailAutoCompleteTextView emailText;
    @InjectView(R.id.et_password) protected EditText passwordText;
    @InjectView(R.id.et_screen_name) protected EditText screenNameText;
    @InjectView(R.id.b_area) protected TextView areaButton;
    @InjectView(R.id.image_avatar) protected ImageView avatarImage;
    @InjectView(R.id.b_sign_up) protected Button signUpButton;
    @InjectView(R.id.t_prompt_sign_in) protected TextView promptSignInText;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> task;

    private String email;
    private String password;
    private String screenName;
    private int area;

    private FileUploadService fileUploadService;
    private Bitmap avatarBitmap;
    private byte[] avatarData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        setContentView(R.layout.activity_register);

        ButterKnife.inject(this);

        emailText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);
        screenNameText.addTextChangedListener(watcher);

        areaButton.addTextChangedListener(watcher);
        areaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(RegisterActivity.this, Constants.Options.AREA_OPTIONS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        area = i;
                        areaButton.setText(Constants.Options.AREA_OPTIONS[i]);
                        areaButton.setTextColor(getResources().getColor(R.color.black));
                    }
                });
            }
        });

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageSelectionActivity();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setMessageId(R.string.info_processing_registration);
                showProgress();
                if (avatarBitmap != null) {
                    saveUserAvatar();
                } else {
                    register(null);
                }
            }
        });

        promptSignInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putExtra(SHOW_SIGN_IN, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        fileUploadService = new FileUploadService();
        fileUploadService.setOnFileUploadListener(this);

        Animation.flyIn(this);
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(emailText) &&
                populated(passwordText) &&
                populated(screenNameText) &&
                areaButton.getText().length() > 0;
        signUpButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void finishRegistration() {
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_EMAIL, email);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onRegistrationResult(final Boolean result) {
        if (result) {
            finishRegistration();
        } else {
            Toaster.showLong(RegisterActivity.this, R.string.error_authentication);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.GALLERY_INTENT_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            Uri originalUri = data.getData();
            if (originalUri != null) {
                startCropImageActivity(originalUri.toString());
            }
        } else if (requestCode == Constants.RequestCode.CROP_PROFILE_IMAGE_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String avatarImagePath = data.getStringExtra(CropImageActivity.INTENT_BITMAP);
            avatarBitmap = BitmapFactory.decodeFile(avatarImagePath);
            avatarImage.setImageBitmap(avatarBitmap);
        }
    }

    private void startImageSelectionActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, Constants.RequestCode.GALLERY_INTENT_REQ_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.RequestCode.GALLERY_INTENT_REQ_CODE);
        }
    }

    private void startCropImageActivity(String imageUri) {
        final Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra(CropImageActivity.INTENT_TITLE, getString(R.string.title_activity_change_avatar));
        intent.putExtra(CropImageActivity.INTENT_IMAGE_URI, imageUri);
        startActivityForResult(intent, Constants.RequestCode.CROP_PROFILE_IMAGE_REQ_CODE);
    }

    private void saveUserAvatar() {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                avatarData = ImageUtils.getBytesBitmap(avatarBitmap);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(RegisterActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                String fileName = emailText.getText().toString() + ".jpg";
                fileUploadService.upload(fileName, avatarData);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onUploadSuccess(final String fileUrl) {
        register(fileUrl);
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        Toaster.showLong(RegisterActivity.this, R.string.error_upload_profile_image);
        Ln.d(e);
    }

    private void register(final String avatarUrl) {
        if (task != null) {
            return;
        }

        password = passwordText.getText().toString();
        email = emailText.getText().toString();
        screenName = screenNameText.getText().toString();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User response = restService.register(email, password, screenName, area, avatarUrl);
                String chatId = response.getObjectId().toLowerCase();
                restService.updateUserChatId(response.getObjectId(), chatId);
                chatService.createAccount(chatId, password);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(RegisterActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                onRegistrationResult(success);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }
}
