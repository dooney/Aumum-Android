package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.ImageViewDialog;
import com.aumum.app.mobile.ui.view.SpannableTextView;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentCard extends Card {

    private Activity activity;
    private Moment moment;

    public MomentCard(Activity activity, Moment moment) {
        super(activity, R.layout.moment_listitem_inner);
        this.activity = activity;
        this.moment = moment;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(moment.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), moment.getUserId()));

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(moment.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), moment.getUserId()));

        TextView cityText = (TextView) view.findViewById(R.id.text_city);
        cityText.setText(moment.getUser().getCity());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(moment.getCreatedAtFormatted());

        SpannableTextView detailsText = (SpannableTextView) view.findViewById(R.id.text_details);
        if (moment.getDetails() != null && moment.getDetails().length() > 0) {
            detailsText.setSpannableText(moment.getDetails());
        } else {
            detailsText.setVisibility(View.GONE);
        }

        GalleryAdapter adapter = new GalleryAdapter(activity, R.layout.image_collection_listitem_inner);
        GridView gridGallery = (GridView) view.findViewById(R.id.grid_gallery);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String imageUrl = moment.getImages().get(position);
                new ImageViewDialog(activity, imageUrl).show();
            }
        });
        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String imageUrl: moment.getImages()) {
            CustomGallery item = new CustomGallery();
            item.type = CustomGallery.HTTP;
            item.imageUri = imageUrl;
            list.add(item);
        }
        if (list.size() > 0) {
            adapter.addAll(list);
        } else {
            gridGallery.setVisibility(View.GONE);
        }
    }
}