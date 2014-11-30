package com.aumum.app.mobile.ui.asking;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Asking;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingCard {

    private Context context;
    private TextView userNameText;
    private TextView createdAtText;
    private TextView questionText;
    private TextView replyText;

    public AskingCard(Context context, View view) {
        this.context = context;
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.questionText = (TextView) view.findViewById(R.id.text_question);
        this.replyText = (TextView) view.findViewById(R.id.text_reply);
    }

    public void refresh(Asking asking) {
        userNameText.setText(asking.getUser().getScreenName());
        createdAtText.setText(asking.getCreatedAtFormatted());
        questionText.setText(asking.getQuestion());
        if (asking.getRepliesCount() > 0) {
            replyText.setText(String.valueOf(asking.getRepliesCount()));
        } else {
            replyText.setText(context.getString(R.string.label_reply));
        }
    }
}
