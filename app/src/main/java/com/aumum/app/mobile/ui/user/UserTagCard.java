package com.aumum.app.mobile.ui.user;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.UserTag;
import com.aumum.app.mobile.ui.view.Animation;

/**
 * Created by Administrator on 1/03/2015.
 */
public class UserTagCard {

    private View tagCardLayout;
    private TextView nameText;
    private ImageView checkbox;
    private UserTagClickListener userTagClickListener;

    public UserTagCard(View view, UserTagClickListener userTagClickListener) {
        tagCardLayout = view.findViewById(R.id.layout_tag_card);
        nameText = (TextView)view.findViewById(R.id.text_tag_name);
        checkbox = (ImageView)view.findViewById(R.id.checkbox);
        this.userTagClickListener = userTagClickListener;
    }

    public void refresh(final UserTag userTag) {
        nameText.setText(userTag.getName());
        checkbox.setSelected(userTagClickListener.isSelected(userTag.getName()));

        tagCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userTagClickListener != null) {
                    if (userTagClickListener.onUserTagClick(userTag.getName())) {
                        checkbox.setSelected(!checkbox.isSelected());
                        Animation.scaleIn(checkbox, Animation.Duration.SHORT);
                    }
                }
            }
        });
    }
}
