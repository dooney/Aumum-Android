package com.aumum.app.mobile.ui.report;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Report;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class ReportActivity extends ProgressDialogActivity {

    @Inject RestService restService;
    @Inject ApiKeyProvider apiKeyProvider;

    private String entityType;
    private String entityId;

    private View submitButton;
    @InjectView(R.id.et_type) protected EditText typeText;
    @InjectView(R.id.et_details) protected EditText detailsText;

    private final TextWatcher watcher = validationTextWatcher();
    private SafeAsyncTask<Boolean> task;

    public final static String TYPE_USER = "用户";
    public final static String TYPE_GROUP = "群组";
    public final static String INTENT_ENTITY_TYPE = "entityType";
    public final static String INTENT_ENTITY_ID = "entityId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_report);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        entityType = intent.getStringExtra(INTENT_ENTITY_TYPE);
        entityId = intent.getStringExtra(INTENT_ENTITY_ID);

        progress.setMessageId(R.string.info_submitting_report);

        typeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReportTypeOptions();
            }
        });
        typeText.addTextChangedListener(watcher);
        detailsText.addTextChangedListener(watcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_submit);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        submitButton = view.findViewById(R.id.b_submit);
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
        final boolean populated = typeText.getText().length() > 0 &&
                populated(detailsText);
        if (submitButton != null) {
            submitButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void showReportTypeOptions() {
        final String options[] = getResources().getStringArray(R.array.label_report_types);
        new ListViewDialog(this,
                getString(R.string.label_select_report_type),
                Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                typeText.setText(options[i]);
            }
        }).show();
    }

    private void submit() {
        if (task != null) {
            return;
        }
        showProgress();
        task = new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String type = typeText.getText().toString();
                String details = detailsText.getText().toString();
                String currentUserId = apiKeyProvider.getAuthUserId();
                Report report = new Report(entityType, entityId, type, details, currentUserId);
                restService.newReport(report);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                showMsg(R.string.info_report_submitted);
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
