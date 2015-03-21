package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Date;
import com.aumum.app.mobile.core.model.Event;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.Place;
import com.aumum.app.mobile.core.model.Time;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.github.kevinsawicki.wishlist.Toaster;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Created by Administrator on 22/03/2015.
 */
public class SharePartyActivity extends ProgressDialogActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener {

    @Inject RestService restService;
    @Inject UserStore userStore;

    private Date date;
    private Time time;
    private String address;
    private Place place;
    private SafeAsyncTask<Boolean> task;

    public static final String INTENT_PAYLOAD = "payload";

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker";

    private Button submitButton;
    @InjectView(R.id.v_scroll) protected ScrollView scrollView;
    @InjectView(R.id.et_date) protected EditText dateText;
    @InjectView(R.id.et_time) protected EditText timeText;
    @InjectView(R.id.et_title) protected EditText titleText;
    @InjectView(R.id.et_address) protected EditText addressText;
    @InjectView(R.id.et_location_description) protected EditText locationDescriptionText;
    @InjectView(R.id.et_details) protected EditText detailsText;

    private final TextWatcher watcher = validationTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_share_party);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        Gson gson = new Gson();
        String payload = intent.getStringExtra(INTENT_PAYLOAD);
        Event event = gson.fromJson(payload, new TypeToken<Event>(){}.getType());
        date = event.getDate();
        time = event.getTime();
        address = event.getAddress();
        place = new Place(address);
        place.setLatitude(event.getLatitude());
        place.setLongitude(event.getLongitude());

        progress.setMessageId(R.string.info_submitting_party);

        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        dateText.setText(date.getDateText());
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(SharePartyActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });
        dateText.addTextChangedListener(watcher);
        timeText.setText(time.getTimeText());
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTime now = DateTime.now();
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(SharePartyActivity.this, now.getHourOfDay(), now.getMinuteOfHour(),
                                DateFormat.is24HourFormat(SharePartyActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });
        timeText.addTextChangedListener(watcher);
        titleText.setText(event.getName());
        titleText.addTextChangedListener(watcher);
        addressText.setText(event.getAddress());
        detailsText.setText(event.getDetails());

        Animation.flyIn(this);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog,
                          int year,
                          int monthOfYear,
                          int dayOfMonth) {
        date.setYear(year);
        date.setMonth(monthOfYear + 1);
        date.setDay(dayOfMonth);
        dateText.setText(date.getDateText());
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog,
                          int hourOfDay,
                          int minute) {
        time.setHour(hourOfDay);
        time.setMinute(minute);
        timeText.setText(time.getTimeText());
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
        final boolean populated = populated(titleText) &&
                dateText.getText().length() > 0 &&
                timeText.getText().length() > 0;
        if (submitButton != null) {
            submitButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void submit() {
        EditTextUtils.hideSoftInput(titleText);
        EditTextUtils.hideSoftInput(locationDescriptionText);
        EditTextUtils.hideSoftInput(detailsText);
        showProgress();

        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                final User currentUser = userStore.getCurrentUser();
                final Party party = new Party(currentUser.getObjectId(),
                        titleText.getText().toString(),
                        date,
                        time,
                        place,
                        locationDescriptionText.getText().toString(),
                        detailsText.getText().toString(),
                        null,
                        null);
                Party response = restService.newParty(party);
                party.setObjectId(response.getObjectId());
                restService.joinParty(party.getObjectId(), currentUser.getObjectId(), null);
                currentUser.addParty(party.getObjectId());
                userStore.save(currentUser);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(SharePartyActivity.this, cause.getMessage());
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
}
