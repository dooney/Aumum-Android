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

import com.aumum.app.mobile.R;

public class InviteContactsActivity extends ActionBarActivity {

    private boolean showSkip;
    private MobileContactAdapter adapter;

    private static final int CONTACT_LOADER_ID = 78;
    public static final String INTENT_SHOW_SKIP = "showSkip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contacts);

        showSkip = getIntent().getBooleanExtra(INTENT_SHOW_SKIP, false);

        ListView listView = (ListView) findViewById(android.R.id.list);
        adapter = new MobileContactAdapter(this);
        listView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(CONTACT_LOADER_ID,
                new Bundle(), contactsLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showSkip) {
            MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_skip));
            menuItem.setActionView(R.layout.menuitem_button_skip);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            View view = menuItem.getActionView();
            Button skipButton = (Button) view.findViewById(R.id.b_skip);
            skipButton.setOnClickListener(new View.OnClickListener() {
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
                    CursorLoader cursorLoader = new CursorLoader(InviteContactsActivity.this,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            projectionFields,
                            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0",
                            null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                    );
                    return cursorLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    adapter.swapCursor(cursor);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    adapter.swapCursor(null);
                }
            };
}
