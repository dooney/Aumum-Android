package com.aumum.app.mobile.ui.group;

import android.graphics.Bitmap;
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
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.GroupDescription;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.easemob.chat.EMGroup;

import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 6/04/2015.
 */
public class NewGroupActivity extends ProgressDialogActivity
    implements TuSdkUtils.AlbumListener,
               TuSdkUtils.EditListener,
               FileUploadService.OnFileUploadListener {

    @Inject UserStore userStore;
    @Inject ChatService chatService;
    @Inject FileUploadService fileUploadService;

    private Button submitButton;
    @InjectView(R.id.image_avatar) protected ImageView avatarImage;
    @InjectView(R.id.et_name) protected EditText nameText;
    @InjectView(R.id.et_description) protected EditText descriptionText;

    private String avatarUrl;
    private String name;
    private String description;

    private final TextWatcher watcher = validationTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_group);
        ButterKnife.inject(this);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TuSdkUtils.album(NewGroupActivity.this, NewGroupActivity.this);
            }
        });
        nameText.addTextChangedListener(watcher);

        fileUploadService.setOnFileUploadListener(this);

        Animation.flyIn(this);
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
        final boolean populated = populated(nameText);
        if (submitButton != null) {
            submitButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void submit() {
        progress.setMessageId(R.string.info_submitting_group);
        showProgress();
        name = nameText.getText().toString();
        EditTextUtils.hideSoftInput(nameText);
        if (descriptionText.getText().length() > 0) {
            description = descriptionText.getText().toString();
        }
        EditTextUtils.hideSoftInput(descriptionText);
        new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User user = userStore.getCurrentUser();
                GroupDescription groupDescription = new GroupDescription(avatarUrl, description);
                EMGroup group = chatService.createGroup(name, groupDescription.getJson(), true);
                chatService.addGroupMember(group.getGroupId(), user.getChatId());
                String groupCreatedText = getString(R.string.label_group_created,
                        user.getScreenName());
                chatService.sendSystemMessage(group.getGroupId(),
                        true, groupCreatedText, null);
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
                setResult(RESULT_OK);
                finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
            }
        }.execute();
    }

    @Override
    public void onUploadSuccess(String remoteUrl) {
        avatarUrl = remoteUrl;
    }

    @Override
    public void onUploadFailure(Exception e) {
        showError(e);
    }

    @Override
    public void onAlbumResult(ImageSqlInfo imageSqlInfo) {
        TuSdkUtils.edit(this, imageSqlInfo, true, true, false, this);
    }

    @Override
    public void onEditResult(File file) {
        try {
            String fileUri = file.getAbsolutePath();
            String avatarUri = ImageLoaderUtils.getFullPath(fileUri);
            Bitmap bitmap = ImageLoaderUtils.loadImage(avatarUri);
            avatarImage.setImageBitmap(bitmap);
            fileUploadService.upload(fileUri, file);
        } catch (Exception e) {
            showError(e);
        }
    }
}
