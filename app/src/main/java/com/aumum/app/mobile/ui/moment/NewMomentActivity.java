package com.aumum.app.mobile.ui.moment;

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
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 4/05/2015.
 */
public class NewMomentActivity extends ProgressDialogActivity {

    @Inject RestService restService;
    @Inject CreditRuleStore creditRuleStore;
    @Inject UserStore userStore;

    public static final String INTENT_IMAGE_URI = "imageUri";

    private Button publishButton;
    @InjectView(R.id.image) protected ImageView image;
    @InjectView(R.id.et_text) protected EditText text;

    private final TextWatcher watcher = validationTextWatcher();
    private SafeAsyncTask<Boolean> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_moment);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        String imageUri = intent.getStringExtra(INTENT_IMAGE_URI);
        ImageLoaderUtils.displayImage(ImageLoaderUtils.getFullPath(imageUri), image);
        text.addTextChangedListener(watcher);

        progress.setMessageId(R.string.info_publishing_moment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_publish);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        publishButton = (Button) view.findViewById(R.id.b_publish);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publish();
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
        if (publishButton != null) {
            publishButton.setEnabled(text.length() > 0);
        }
    }

    private void publish() {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                updateCredit(currentUser, CreditRule.ADD_MOMENT);
                return true;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                setResult(Constants.RequestCode.NEW_MOMENT_REQ_CODE);
                finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    private void updateCredit(User currentUser, int seq) throws Exception {
        final CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            final int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            userStore.save(currentUser);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMsg(getString(R.string.info_got_credit,
                            creditRule.getDescription(), credit));
                }
            });
        }
    }
}
