package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.google.gson.Gson;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class NewMomentActivity extends ProgressDialogActivity {

    @Inject UserStore userStore;
    @Inject RestService restService;
    @Inject MessageDeliveryService messageDeliveryService;

    private Party party;

    @InjectView(R.id.edit_moment_text) protected EditText momentText;

    private SafeAsyncTask<Boolean> task;

    public final static String INTENT_PARTY = "party";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_moment);
        ButterKnife.inject(this);
        progress.setMessageId(R.string.info_posting_moment);
        final Intent intent = getIntent();
        Gson gson = new Gson();
        party = gson.fromJson(intent.getStringExtra(INTENT_PARTY), Party.class);

        Animation.flyIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_submit_new_party))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        submitNewMoment();
        return super.onOptionsItemSelected(item);
    }

    private void submitNewMoment() {
        if (task != null) {
            return;
        }

        final Moment moment = new Moment();
        moment.setText(momentText.getText().toString());

        showProgress();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                moment.setUserId(currentUser.getObjectId());
                Moment response = restService.newMoment(moment);
                restService.addUserMomentPost(currentUser.getObjectId(), response.getObjectId());
                restService.addPartyMoment(party.getObjectId(), response.getObjectId());
                for(String userId: party.getMembers()) {
                    restService.addUserMoment(userId, response.getObjectId());
                }
                Message message = new Message(Message.Type.PARTY_CHECK_IN,
                        currentUser.getObjectId(), party.getUserId(), null, party.getObjectId(), party.getTitle());
                messageDeliveryService.send(message);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(NewMomentActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toaster.showLong(NewMomentActivity.this, R.string.error_post_new_moment);
                }
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
