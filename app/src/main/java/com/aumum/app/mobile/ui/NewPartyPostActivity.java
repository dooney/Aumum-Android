package com.aumum.app.mobile.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.aumum.app.mobile.core.BootstrapService;
import com.aumum.app.mobile.core.Date;
import com.aumum.app.mobile.core.Party;
import com.aumum.app.mobile.core.Time;
import com.aumum.app.mobile.util.SafeAsyncTask;
import com.aumum.app.mobile.util.UIUtils;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;
import retrofit.RetrofitError;

public class NewPartyPostActivity extends ActionBarActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
                   RadialTimePickerDialog.OnTimeSetListener{

    @Inject BootstrapService bootstrapService;

    @InjectView(R.id.b_date) protected Button dateButton;
    @InjectView(R.id.b_time) protected Button timeButton;
    @InjectView(R.id.b_age) protected Button ageButton;
    @InjectView(R.id.b_gender) protected Button genderButton;
    @InjectView(R.id.et_title) protected EditText titleText;
    @InjectView(R.id.et_location) protected EditText locationText;
    @InjectView(R.id.et_details) protected EditText detailsText;

    protected MenuItem sendButton;

    private final TextWatcher watcher = validationTextWatcher();

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

        Injector.inject(this);

        setContentView(R.layout.activity_new_party_post);

        Views.inject(this);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(NewPartyPostActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTime now = DateTime.now();
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(NewPartyPostActivity.this, now.getHourOfDay(), now.getMinuteOfHour(),
                                DateFormat.is24HourFormat(NewPartyPostActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });

        ageButton.setText(getString(R.string.label_unlimited));
        ageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence options[] = {
                        getString(R.string.label_unlimited),
                        getString(R.string.label_pre_pregnant),
                        getString(R.string.label_pregnant),
                        getString(R.string.label_age_0_1),
                        getString(R.string.label_age_1_3),
                        getString(R.string.label_age_3_6),
                        getString(R.string.label_age_6_),
                };
                UIUtils.showAlert(NewPartyPostActivity.this, R.string.label_age, options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        age = i;
                        ageButton.setText(options[i]);
                    }
                });
            }
        });

        genderButton.setText(getString(R.string.label_unlimited));
        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence options[] = {
                        getString(R.string.label_unlimited),
                        getString(R.string.label_girl_only),
                        getString(R.string.label_boy_only)
                };
                UIUtils.showAlert(NewPartyPostActivity.this, R.string.label_gender, options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gender = i;
                        genderButton.setText(options[i]);
                    }
                });
            }
        });

        titleText.addTextChangedListener(watcher);
        locationText.addTextChangedListener(watcher);
        detailsText.addTextChangedListener(watcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_party_post, menu);
        sendButton = menu.findItem(R.id.b_send_post);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.b_send_post) {
            handleSend();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        date.setYear(year);
        date.setMonth(monthOfYear + 1);
        date.setDay(dayOfMonth);
        dateButton.setText(date.getDateString());
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
        final boolean populated = populated(titleText) && populated(locationText) && populated(detailsText);
        if (sendButton != null) {
            sendButton.setVisible(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.message_posting));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                if (newPartyPostTask != null) {
                    newPartyPostTask.cancel(true);
                }
            }
        });
        return dialog;
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    private void finishNewPartyPost() {
        final Intent intent = new Intent();
        setResult(RESULT_OK, intent);
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
                    throw new Exception(getString(R.string.message_model_validation_failed));
                }
                bootstrapService.newParty(party);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(NewPartyPostActivity.this, cause.getMessage());
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
            Toaster.showLong(NewPartyPostActivity.this,
                    R.string.message_post_failed_new_party);
        }
    }
}
