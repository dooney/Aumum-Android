package com.aumum.app.mobile.ui.contact;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class MobileContactsActivity extends ActionBarActivity
    implements MobileContactAdapter.OnAddContactListener{

    @Inject UserStore userStore;
    @Inject RestService restService;
    @Inject ChatService chatService;

    @InjectView(android.R.id.list ) protected ListView listView;
    @InjectView(R.id.pb_loading) protected ProgressBar progressBar;

    private String userId;
    private boolean showSkip;
    private MobileContactAdapter adapter;
    private User currentUser;
    private HashMap<String, String> contactList;

    private static final int CONTACT_LOADER_ID = 78;
    public static final String INTENT_USER_ID = "userId";
    public static final String INTENT_SHOW_SKIP = "showSkip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_mobile_contacts);
        ButterKnife.inject(this);

        userId = getIntent().getStringExtra(INTENT_USER_ID);
        showSkip = getIntent().getBooleanExtra(INTENT_SHOW_SKIP, false);
        contactList = new HashMap<String, String>();

        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        getSupportLoaderManager().initLoader(CONTACT_LOADER_ID,
                new Bundle(), contactsLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showSkip) {
            MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_enter));
            menuItem.setActionView(R.layout.menuitem_button_enter);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            View view = menuItem.getActionView();
            Button enterButton = (Button) view.findViewById(R.id.b_enter);
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        return true;
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    String[] projectionFields =  new String[] {
                            ContactsContract.CommonDataKinds.Phone._ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    };
                    CursorLoader cursorLoader = new CursorLoader(MobileContactsActivity.this,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            projectionFields,
                            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0",
                            null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                    );
                    return cursorLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
                    new SafeAsyncTask<Boolean>() {
                        public Boolean call() throws Exception {
                            currentUser = userStore.getUserById(userId);
                            getInAppContactList(cursor);
                            return true;
                        }

                        @Override
                        protected void onException(final Exception e) throws RuntimeException {
                            if(!(e instanceof RetrofitError)) {
                                final Throwable cause = e.getCause() != null ? e.getCause() : e;
                                if(cause != null) {
                                    Ln.e(cause.getMessage());
                                }
                            }
                        }

                        @Override
                        protected void onSuccess(Boolean success) throws Exception {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new MobileContactAdapter(MobileContactsActivity.this,
                                            currentUser, contactList, MobileContactsActivity.this);
                                    listView.setAdapter(adapter);
                                    adapter.swapCursor(cursor);
                                }
                            });
                        }

                        @Override
                        protected void onFinally() throws RuntimeException {
                            progressBar.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                    }.execute();
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    if (adapter != null) {
                        adapter.swapCursor(null);
                    }
                }
            };

    private void getInAppContactList(Cursor cursor) {
        ArrayList<String> numberList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (number != null) {
                numberList.add(number.replace(" ", ""));
            }
        }
        contactList.putAll(restService.getInAppContactList(numberList));
    }

    @Override
    public void onAddContact() {
        new EditTextDialog(this, R.layout.dialog_edit_text_multiline, R.string.hint_hello,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        String hello = (String) value;
                        chatService.addContact(userId, hello);
                        Thread.sleep(1000);
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(MobileContactsActivity.this, errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        Toaster.showShort(MobileContactsActivity.this, R.string.info_add_contact_sent);
                    }

                    @Override
                    public void onFailed() {
                        Toaster.showShort(MobileContactsActivity.this, R.string.error_add_contact);
                    }
                }).show();
    }
}
