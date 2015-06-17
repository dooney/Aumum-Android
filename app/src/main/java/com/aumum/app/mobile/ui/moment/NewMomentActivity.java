package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 4/05/2015.
 */
public class NewMomentActivity extends ProgressDialogActivity
    implements FileUploadService.FileUploadListener {

    @Inject RestService restService;
    @Inject FileUploadService fileUploadService;
    @Inject UserStore userStore;

    public static final String INTENT_IMAGE_URI = "imageUri";
    private String imageUri;

    private View publishButton;
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
        imageUri = intent.getStringExtra(INTENT_IMAGE_URI);
        ImageLoaderUtils.displayImage(ImageLoaderUtils.getFullPath(imageUri), image);
        text.addTextChangedListener(watcher);

        progress.setMessageId(R.string.info_publishing_moment);
        fileUploadService.setFileUploadListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_publish);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        publishButton = view.findViewById(R.id.b_publish);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                try {
                    fileUploadService.upload(imageUri);
                } catch (Exception e) {
                    showError(e);
                }
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

    private void publish(final String imageUrl) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                Moment moment = new Moment(currentUser.getObjectId(),
                        text.getText().toString(), imageUrl);
                moment = restService.newMoment(moment);
                restService.addUserMoment(currentUser.getObjectId(),
                        moment.getObjectId());
                currentUser.addMoment(moment.getObjectId());
                userStore.save(currentUser);
                notifyContacts(currentUser.getContacts());
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
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onUploadSuccess(String remoteUrl) {
        publish(remoteUrl);
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        showError(e);
    }

    private void notifyContacts(final List<String> users) {
        if (users.size() > 0) {
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    for (String userId: users) {
                        UserInfo user = userStore.getUserInfoById(userId);
                        CmdMessage cmdMessage = new CmdMessage(
                                CmdMessage.Type.NEW_MOMENT, null, null, null);
                        EMChatUtils.sendCmdMessage(user.getChatId(), cmdMessage);
                    }
                    return true;
                }
            }.execute();
        }
    }
}
