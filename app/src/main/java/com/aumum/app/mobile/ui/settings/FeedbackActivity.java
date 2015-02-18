package com.aumum.app.mobile.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Feedback;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 13/01/2015.
 */
public class FeedbackActivity extends ProgressDialogActivity {

    @Inject RestService restService;
    @Inject ApiKeyProvider apiKeyProvider;

    private Button submitButton;
    @InjectView(R.id.et_details) protected EditText detailsText;

    private final TextWatcher watcher = validationTextWatcher();
    private SafeAsyncTask<Boolean> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);

        progress.setMessageId(R.string.info_submitting_feedback);

        detailsText.addTextChangedListener(watcher);
        EditTextUtils.showSoftInput(detailsText, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_submit);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        submitButton = (Button) view.findViewById(R.id.b_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        updateUIWithValidation();
        return true;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        if (submitButton != null) {
            submitButton.setEnabled(detailsText.length() > 0);
        }
    }

    private void submit() {
        if (task != null) {
            return;
        }
        showProgress();
        task = new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String details = detailsText.getText().toString();
                String currentUserId = apiKeyProvider.getAuthUserId();
                Feedback feedback = new Feedback(details, currentUserId);
                restService.newFeedback(feedback);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(FeedbackActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                Toaster.showShort(FeedbackActivity.this, R.string.info_feedback_submitted);
                finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
            }
        };
        task.execute();
    }
}
