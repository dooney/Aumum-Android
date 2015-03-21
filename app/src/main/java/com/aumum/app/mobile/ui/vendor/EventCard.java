package com.aumum.app.mobile.ui.vendor;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Event;
import com.aumum.app.mobile.ui.browser.BrowserActivity;
import com.aumum.app.mobile.ui.view.SpannableTextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 21/03/2015.
 */
public class EventCard extends Card {

    private Activity activity;
    private Event event;

    public EventCard(Activity activity,  Event event) {
        super(activity, R.layout.event_listitem_inner);
        this.activity = activity;
        this.event = event;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        TextView titleText = (TextView) view.findViewById(R.id.text_name);
        titleText.setText(event.getName());

        TextView timeText = (TextView) view.findViewById(R.id.text_time);
        timeText.setText(event.getDateTimeText());

        TextView addressText = (TextView) view.findViewById(R.id.text_address);
        addressText.setText(event.getAddress());

        SpannableTextView descriptionText = (SpannableTextView) view.findViewById(R.id.text_description);
        descriptionText.setSpannableText(event.getDescription());

        TextView linkText = (TextView) view.findViewById(R.id.text_link);
        linkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBrowserActivity(event);
            }
        });
    }

    private void startBrowserActivity(Event event) {
        final Intent intent = new Intent(activity, BrowserActivity.class);
        intent.putExtra(BrowserActivity.INTENT_TITLE, event.getName());
        intent.putExtra(BrowserActivity.INTENT_URL, event.getUrl());
        activity.startActivity(intent);
    }
}
