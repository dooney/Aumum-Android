package com.aumum.app.mobile.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.Strings;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import retrofit.RetrofitError;

public class VerifyActivity extends ProgressDialogActivity {

    @Inject RestService restService;
    @Inject ChatService chatService;

    public static final String INTENT_COUNTRY_CODE = "countryCode";
    public static final String INTENT_PHONE = "phone";
    public static final String INTENT_USER_ID = "userId";
    public static final String INTENT_PASSWORD = "password";
    public static final String INTENT_TOKEN = "token";

    private final int COMPLETE_ACTIVITY_REQ_CODE = 100;

    private final long RETRY_INTERVAL = 60;
    private final long INTERVAL = 1;
    private long total = RETRY_INTERVAL;
    private String countryCode;
    private String phone;
    private String verificationCode;
    private String userId;
    private String password;
    private String token;

    private EventHandler handler;
    private SafeAsyncTask<Boolean> task;
    private final TextWatcher watcher = validationTextWatcher();

    @InjectView(R.id.text_label_phone) protected TextView phoneLabelText;
    @InjectView(R.id.text_label_code_tip) protected TextView codeTipLabelText;
    @InjectView(R.id.et_verification_code) protected EditText verificationCodeText;
    @InjectView(R.id.b_confirm) protected Button confirmButton;
    @InjectView(R.id.b_resend) protected Button resendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_verify);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        countryCode = intent.getStringExtra(INTENT_COUNTRY_CODE);
        phone = intent.getStringExtra(INTENT_PHONE);
        password = intent.getStringExtra(INTENT_PASSWORD);

        String phoneLabelHtmlText = getString(R.string.label_your_phone, countryCode, phone);
        phoneLabelText.setText(Html.fromHtml(phoneLabelHtmlText));
        String codeTipLabelHtmlText = getString(R.string.label_will_receive_verification_code);
        codeTipLabelText.setText(Html.fromHtml(codeTipLabelHtmlText));
        verificationCodeText.addTextChangedListener(watcher);
        String text = getString(R.string.label_will_receive_sms_within, total);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditTextUtils.hideSoftInput(verificationCodeText);
                progress.setMessageId(R.string.info_submitting_registration);
                showProgress();
                verificationCode = verificationCodeText.getText().toString();
                SMSSDK.submitVerificationCode(Strings.removeLeadingZeros(countryCode), phone, verificationCode);
            }
        });
        resendButton.setText(Html.fromHtml(text));
        resendButton.setEnabled(false);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setMessageId(R.string.info_sending_verification_sms);
                showProgress();
                SMSSDK.getVerificationCode(Strings.removeLeadingZeros(countryCode), phone);
            }
        });

        handler = new EventHandler() {
            public void afterEvent(final int event, final int result, final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                register();
                            } else {
                                hideProgress();
                                Toaster.showShort(VerifyActivity.this,
                                        R.string.error_verify_code);
                            }
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            hideProgress();
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                Toaster.showShort(VerifyActivity.this,
                                        R.string.info_verification_sms_sent);
                                total = RETRY_INTERVAL;
                                countDown();
                            } else {
                                Toaster.showShort(VerifyActivity.this,
                                        R.string.error_send_verification_sms);
                            }
                        }
                    }
                });
            }
        };

        countDown();
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

        if (requestCode == COMPLETE_ACTIVITY_REQ_CODE && resultCode == RESULT_OK) {
            final Intent intent = new Intent();
            intent.putExtra(INTENT_USER_ID, userId);
            intent.putExtra(INTENT_TOKEN, token);
            setResult(RESULT_OK, intent);
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
        final boolean populated = populated(verificationCodeText);
        confirmButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void countDown() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (total > 0) {
                        long interval = INTERVAL * 1000;
                        Thread.sleep(interval);
                        total -= INTERVAL;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (total == 0) {
                                    resendButton.setText(R.string.label_resend_verification_code);
                                    resendButton.setEnabled(true);
                                } else {
                                    String text = getString(R.string.label_will_receive_sms_within, total);
                                    resendButton.setText(Html.fromHtml(text));
                                    resendButton.setEnabled(false);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Ln.e(e);
                }
            }
        }.start();
    }

    private void register() {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String mobile = countryCode + phone;
                User response = restService.register(mobile, password);
                userId = response.getObjectId();
                token = response.getSessionToken();
                String chatId = userId.toLowerCase();
                restService.updateUserChatId(response.getObjectId(), chatId);
                chatService.createAccount(chatId, password);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(VerifyActivity.this, cause.getMessage());
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

    private void onRegistrationResult(final Boolean result) {
        if (result) {
            startCompleteProfileActivity(userId);
        } else {
            Toaster.showShort(VerifyActivity.this, R.string.error_registration);
        }
    }

    private void startCompleteProfileActivity(String userId) {
        final Intent intent = new Intent(this, CompleteProfileActivity.class);
        intent.putExtra(CompleteProfileActivity.INTENT_USER_ID, userId);
        startActivityForResult(intent, COMPLETE_ACTIVITY_REQ_CODE);
    }
}