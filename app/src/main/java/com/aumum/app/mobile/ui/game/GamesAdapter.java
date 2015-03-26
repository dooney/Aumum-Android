package com.aumum.app.mobile.ui.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Game;

import java.util.List;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GamesAdapter extends ArrayAdapter<Game> {

    public GamesAdapter(Context context, List<Game> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GameCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.game_listitem_inner, parent, false);
            card = new GameCard(convertView);
            convertView.setTag(card);
        } else {
            card = (GameCard) convertView.getTag();
        }

        Game game = getItem(position);
        card.refresh(game);

        return convertView;
    }
}
