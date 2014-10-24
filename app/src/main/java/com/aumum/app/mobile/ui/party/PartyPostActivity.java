package com.aumum.app.mobile.ui.party;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Date;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.Time;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.ProgressDialog;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class PartyPostActivity extends ActionBarActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
                   RadialTimePickerDialog.OnTimeSetListener{

    @Inject
    RestService restService;

    private UserStore userStore;

    @InjectView(R.id.b_date) protected Button dateButton;
    @InjectView(R.id.b_time) protected Button timeButton;
    @InjectView(R.id.b_age) protected Button ageButton;
    @InjectView(R.id.b_gender) protected Button genderButton;
    @InjectView(R.id.et_title) protected EditText titleText;
    @InjectView(R.id.et_location) protected EditText locationText;
    @InjectView(R.id.et_details) protected EditText detailsText;

    protected MenuItem sendButton;

    private final TextWatcher watcher = validationTextWatcher();
    private final ProgressDialog progress = ProgressDialog.newInstance(R.string.info_posting_party);

    private SafeAsyncTask<Boolean> newPartyPostTask;

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker";

    private Date date = new Date();
    private Time time = new Time();
    private int age;
    private int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userStore = UserStore.getInstance(this);

        Injector.inject(this);

        setContentView(R.layout.activity_new_party_post);

        ButterKnife.inject(this);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(PartyPostActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTime now = DateTime.now();
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(PartyPostActivity.this, now.getHourOfDay(), now.getMinuteOfHour(),
                                DateFormat.is24HourFormat(PartyPostActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });

        ageButton.setText(getString(R.string.label_unlimited));
        ageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(PartyPostActivity.this, Constants.AGE_OPTIONS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        age = i;
                        ageButton.setText(Constants.AGE_OPTIONS[i]);
                    }
                });
            }
        });

        genderButton.setText(getString(R.string.label_unlimited));
        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(PartyPostActivity.this, Constants.GENDER_OPTIONS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gender = i;
                        genderButton.setText(Constants.GENDER_OPTIONS[i]);
                    }
                });
            }
        });

        dateButton.addTextChangedListener(watcher);
        timeButton.addTextChangedListener(watcher);
        ageButton.addTextChangedListener(watcher);
        genderButton.addTextChangedListener(watcher);
        titleText.addTextChangedListener(watcher);
        locationText.addTextChangedListener(watcher);
        detailsText.addTextChangedListener(watcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "SEND")
                .setIcon(R.drawable.ic_fa_send_o)
                .setVisible(false)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        sendButton = menu.findItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        handleSend();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        date.setYear(year);
        date.setMonth(monthOfYear + 1);
        date.setDay(dayOfMonth);
        dateButton.setText(date.getDateText());
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        time.setHour(hourOfDay);
        time.setMinute(minute);
        timeButton.setText(time.getTimeString());
    }

    @Override
    public void onResume() {
        super.onResume();

        CalendarDatePickerDialog calendarDatePickerDialog = (CalendarDatePickerDialog) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialog != null) {
            calendarDatePickerDialog.setOnDateSetListener(this);
        }

        RadialTimePickerDialog radialTimePickerDialog = (RadialTimePickerDialog) getSupportFragmentManager().findFragmentByTag(
                FRAG_TAG_TIME_PICKER);
        if (radialTimePickerDialog != null) {
            radialTimePickerDialog.setOnTimeSetListener(this);
        }

        updateUIWithValidation();
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = dateButton.getText().length() > 0 &&
                timeButton.getText().length() > 0 &&
                ageButton.getText().length() > 0 &&
                genderButton.getText().length() > 0 &&
                populated(titleText) &&
                populated(locationText) &&
                populated(detailsText);
        if (sendButton != null) {
            sendButton.setVisible(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private synchronized void showProgress() {
        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
        }
    }

    private synchronized void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            progress.dismissAllowingStateLoss();
        }
    }

    private void finishNewPartyPost() {
        setResult(RESULT_OK);
        finish();
    }

    private void handleSend() {
        if (newPartyPostTask != null) {
            return;
        }

        final Party party = new Party();
        party.setDate(date);
        party.setTime(time);
        party.setAge(age);
        party.setGender(gender);
        party.setTitle(titleText.getText().toString());
        party.setLocation(locationText.getText().toString());
        party.setDetails(detailsText.getText().toString());

        showProgress();

        newPartyPostTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (!party.validate()) {
                    throw new Exception(getString(R.string.error_validate_party));
                }
                User user = userStore.getCurrentUser(false);
                party.setUserId(user.getObjectId());
                party.setArea(user.getArea());
                Party response = restService.newParty(party);
                restService.addPartyMember(response.getObjectId(), user.getObjectId());
                restService.addUserParty(user.getObjectId(), response.getObjectId());
                restService.addUserPartyPost(user.getObjectId(), response.getObjectId());
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(PartyPostActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onNewPartyPostResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                newPartyPostTask = null;
            }
        };
        newPartyPostTask.execute();
    }

    private void onNewPartyPostResult(final boolean result) {
        if (result) {
            finishNewPartyPost();
        } else {
            Toaster.showLong(PartyPostActivity.this,
                    R.string.error_post_new_party);
        }
    }
}
