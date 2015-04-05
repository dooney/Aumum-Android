package com.aumum.app.mobile.ui.party;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.PartyRequest;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 13/03/2015.
 */
public class NewPartyRequestActivity extends ProgressDialogActivity {

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject CreditRuleStore creditRuleStore;

    private Button submitButton;
    @InjectView(R.id.et_area) protected EditText areaText;
    @InjectView(R.id.et_type) protected EditText typeText;
    @InjectView(R.id.et_sub_type) protected EditText subTypeText;

    String areaOptions[];
    private final int AREA_CITY = 0;
    private final int AREA_SUBURB = 1;
    private int area = AREA_CITY;

    String typeOptions[];
    private final int TYPE_FAMILIES = 0;
    private final int TYPE_MUMS = 1;
    private int type = TYPE_FAMILIES;

    private int subType = 0;
    String familySubTypes[];
    private HashMap<Integer, String[]> subTypeMapping;

    private SafeAsyncTask<Boolean> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_party_request);
        ButterKnife.inject(this);

        progress.setMessageId(R.string.info_submitting_party_request);

        areaOptions = getResources().getStringArray(R.array.label_party_request_area);
        areaText.setText(areaOptions[area]);
        areaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAreaOptions();
            }
        });

        typeOptions = getResources().getStringArray(R.array.label_party_request_type);
        typeText.setText(typeOptions[type]);
        typeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeOptions();
            }
        });

        familySubTypes = getResources().getStringArray(R.array.label_party_request_family_sub_type);
        subTypeMapping = new HashMap<Integer, String[]>() {
            {
                put(TYPE_FAMILIES, familySubTypes);
                put(TYPE_MUMS, null);
            }
        };
        subTypeText.setText(subTypeMapping.get(TYPE_FAMILIES)[subType]);
        subTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSubTypeOptions();
            }
        });

        typeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeOptions();
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
        return true;
    }

    private void showAreaOptions() {
        new ListViewDialog(NewPartyRequestActivity.this,
                getString(R.string.label_select_area),
                Arrays.asList(areaOptions),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        area = i;
                        areaText.setText(areaOptions[i]);
                    }
                }).show();
    }

    private void showTypeOptions() {
        new ListViewDialog(NewPartyRequestActivity.this,
                getString(R.string.label_select_type),
                Arrays.asList(typeOptions),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        type = i;
                        typeText.setText(typeOptions[i]);
                        String[] subTypes = subTypeMapping.get(type);
                        if (subTypes != null) {
                            subType = 0;
                            subTypeText.setText(subTypes[subType]);
                            subTypeText.setVisibility(View.VISIBLE);
                        } else {
                            subType = -1;
                            subTypeText.setVisibility(View.GONE);
                        }
                    }
                }).show();
    }

    private void showSubTypeOptions() {
        final String[] subTypes = subTypeMapping.get(type);
        new ListViewDialog(NewPartyRequestActivity.this,
                getString(R.string.label_select_sub_type),
                Arrays.asList(subTypes),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        subType = i;
                        subTypeText.setText(subTypes[i]);
                    }
                }).show();
    }

    private void submit() {
        showProgress();

        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                String city = currentUser.getCity();
                String areaValue = null;
                if (area == AREA_SUBURB) {
                    areaValue = currentUser.getArea();
                }
                String type = typeText.getText().toString();
                String subTypeValue = null;
                if (subType >= 0) {
                    subTypeValue = subTypeText.getText().toString();
                }
                PartyRequest partyRequest = new PartyRequest(currentUser.getObjectId(),
                        city, areaValue, type, subTypeValue);
                restService.newPartyRequest(partyRequest);
                updateCredit(currentUser, CreditRule.ADD_PARTY_REQUEST);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(NewPartyRequestActivity.this, cause.getMessage());
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
                    Toaster.showShort(NewPartyRequestActivity.this, getString(R.string.info_got_credit,
                            creditRule.getDescription(), credit));
                }
            });
        }
    }
}
