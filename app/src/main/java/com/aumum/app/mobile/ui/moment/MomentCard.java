package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 8/11/2014.
 */
public class MomentCard extends Card {

    public MomentCard(final Activity context, final Moment moment, String currentUserId) {
        super(context, R.layout.moment_listitem_inner);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
    }
}
