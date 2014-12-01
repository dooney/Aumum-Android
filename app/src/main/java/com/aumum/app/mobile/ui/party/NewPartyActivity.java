package com.aumum.app.mobile.ui.party;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Date;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.Time;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.GooglePlaceUtils;
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

public class NewPartyActivity extends ProgressDialogActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
                   RadialTimePickerDialog.OnTimeSetListener {

    @Inject RestService restService;
    @Inject MessageDeliveryService messageDeliveryService;
    @Inject UserStore userStore;

    private Date date = new Date();
    private Time time = new Time();
    private int age;
    private int gender;

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker";

    private Button submitButton;
    @InjectView(R.id.v_scroll) protected ScrollView scrollView;
    @InjectView(R.id.b_date) protected TextView dateButton;
    @InjectView(R.id.b_time) protected TextView timeButton;
    @InjectView(R.id.b_age) protected TextView ageButton;
    @InjectView(R.id.b_gender) protected TextView genderButton;
    @InjectView(R.id.et_title) protected EditText titleText;
    @InjectView(R.id.et_location) protected AutoCompleteTextView locationText;
    @InjectView(R.id.et_details) protected EditText detailsText;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_party);
        ButterKnife.inject(this);

        progress.setMessageId(R.string.info_submitting_party);

        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(NewPartyActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTime now = DateTime.now();
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(NewPartyActivity.this, now.getHourOfDay(), now.getMinuteOfHour(),
                                DateFormat.is24HourFormat(NewPartyActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });

        ageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(NewPartyActivity.this, Constants.Options.AGE_OPTIONS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        age = i;
                        ageButton.setText(Constants.Options.AGE_OPTIONS[i]);
                        ageButton.setTextColor(getResources().getColor(R.color.black));
                    }
                });
            }
        });

        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(NewPartyActivity.this, Constants.Options.GENDER_OPTIONS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gender = i;
                        genderButton.setText(Constants.Options.GENDER_OPTIONS[i]);
                        genderButton.setTextColor(getResources().getColor(R.color.black));
                    }
                });
            }
        });

        titleText.addTextChangedListener(watcher);

        locationText.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.place_autocomplete_listitem));
        locationText.addTextChangedListener(watcher);

        detailsText.addTextChangedListener(watcher);

        Animation.flyIn(this);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        date.setYear(year);
        date.setMonth(monthOfYear + 1);
        date.setDay(dayOfMonth);
        dateButton.setText(date.getDateText());
        dateButton.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        time.setHour(hourOfDay);
        time.setMinute(minute);
        timeButton.setText(time.getTimeText());
        timeButton.setTextColor(getResources().getColor(R.color.black));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_submit));
        menuItem.setActionView(R.layout.menuitem_button_submit);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        submitButton = (Button) view.findViewById(R.id.b_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitNewParty();
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
                                  populated(locationText) &&
                                  populated(detailsText) &&
                                  dateButton.getText().length() > 0 &&
                                  timeButton.getText().length() > 0 &&
                                  ageButton.getText().length() > 0 &&
                                  genderButton.getText().length() > 0;
        submitButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void submitNewParty() {
        if (task != null) {
            return;
        }

        showProgress();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User user = userStore.getCurrentUser();
                Party party = new Party(user.getObjectId(),
                                        titleText.getText().toString(),
                                        date,
                                        time,
                                        age,
                                        gender,
                                        locationText.getText().toString(),
                                        detailsText.getText().toString());
                if (!GooglePlaceUtils.setPlaceLatLong(party.getPlace())) {
                    throw new Exception(getString(R.string.error_validate_party_location, party.getPlace().getLocation()));
                }
                Party response = restService.newParty(party);
                restService.addPartyMember(response.getObjectId(), user.getObjectId());
                restService.addUserParty(user.getObjectId(), response.getObjectId());
                for (String userId: user.getContacts()) {
                    String content = getString(R.string.label_new_party_message, party.getTitle());
                    Message message = new Message(Message.Type.PARTY_NEW,
                            user.getObjectId(), userId, content, party.getObjectId());
                    messageDeliveryService.send(message);
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(NewPartyActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toaster.showShort(NewPartyActivity.this, R.string.error_submit_new_party);
                }
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
