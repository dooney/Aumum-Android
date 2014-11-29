package com.aumum.app.mobile.ui.asking;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Asking;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingCard {

    private TextView userNameText;
    private TextView createdAtText;
    private TextView questionText;

    public AskingCard(View view) {
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.questionText = (TextView) view.findViewById(R.id.text_question);
    }

    public void refresh(Asking asking) {
        userNameText.setText(asking.getUser().getScreenName());
        createdAtText.setText(asking.getCreatedAtFormatted());
        questionText.setText(asking.getQuestion());
    }
}
