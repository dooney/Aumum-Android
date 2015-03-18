package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ShareMomentActivity extends ProgressDialogActivity {

    @Inject UserStore userStore;
    @Inject RestService restService;
    @Inject ChatService chatService;

    private SafeAsyncTask<Boolean> task;
    private GalleryAdapter adapter;
    private ArrayList<String> imageUrlList;

    private Button submitButton;
    @InjectView(R.id.v_scroll) protected ScrollView scrollView;
    @InjectView(R.id.et_details) protected EditText detailsText;
    @InjectView(R.id.layout_add_more) protected View addMoreLayout;
    @InjectView(R.id.grid_gallery) protected GridView gridGallery;

    private final TextWatcher watcher = validationTextWatcher();

    public static final String INTENT_MOMENT_TEXT = "text";
    public static final String INTENT_MOMENT_IMAGES = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_moment);
        ButterKnife.inject(this);

        final Intent intent = getIntent();

        progress.setMessageId(R.string.info_submitting_moment);

        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        String text = intent.getStringExtra(INTENT_MOMENT_TEXT);
        detailsText.addTextChangedListener(watcher);
        detailsText.setText(text);

        addMoreLayout.setVisibility(View.GONE);

        adapter = new GalleryAdapter(this, R.layout.image_collection_listitem_inner);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String imageUrl = adapter.getItem(position).getUri();
                final Intent intent = new Intent(ShareMomentActivity.this, ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
                startActivity(intent);
            }
        });
        imageUrlList = intent.getStringArrayListExtra(INTENT_MOMENT_IMAGES);
        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String url : imageUrlList) {
            CustomGallery item = new CustomGallery();
            item.type = CustomGallery.HTTP;
            item.imageUri = url;
            list.add(item);
        }
        adapter.addAll(list);

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
        final boolean populated = populated(detailsText);
        if (submitButton != null) {
            submitButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void submit() {
        EditTextUtils.hideSoftInput(detailsText);
        showProgress();

        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                Moment moment = new Moment(
                        currentUser.getObjectId(),
                        detailsText.getText().toString(),
                        imageUrlList);
                Moment response = restService.newMoment(moment);
                moment.setObjectId(response.getObjectId());
                restService.addUserMoment(currentUser.getObjectId(), moment.getObjectId());
                currentUser.addMoment(moment.getObjectId());
                userStore.save(currentUser);
                notifyContacts(currentUser, moment);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(ShareMomentActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                setResult(RESULT_OK);
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

    private void notifyContacts(final User currentUser,
                                final Moment moment) throws Exception {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String title = getString(R.string.label_new_moment_message, currentUser.getScreenName());
                for (String userId : currentUser.getContacts()) {
                    CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.MOMENT_NEW,
                            title, moment.getDetails(), moment.getObjectId());
                    chatService.sendCmdMessage(userId.toLowerCase(), cmdMessage, false, null);
                }
                return true;
            }
        }.execute();
    }
}
