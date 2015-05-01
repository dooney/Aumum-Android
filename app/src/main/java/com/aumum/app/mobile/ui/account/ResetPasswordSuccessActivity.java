package com.aumum.app.mobile.ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class ResetPasswordSuccessActivity extends ProgressDialogActivity {

    @Inject RestService restService;

    @InjectView(R.id.text_email) protected TextView emailText;
    @InjectView(R.id.b_resend) protected Button resendButton;

    private String email;
    private SafeAsyncTask<Boolean> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_reset_password_success);
        ButterKnife.inject(this);

        progress.setMessageId(R.string.info_sending_reset_password_email);

        email = getIntent().getStringExtra(Constants.Auth.KEY_ACCOUNT_EMAIL);
        emailText.setText(email);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        if (task != null) {
            return;
        }
        showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.resetPassword(email);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                showMsg(R.string.info_reset_password_email_sent);
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
