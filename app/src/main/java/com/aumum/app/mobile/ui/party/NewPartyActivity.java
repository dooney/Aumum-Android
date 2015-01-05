package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Date;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.Place;
import com.aumum.app.mobile.core.model.Time;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.contact.ContactPickerActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.GooglePlaceUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.easemob.chat.EMGroup;
import com.github.kevinsawicki.wishlist.Toaster;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class NewPartyActivity extends ProgressDialogActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
                   RadialTimePickerDialog.OnTimeSetListener,
                   FileUploadService.OnFileUploadListener {

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject FileUploadService fileUploadService;
    @Inject ChatService chatService;

    private Date date = new Date();
    private Time time = new Time();

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker";

    private Button submitButton;
    @InjectView(R.id.v_scroll) protected ScrollView scrollView;
    @InjectView(R.id.et_date) protected EditText dateText;
    @InjectView(R.id.et_time) protected EditText timeText;
    @InjectView(R.id.et_title) protected EditText titleText;
    @InjectView(R.id.et_address) protected AutoCompleteTextView addressText;
    @InjectView(R.id.et_location_description) protected EditText locationDescriptionText;
    @InjectView(R.id.et_details) protected EditText detailsText;
    @InjectView(R.id.layout_privacy) protected ViewGroup privacyLayout;
    @InjectView(R.id.text_privacy) protected TextView privacyText;
    @InjectView(R.id.layout_group) protected ViewGroup groupLayout;
    @InjectView(R.id.text_group) protected TextView groupText;
    @InjectView(R.id.layout_notify) protected ViewGroup notifyLayout;
    @InjectView(R.id.text_add_more) protected TextView addMoreText;
    @InjectView(R.id.layout_type_selection) protected ViewGroup typeSelectionLayout;
    @InjectView(R.id.layout_image) protected ViewGroup imageLayout;
    @InjectView(R.id.grid_gallery) protected GridView gridGallery;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> task;
    private GalleryAdapter adapter;
    private String imagePathList[];
    private ArrayList<String> imageUrlList;

    private int groupType;
    private int privacyType;
    private final int PRIVACY_TYPE_PUBLIC = 0;
    private final int PRIVACY_TYPE_CONTACTS = 1;
    private final int PRIVACY_TYPE_SPECIFIED_CONTACTS = 2;
    private ArrayList<String> specifiedContacts;
    private ArrayList<String> notifiedContacts;

    private final int SPECIFIED_CONTACTS_REQ_CODE = 100;
    private final int NOTIFIED_CONTACTS_REQ_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_party);
        ButterKnife.inject(this);

        imageUrlList = new ArrayList<String>();
        fileUploadService.setOnFileUploadListener(this);
        specifiedContacts = new ArrayList<String>();
        notifiedContacts = new ArrayList<String>();

        progress.setMessageId(R.string.info_submitting_party);

        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        dateText.setOnClickListener(new View.OnClickListener() {
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
        dateText.addTextChangedListener(watcher);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTime now = DateTime.now();
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(NewPartyActivity.this, now.getHourOfDay(), now.getMinuteOfHour(),
                                DateFormat.is24HourFormat(NewPartyActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });
        timeText.addTextChangedListener(watcher);
        titleText.addTextChangedListener(watcher);
        addressText.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.place_autocomplete_listitem));
        addressText.addTextChangedListener(watcher);
        detailsText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && submitButton.isEnabled()) {
                    submit();
                    return true;
                }
                return false;
            }
        });
        detailsText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && submitButton.isEnabled()) {
                    submit();
                    return true;
                }
                return false;
            }
        });
        privacyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivacyOptions();
            }
        });
        groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGroupOptions();
            }
        });
        notifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNotifiedContactsActivity();
            }
        });
        addMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTypeSelectionLayout();
            }
        });
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(NewPartyActivity.this, ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.INTENT_ACTION, ImagePickerActivity.ACTION_MULTIPLE_PICK);
                startActivityForResult(intent, Constants.RequestCode.IMAGE_PICKER_REQ_CODE);
            }
        });
        adapter = new GalleryAdapter(this, R.layout.image_collection_listitem_inner, ImageLoaderUtils.getInstance());
        gridGallery.setAdapter(adapter);

        Animation.flyIn(this);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        date.setYear(year);
        date.setMonth(monthOfYear + 1);
        date.setDay(dayOfMonth);
        dateText.setText(date.getDateText());
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        time.setHour(hourOfDay);
        time.setMinute(minute);
        timeText.setText(time.getTimeText());
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
        } else if (requestCode == SPECIFIED_CONTACTS_REQ_CODE && resultCode == RESULT_OK) {
            final ArrayList<String> selectedContacts =
                    data.getStringArrayListExtra(ContactPickerActivity.INTENT_SELECTED_CONTACTS);
            specifiedContacts.clear();
            specifiedContacts.addAll(selectedContacts);
            privacyText.setText(R.string.label_visible_to_contacts);
        } else if (requestCode == NOTIFIED_CONTACTS_REQ_CODE && resultCode == RESULT_OK) {
            final ArrayList<String> selectedContacts =
                    data.getStringArrayListExtra(ContactPickerActivity.INTENT_SELECTED_CONTACTS);
            notifiedContacts.clear();
            notifiedContacts.addAll(selectedContacts);
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
        final boolean populated = populated(titleText) &&
                populated(addressText) &&
                dateText.getText().length() > 0 &&
                timeText.getText().length() > 0;
        submitButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void submit() {
        EditTextUtils.hideSoftInput(titleText);
        EditTextUtils.hideSoftInput(addressText);
        EditTextUtils.hideSoftInput(locationDescriptionText);
        EditTextUtils.hideSoftInput(detailsText);
        showProgress();

        imageUrlList.clear();
        if (imagePathList != null) {
            for (String path : imagePathList) {
                uploadImage(path);
            }
        } else {
            submitNewParty();
        }
    }

    private void uploadImage(final String imagePath) {
        new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                byte avatarData[] = ImageUtils.decodeBitmap(imagePath);
                fileUploadService.upload(imagePath, avatarData);
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
                hideProgress();
            }
        }.execute();
    }

    private void submitNewParty() {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String address = addressText.getText().toString();
                Place place = GooglePlaceUtils.getPlace(address);
                if (place == null) {
                    throw new Exception(getString(R.string.error_invalid_party_address, address));
                }
                final User currentUser = userStore.getCurrentUser();
                List<String> subscriptions = getSubscriptions(currentUser);
                final Party party = new Party(currentUser.getObjectId(),
                        titleText.getText().toString(),
                        date,
                        time,
                        place,
                        locationDescriptionText.getText().toString(),
                        detailsText.getText().toString(),
                        imageUrlList,
                        subscriptions);
                final Party response = restService.newParty(party);
                restService.addPartyMember(response.getObjectId(), currentUser.getObjectId());
                restService.addUserParty(currentUser.getObjectId(), response.getObjectId());
                userStore.addParty(currentUser.getObjectId(), response.getObjectId());
                if (groupType == 0) {
                    new SafeAsyncTask<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            EMGroup group = chatService.createGroup(party.getTitle());
                            chatService.addGroupMember(group.getGroupId(), currentUser.getChatId());
                            restService.addPartyGroup(response.getObjectId(),
                                    group.getGroupId());
                            String groupCreatedText = getString(R.string.label_group_created,
                                    currentUser.getScreenName());
                            chatService.sendSystemMessage(group.getGroupId(),
                                    true, groupCreatedText, null);
                            return true;
                        }
                    }.execute();
                }
                if (notifiedContacts.size() > 0) {
                    String title = getString(R.string.label_new_party_message, currentUser.getScreenName());
                    for (String userId : notifiedContacts) {
                        User user = userStore.getUserById(userId);
                        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_NEW,
                                                               title,
                                                               party.getTitle(),
                                                               response.getObjectId());
                        chatService.sendCmdMessage(user.getChatId(), cmdMessage, false, null);
                    }
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
            submitNewParty();
        }
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        final Throwable cause = e.getCause() != null ? e.getCause() : e;
        if(cause != null) {
            Toaster.showShort(NewPartyActivity.this, cause.getMessage());
        }
    }

    private void showPrivacyOptions() {
        final String options[] = getResources().getStringArray(R.array.label_party_privacy);
        new ListViewDialog(NewPartyActivity.this,
                getString(R.string.label_select_privacy_type),
                Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                privacyType = i;
                switch (i) {
                    case PRIVACY_TYPE_PUBLIC:
                        privacyText.setText(options[i]);
                        break;
                    case PRIVACY_TYPE_CONTACTS:
                        privacyText.setText(options[i]);
                        break;
                    case PRIVACY_TYPE_SPECIFIED_CONTACTS:
                        startSpecifiedContactsActivity();
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private List<String> getSubscriptions(User user) {
        switch (privacyType) {
            case PRIVACY_TYPE_PUBLIC:
                return null;
            case PRIVACY_TYPE_CONTACTS:
                return user.getContacts();
            case PRIVACY_TYPE_SPECIFIED_CONTACTS:
                return specifiedContacts;
            default:
                return null;
        }
    }

    private void showGroupOptions() {
        final String options[] = getResources().getStringArray(R.array.label_party_group);
        new ListViewDialog(NewPartyActivity.this,
                getString(R.string.label_select_group_type),
                Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                groupType = i;
                groupText.setText(options[i]);
            }
        }).show();
    }

    private void startSpecifiedContactsActivity() {
        final Intent intent = new Intent(this, ContactPickerActivity.class);
        startActivityForResult(intent, SPECIFIED_CONTACTS_REQ_CODE);
    }

    private void startNotifiedContactsActivity() {
        final Intent intent = new Intent(this, ContactPickerActivity.class);
        startActivityForResult(intent, NOTIFIED_CONTACTS_REQ_CODE);
    }
}
