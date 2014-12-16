package com.aumum.app.mobile.ui.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.AuthenticateActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.Strings;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_SIGN_IN;

public class RegisterActivity extends AuthenticateActivity
    implements ConfirmPhoneDialog.OnConfirmListener {
    @Inject RestService restService;
    @Inject ChatService chatService;

    @InjectView(R.id.text_country) protected TextView countryText;
    @InjectView(R.id.text_country_code) protected TextView countryCodeText;
    @InjectView(R.id.et_phone) protected EditText phoneText;
    @InjectView(R.id.et_password) protected EditText passwordText;
    @InjectView(R.id.b_sign_up) protected Button signUpButton;
    @InjectView(R.id.t_prompt_sign_in) protected TextView promptSignInText;

    private final TextWatcher watcher = validationTextWatcher();

    private EventHandler handler;
    private SafeAsyncTask<Boolean> task;

    private String countryCode;
    private String phone;
    private String userId;
    private String password;
    private String token;

    private final int VERIFY_ACTIVITY_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        countryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String countryOptions[] = Constants.Options.COUNTRY_OPTIONS;
                DialogUtils.showDialog(RegisterActivity.this, countryOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        countryText.setText(countryOptions[i]);
                        switch (i) {
                            case 1:
                                countryCodeText.setText("0064");
                                break;
                            case 2:
                                countryCodeText.setText("0086");
                                break;
                            default:
                                countryCodeText.setText("0061");
                                break;
                        }
                    }
                });
            }
        });
        phoneText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmPhoneDialog dialog = new ConfirmPhoneDialog(RegisterActivity.this, RegisterActivity.this);
                countryCode = countryCodeText.getText().toString().trim();
                phone = phoneText.getText().toString().trim();
                password = passwordText.getText().toString();
                dialog.setPhone(countryCode, phone);
                dialog.show();
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

        handler = new EventHandler() {
            public void afterEvent(final int event, final int result, final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            startVerifyActivity();
                        } else {
                            Toaster.showShort(RegisterActivity.this,
                                    R.string.error_send_verification_sms);
                        }
                    }
                });
            }
        };

        Animation.flyIn(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
        SMSSDK.registerEventHandler(handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SMSSDK.unregisterEventHandler(handler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VERIFY_ACTIVITY_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                userId = data.getStringExtra(VerifyActivity.INTENT_USER_ID);
                token = data.getStringExtra(VerifyActivity.INTENT_TOKEN);
                finishAuthentication(userId, password, token);
            }
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
        final boolean populated = populated(phoneText) &&
                populated(passwordText);
        signUpButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Override
    public void onConfirmPhone() {
        progress.setMessageId(R.string.info_sending_verification_sms);
        showProgress();
        SMSSDK.getVerificationCode(Strings.removeLeadingZeros(countryCode), phone);
    }

    private void startVerifyActivity() {
        final Intent intent = new Intent(this, VerifyActivity.class);
        intent.putExtra(VerifyActivity.INTENT_COUNTRY_CODE, countryCode);
        intent.putExtra(VerifyActivity.INTENT_PHONE, phone);
        intent.putExtra(VerifyActivity.INTENT_PASSWORD, password);
        startActivityForResult(intent, VERIFY_ACTIVITY_REQ_CODE);
    }
}
