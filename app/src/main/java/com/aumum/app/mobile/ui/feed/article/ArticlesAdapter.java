package com.aumum.app.mobile.ui.feed.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.ArticleItem;

import java.util.List;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ArticlesAdapter extends ArrayAdapter<ArticleItem> {

    public ArticlesAdapter(Context context, List<ArticleItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ArticleItemCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.article_item_listitem_inner, parent, false);
            card = new ArticleItemCard(convertView);
            convertView.setTag(card);
        } else {
            card = (ArticleItemCard) convertView.getTag();
        }

        ArticleItem articleItem = getItem(position);
        card.refresh(articleItem);

        return convertView;
    }
}
