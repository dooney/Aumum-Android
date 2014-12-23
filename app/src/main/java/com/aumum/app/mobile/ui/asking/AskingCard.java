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
    private TextView cityText;
    private TextView updatedAtText;
    private SpannableTextView titleText;
    private TextView replyText;

    public AskingCard(Context context, View view) {
        this.context = context;
        this.hasPicImage = (ImageView) view.findViewById(R.id.image_has_pic);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.cityText = (TextView) view.findViewById(R.id.text_city);
        this.updatedAtText = (TextView) view.findViewById(R.id.text_updatedAt);
        this.titleText = (SpannableTextView) view.findViewById(R.id.text_title);
        this.replyText = (TextView) view.findViewById(R.id.text_reply);
    }

    public void refresh(Asking asking) {
        if (asking.getImagesCount() > 0) {
            hasPicImage.setVisibility(View.VISIBLE);
        } else {
            hasPicImage.setVisibility(View.GONE);
        }
        userNameText.setText(asking.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(context, asking.getUserId()));
        cityText.setText(Constants.Options.CITY_OPTIONS[asking.getUser().getCity()]);
        updatedAtText.setText(asking.getUpdatedAtFormatted());
        titleText.setSpannableText(asking.getTitle());
        replyText.setText(String.valueOf(asking.getRepliesCount()));
    }
}
