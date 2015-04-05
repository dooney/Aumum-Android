package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 2/03/2015.
 */
public class NewMomentActivity extends ProgressDialogActivity
        implements FileUploadService.OnFileUploadListener {

    @Inject UserStore userStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject RestService restService;
    @Inject FileUploadService fileUploadService;
    @Inject ChatService chatService;

    private SafeAsyncTask<Boolean> task;
    private GalleryAdapter adapter;
    private String imagePathList[];
    private ArrayList<String> imageUrlList;

    private Button submitButton;
    @InjectView(R.id.v_scroll) protected ScrollView scrollView;
    @InjectView(R.id.et_details) protected EditText detailsText;
    @InjectView(R.id.layout_add_more) protected View addMoreLayout;
    @InjectView(R.id.layout_type_selection) protected ViewGroup typeSelectionLayout;
    @InjectView(R.id.layout_image) protected ViewGroup imageLayout;
    @InjectView(R.id.grid_gallery) protected GridView gridGallery;

    private final TextWatcher watcher = validationTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_moment);
        ButterKnife.inject(this);

        imageUrlList = new ArrayList<String>();
        fileUploadService.setOnFileUploadListener(this);

        progress.setMessageId(R.string.info_submitting_moment);

        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        detailsText.addTextChangedListener(watcher);
        addMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTypeSelectionLayout();
            }
        });
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(NewMomentActivity.this, ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.INTENT_ACTION, ImagePickerActivity.ACTION_MULTIPLE_PICK);
                startActivityForResult(intent, Constants.RequestCode.IMAGE_PICKER_REQ_CODE);
            }
        });
        adapter = new GalleryAdapter(this, R.layout.image_collection_listitem_inner);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String imageUrl = adapter.getItem(position).getUri();
                final Intent intent = new Intent(NewMomentActivity.this, ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
                startActivity(intent);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.IMAGE_PICKER_REQ_CODE) {
            toggleTypeSelectionLayout();
            if (resultCode == Activity.RESULT_OK) {
                imagePathList = data.getStringArrayExtra(ImagePickerActivity.INTENT_ALL_PATH);
                if (imagePathList != null) {
                    ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
                    for (String path : imagePathList) {
                        CustomGallery item = new CustomGallery();
                        item.type = CustomGallery.FILE;
                        item.imageUri = path;
                        list.add(item);
                    }
                    adapter.addAll(list);
                }
            }
        }
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

        imageUrlList.clear();
        if (imagePathList != null) {
            for (String path : imagePathList) {
                uploadImage(path);
            }
        } else {
            submitNewMoment();
        }
    }

    private void uploadImage(final String imagePath) {
        new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                Bitmap bitmap = ImageLoaderUtils.loadImage("file://" + imagePath);
                if (bitmap == null) {
                    throw new Exception(getString(R.string.error_invalid_image_file, imagePath));
                }
                byte data[] = ImageUtils.getBytesBitmap(bitmap);
                fileUploadService.upload(imagePath, data);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(NewMomentActivity.this, cause.getMessage());
                    }
                }
                hideProgress();
            }
        }.execute();
    }

    private void submitNewMoment() {
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
                updateCredit(currentUser, CreditRule.ADD_MOMENT);
                notifyContacts(currentUser, moment);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(NewMomentActivity.this, cause.getMessage());
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

    private void toggleTypeSelectionLayout() {
        if (typeSelectionLayout.getVisibility() == View.GONE) {
            Animation.flyIn(typeSelectionLayout);
        } else {
            Animation.flyOut(typeSelectionLayout);
        }
    }

    @Override
    public void onUploadSuccess(String fileUrl) {
        imageUrlList.add(fileUrl);
        if (imageUrlList.size() == imagePathList.length) {
            submitNewMoment();
        }
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        final Throwable cause = e.getCause() != null ? e.getCause() : e;
        if(cause != null) {
            Toaster.showShort(NewMomentActivity.this, cause.getMessage());
        }
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
                    Toaster.showShort(NewMomentActivity.this, getString(R.string.info_got_credit,
                            creditRule.getDescription(), credit));
                }
            });
        }
    }
}