package com.aumum.app.mobile.ui.saving;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Saving;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.saving.SavingCommentClickListener;
import com.aumum.app.mobile.ui.saving.SavingLikeListener;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingCard extends Card {

    private Activity activity;
    private Saving saving;
    private String currentUserId;
    private SavingLikeListener likeListener;
    private SavingCommentClickListener savingCommentClickListener;

    public SavingCard(Activity activity, Saving saving, String currentUserId,
                      SavingCommentClickListener savingCommentClickListener) {
        super(activity, R.layout.saving_listitem_inner);
        this.activity = activity;
        this.saving = saving;
        this.currentUserId = currentUserId;
        likeListener = new SavingLikeListener(saving);
        this.savingCommentClickListener = savingCommentClickListener;
    }

    public Saving getSaving() {
        return saving;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(saving.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), saving.getUserId()));

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(saving.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), saving.getUserId()));

        TextView cityText = (TextView) view.findViewById(R.id.text_city);
        cityText.setText(saving.getUser().getCity());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(saving.getCreatedAtFormatted());

        SpannableTextView detailsText = (SpannableTextView) view.findViewById(R.id.text_details);
        if (saving.getDetails() != null && saving.getDetails().length() > 0) {
            detailsText.setSpannableText(saving.getDetails());
        } else {
            detailsText.setVisibility(View.GONE);
        }

        GalleryAdapter adapter = new GalleryAdapter(activity, R.layout.image_collection_listitem_inner);
        GridView gridGallery = (GridView) view.findViewById(R.id.grid_gallery);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String imageUrl = saving.getImages().get(position);
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
        for (String imageUrl: saving.getImages()) {
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
        int comments = saving.getCommentsCount();
        commentText.setText(comments > 0 ? String.valueOf(comments) : view.getResources().getString(R.string.label_comment));
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savingCommentClickListener != null) {
                    savingCommentClickListener.OnClick(saving.getObjectId());
                }
            }
        });

        LikeTextView likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);
        likeText.init(saving.getLikesCount(), saving.isLiked(currentUserId));
        likeText.setLikeListener(likeListener);
    }

    private void clickImageByIndex(int index) {
        String imageUrl = saving.getImages().get(index);
        final Intent intent = new Intent(activity, ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
        activity.startActivity(intent);
    }
}
