package com.aumum.app.mobile.ui.register;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.Ln;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VerifyActivity extends ActionBarActivity {

    public static final String INTENT_COUNTRY_CODE = "countryCode";
    public static final String INTENT_PHONE = "phone";
    public static final String INTENT_PASSWORD = "password";

    private final long RETRY_INTERVAL = 60;
    private final long INTERVAL = 1;
    private long total = RETRY_INTERVAL;
    private String countryCode;
    private String phone;
    private String password;

    private final TextWatcher watcher = validationTextWatcher();

    @InjectView(R.id.text_phone) protected TextView phoneText;
    @InjectView(R.id.et_verification_code) protected EditText verificationCodeText;
    @InjectView(R.id.b_confirm) protected Button confirmButton;
    @InjectView(R.id.b_resend) protected Button resendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        countryCode = intent.getStringExtra(INTENT_COUNTRY_CODE);
        phone = intent.getStringExtra(INTENT_PHONE);
        password = intent.getStringExtra(INTENT_PASSWORD);

        phoneText.setText(getString(R.string.label_your_phone, countryCode, phone));
        verificationCodeText.addTextChangedListener(watcher);
        String text = getString(R.string.info_will_receive_sms_within, total);
        resendButton.setText(Html.fromHtml(text));
        resendButton.setEnabled(false);

        countDown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
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
                                    String text = getString(R.string.info_will_receive_sms_within, total);
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
}
