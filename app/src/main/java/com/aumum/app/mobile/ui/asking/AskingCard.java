package com.aumum.app.mobile.ui.asking;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.ui.user.UserListener;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingCard {

    private Context context;
    private TextView userNameText;
    private TextView areaText;
    private TextView updatedAtText;
    private TextView questionText;
    private TextView replyText;

    public AskingCard(Context context, View view) {
        this.context = context;
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.areaText = (TextView) view.findViewById(R.id.text_area);
        this.updatedAtText = (TextView) view.findViewById(R.id.text_updatedAt);
        this.questionText = (TextView) view.findViewById(R.id.text_question);
        this.replyText = (TextView) view.findViewById(R.id.text_reply);
    }

    public void refresh(Asking asking) {
        userNameText.setText(asking.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(context, asking.getUserId()));
        areaText.setText(Constants.Options.AREA_OPTIONS[asking.getUser().getArea()]);
        updatedAtText.setText(asking.getUpdatedAtFormatted());
        questionText.setText(asking.getQuestion());
        replyText.setText(String.valueOf(asking.getRepliesCount()));
    }
}
