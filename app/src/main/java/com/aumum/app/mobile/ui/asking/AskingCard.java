package com.aumum.app.mobile.ui.asking;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.SpannableTextView;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingCard {

    private Context context;
    private ImageView hasPicImage;
    private TextView userNameText;
    private TextView areaText;
    private TextView updatedAtText;
    private SpannableTextView questionText;
    private TextView replyText;

    public AskingCard(Context context, View view) {
        this.context = context;
        this.hasPicImage = (ImageView) view.findViewById(R.id.image_has_pic);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.areaText = (TextView) view.findViewById(R.id.text_area);
        this.updatedAtText = (TextView) view.findViewById(R.id.text_updatedAt);
        this.questionText = (SpannableTextView) view.findViewById(R.id.text_question);
        this.replyText = (TextView) view.findViewById(R.id.text_reply);
    }

    public void refresh(Asking asking) {
        if (asking.getImages().size() > 0) {
            hasPicImage.setVisibility(View.VISIBLE);
        } else {
            hasPicImage.setVisibility(View.GONE);
        }
        userNameText.setText(asking.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(context, asking.getUserId()));
        areaText.setText(Constants.Options.AREA_OPTIONS[asking.getUser().getArea()]);
        updatedAtText.setText(asking.getUpdatedAtFormatted());
        questionText.setSpannableText(asking.getQuestion());
        replyText.setText(String.valueOf(asking.getRepliesCount()));
    }
}
