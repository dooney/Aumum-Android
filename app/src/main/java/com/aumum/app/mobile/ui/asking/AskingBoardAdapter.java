package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingBoard;

import java.util.List;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingBoardAdapter extends ArrayAdapter<AskingBoard> {

    public AskingBoardAdapter(Activity activity,
                              List<AskingBoard> objects) {
        super(activity, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AskingBoardCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.asking_board_listitem_inner, parent, false);
            card = new AskingBoardCard(convertView);
            convertView.setTag(card);
        } else {
            card = (AskingBoardCard) convertView.getTag();
        }

        AskingBoard askingBoard = getItem(position);
        card.refresh(askingBoard);

        return convertView;
    }
}
