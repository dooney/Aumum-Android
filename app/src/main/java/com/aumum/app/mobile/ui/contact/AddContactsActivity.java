package com.aumum.app.mobile.ui.contact;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.DialogUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class AddContactsActivity extends ActionBarActivity {

    @Inject ApiKeyProvider apiKeyProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_add_contacts);
        ButterKnife.inject(this);

        Animation.flyIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showActionDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_add_contacts_actions);
        DialogUtils.showDialog(this, options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                startAddMobileContactsActivity();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void startAddMobileContactsActivity() {
        final Intent intent = new Intent(this, MobileContactsActivity.class);
        String userId = apiKeyProvider.getAuthUserId();
        intent.putExtra(MobileContactsActivity.INTENT_USER_ID, userId);
        startActivity(intent);
    }
}
