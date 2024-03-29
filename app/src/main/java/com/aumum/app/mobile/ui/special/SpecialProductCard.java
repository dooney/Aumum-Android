package com.aumum.app.mobile.ui.special;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialProductCard {

    Activity activity;
    private ImageView previewImage;
    private TextView nameText;
    private TextView wasText;
    private TextView nowText;
    private LikeTextView likeText;
    private FavoriteTextView favoriteText;

    public SpecialProductCard(Activity activity, View view) {
        this.activity = activity;
        previewImage = (ImageView) view.findViewById(R.id.image_preview);
        nameText = (TextView) view.findViewById(R.id.text_name);
        wasText = (TextView) view.findViewById(R.id.text_was);
        nowText = (TextView) view.findViewById(R.id.text_now);
        likeText = (LikeTextView) view.findViewById(R.id.text_like);
        favoriteText = (FavoriteTextView) view.findViewById(R.id.text_favorite);
    }

    public void refresh(final SpecialProduct specialProduct, final String currentUserId) {
        ImageLoaderUtils.displayImage(specialProduct.getPreviewUrl(), previewImage);
        previewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(activity, ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, specialProduct.getPreviewUrl());
                activity.startActivity(intent);
            }
        });
        nameText.setText(specialProduct.getName());
        String was = activity.getString(R.string.label_was, specialProduct.getWas());
        wasText.setText(was);
        String now = activity.getString(R.string.label_now, specialProduct.getNow());
        nowText.setText(now);

        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up_s);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up_s);
        likeText.init(specialProduct.getLikesCount(), specialProduct.isLiked(currentUserId));
        SpecialProductLikeListener likeListener = new SpecialProductLikeListener(specialProduct);
        likeText.setLikeListener(likeListener);

        favoriteText.setFavoriteResId(R.drawable.ic_fa_star_o_s);
        favoriteText.setFavoritedResId(R.drawable.ic_fa_star_s);
        favoriteText.init(specialProduct.getFavoritesCount(), specialProduct.isFavorited(currentUserId));
        favoriteText.setFavoriteListener(new SpecialProductFavoriteListener(specialProduct));
    }
}
