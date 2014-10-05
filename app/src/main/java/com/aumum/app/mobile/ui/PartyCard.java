package com.aumum.app.mobile.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.Party;
import com.aumum.app.mobile.ui.view.FollowTextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/10/2014.
 */
public class PartyCard extends Card {
    private Party party;
    private boolean isFollowing;

    public PartyCard(Context context, Party party, boolean isFollowing) {
        super(context, R.layout.party_listitem_inner);
        this.party = party;
        this.isFollowing = isFollowing;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        FollowTextView followText = (FollowTextView) view.findViewById(R.id.text_follow);
        int drawableId = (isFollowing ? R.drawable.ic_fa_check_circle : R.drawable.ic_fa_plus_circle);
        followText.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
        followText.setText(isFollowing ? R.string.label_unfollow : R.string.label_follow);
        followText.setFollowListener(new FollowListener(party.getUserId()));
        followText.setFollowing(isFollowing);

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(party.getUserId());

        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        titleText.setText(party.getTitle());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText("5分钟前");

        TextView timeText = (TextView) view.findViewById(R.id.text_time);
        timeText.setText("2014年10月1号 上午11点半");

        TextView locationText = (TextView) view.findViewById(R.id.text_location);
        locationText.setText(party.getLocation());

        TextView ageText = (TextView) view.findViewById(R.id.text_age);
        ageText.setText(Constants.ageOptions[party.getAge()]);

        TextView genderText = (TextView) view.findViewById(R.id.text_gender);
        genderText.setText(Constants.genderOptions[party.getGender()]);

        TextView detailsText = (TextView) view.findViewById(R.id.text_details);
        detailsText.setText(party.getDetails());
    }
}
