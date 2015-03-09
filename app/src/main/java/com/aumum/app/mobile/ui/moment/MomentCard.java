package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentCard extends Card {

    private Activity activity;
    private Moment moment;
    private String currentUserId;
    private MomentLikeListener likeListener;
    private MomentCommentClickListener momentCommentClickListener;

    public MomentCard(Activity activity, Moment moment, String currentUserId,
                      MomentCommentClickListener momentCommentClickListener) {
        super(activity, R.layout.moment_listitem_inner);
        this.activity = activity;
        this.moment = moment;
        this.currentUserId = currentUserId;
        likeListener = new MomentLikeListener(moment);
        this.momentCommentClickListener = momentCommentClickListener;
    }

    public Moment getMoment() {
        return moment;
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
                final Intent intent = new Intent(activity, ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
                activity.startActivity(intent);
            }
        });
        ImageView imageGallery = (ImageView) view.findViewById(R.id.image_gallery);
        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImageByIndex(0);
            }
        });
        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String imageUrl: moment.getImages()) {
            CustomGallery item = new CustomGallery();
            item.type = CustomGallery.HTTP;
            item.imageUri = imageUrl;
            list.add(item);
        }
        gridGallery.setVisibility(View.GONE);
        imageGallery.setVisibility(View.GONE);
        if (list.size() > 0) {
            if (list.size() > 1) {
                adapter.addAll(list);
                gridGallery.setVisibility(View.VISIBLE);
            } else {
                ImageLoaderUtils.displayImage(list.get(0).getUri(), imageGallery);
                imageGallery.setVisibility(View.VISIBLE);
            }
        }

        TextView commentText = (TextView) view.findViewById(R.id.text_comment);
        int comments = moment.getCommentsCount();
        commentText.setText(comments > 0 ? String.valueOf(comments) : view.getResources().getString(R.string.label_comment));
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (momentCommentClickListener != null) {
                    momentCommentClickListener.OnClick(moment.getObjectId());
                }
            }
        });

        LikeTextView likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);
        likeText.init(moment.getLikesCount(), moment.isLiked(currentUserId));
        likeText.setLikeListener(likeListener);
    }

    private void clickImageByIndex(int index) {
        String imageUrl = moment.getImages().get(index);
        final Intent intent = new Intent(activity, ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
        activity.startActivity(intent);
    }
}