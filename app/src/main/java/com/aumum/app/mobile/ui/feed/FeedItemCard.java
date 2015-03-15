package com.aumum.app.mobile.ui.feed;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.FeedItem;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.GifViewActivity;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 14/03/2015.
 */
public class FeedItemCard extends Card {

    private Activity activity;
    private FeedItem feedItem;

    public FeedItemCard(Activity activity, FeedItem feedItem) {
        super(activity, R.layout.feed_item_listitem_inner);
        this.activity = activity;
        this.feedItem = feedItem;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        SpannableTextView contentText = (SpannableTextView) view.findViewById(R.id.text_content);
        if (feedItem.getText() != null) {
            contentText.setSpannableText(feedItem.getText());
            contentText.setVisibility(View.VISIBLE);
        } else {
            contentText.setVisibility(View.GONE);
        }

        GalleryAdapter adapter = new GalleryAdapter(activity, R.layout.image_collection_listitem_inner);
        GridView gridGallery = (GridView) view.findViewById(R.id.grid_gallery);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String imageUrl = feedItem.getImages().get(position);
                final Intent intent = new Intent(activity, ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
                activity.startActivity(intent);
            }
        });
        ViewGroup singleImageLayout = (ViewGroup) view.findViewById(R.id.layout_single_image);
        ImageView imageGallery = (ImageView) view.findViewById(R.id.image_gallery);
        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImageByIndex(0);
            }
        });
        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String imageUrl: feedItem.getImages()) {
            CustomGallery item = new CustomGallery();
            item.type = CustomGallery.HTTP;
            item.imageUri = imageUrl;
            list.add(item);
        }
        gridGallery.setVisibility(View.GONE);
        singleImageLayout.setVisibility(View.GONE);
        if (list.size() > 0) {
            if (list.size() > 1) {
                adapter.addAll(list);
                gridGallery.setVisibility(View.VISIBLE);
            } else {
                TextView gifText = (TextView) view.findViewById(R.id.text_gif);
                gifText.setVisibility(View.GONE);
                String imageUrl = feedItem.getImages().get(0);
                if (imageUrl.endsWith(".gif")) {
                    gifText.setVisibility(View.VISIBLE);
                }
                ImageLoaderUtils.displayImage(list.get(0).getUri(), imageGallery);
                singleImageLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void clickImageByIndex(int index) {
        String imageUrl = feedItem.getImages().get(index);
        if (imageUrl.endsWith(".gif")) {
            final Intent intent = new Intent(activity, GifViewActivity.class);
            intent.putExtra(GifViewActivity.INTENT_IMAGE_URI, imageUrl);
            activity.startActivity(intent);
        } else {
            final Intent intent = new Intent(activity, ImageViewActivity.class);
            intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
            activity.startActivity(intent);
        }
    }
}
