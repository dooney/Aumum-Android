package com.aumum.app.mobile.ui.report;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Report;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class ReportActivity extends ProgressDialogActivity {

    @Inject RestService restService;

    private String entityType;
    private String entityId;

    private Button submitButton;
    @InjectView(R.id.et_type) protected EditText typeText;
    @InjectView(R.id.et_details) protected EditText detailsText;

    private final TextWatcher watcher = validationTextWatcher();
    private SafeAsyncTask<Boolean> task;

    public final static String TYPE_PARTY = "聚会";
    public final static String TYPE_PARTY_COMMENT = "聚会评论";
    public final static String TYPE_ASKING = "说说";
    public final static String TYPE_ASKING_REPLY = "说说回复";
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
        detailsText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && submitButton.isEnabled()) {
                    submit();
                    return true;
                }
                return false;
            }
        });
        detailsText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && submitButton.isEnabled()) {
                    submit();
                    return true;
                }
                return false;
            }
        });
        detailsText.addTextChangedListener(watcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_submit));
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
        final boolean populated = populated(detailsText) &&
                typeText.getText().length() > 0;
        submitButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void showReportTypeOptions() {
        final String options[] = getResources().getStringArray(R.array.label_report_types);
        DialogUtils.showDialog(this, options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        typeText.setText(options[i]);
                    }
                });
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
                Report report = new Report(entityType, entityId, type, details);
                restService.newReport(report);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(ReportActivity.this, R.string.error_submit_report);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                Toaster.showShort(ReportActivity.this, R.string.info_report_submitted);
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
