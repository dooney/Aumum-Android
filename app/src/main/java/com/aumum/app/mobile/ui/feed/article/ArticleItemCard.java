package com.aumum.app.mobile.ui.feed.article;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.ArticleItem;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ArticleItemCard {

    private View view;

    public ArticleItemCard(View view) {
        this.view = view;
    }

    public void refresh(ArticleItem articleItem) {
        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        titleText.setText(articleItem.getTitle());
    }
}
