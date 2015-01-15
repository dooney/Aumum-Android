package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyCalendar;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.view.calendar.WeekView;
import com.aumum.app.mobile.ui.view.calendar.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class PartyCalendarFragment extends LoaderFragment<PartyCalendar>
        implements WeekView.MonthChangeListener,
                   WeekView.EventClickListener {

    @Inject PartyStore partyStore;
    @Inject UserStore userStore;

    private List<WeekViewEvent> events;
    private WeekView weekView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        events = new ArrayList<WeekViewEvent>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_calendar, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekView = (WeekView) view.findViewById(R.id.weekView);
        weekView.setDefaultEventColor(Color.parseColor("#d9534f"));
        weekView.setMonthChangeListener(this);
        weekView.setOnEventClickListener(this);
    }

    @Override
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return weekView;
    }

    @Override
    protected PartyCalendar loadDataCore(Bundle bundle) throws Exception {
        User currentUser = userStore.getCurrentUser();
        List<Party> partyList = partyStore
                .getAllList(currentUser.getObjectId(), currentUser.getParties());
        return new PartyCalendar(partyList);
    }

    @Override
    protected void handleLoadResult(PartyCalendar partyCalendar) {
        if (partyCalendar != null) {
            setData(partyCalendar);
            for (Party party: partyCalendar.getPartyList()) {
                int startYear = party.getDate().getYear();
                int startMonth = party.getDate().getMonth() - 1;
                int startDayOfMonth = party.getDate().getDay();
                int startHourOfDay = party.getTime().getHour();
                int startMinute = party.getTime().getMinute();
                WeekViewEvent event = buildEvent(party.getObjectId(), party.getTitle(),
                        startYear, startMonth, startDayOfMonth, startHourOfDay, startMinute);
                events.add(event);
            }
        }
    }

    private WeekViewEvent buildEvent(String eventId,
                                     String eventTitle,
                                     int startYear,
                                     int startMonth,
                                     int startDayOfMonth,
                                     int startHourOfDay,
                                     int startMinute) {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, startYear);
        startTime.set(Calendar.MONTH, startMonth);
        startTime.set(Calendar.DAY_OF_MONTH, startDayOfMonth);
        startTime.set(Calendar.HOUR_OF_DAY, startHourOfDay);
        startTime.set(Calendar.MINUTE, startMinute);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 2);
        WeekViewEvent event = new WeekViewEvent(eventId, eventTitle, startTime, endTime);
        return event;
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> eventList = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event: events) {
            Calendar calendar = event.getStartTime();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            if (year == newYear && month == newMonth) {
                eventList.add(event);
            }
        }
        return eventList;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        startPartyDetailsActivity(event.getId());
    }

    private void startPartyDetailsActivity(String partyId) {
        final Intent intent = new Intent(getActivity(), PartyDetailsSingleActivity.class);
        intent.putExtra(PartyDetailsSingleActivity.INTENT_PARTY_ID, partyId);
        startActivity(intent);
    }
}
