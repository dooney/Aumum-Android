package com.aumum.app.mobile.ui.image;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 19/03/2015.
 */
public class ImageCard extends Card {

    protected Activity activity;
    protected ArrayList<String> images = new ArrayList<>();

    public ImageCard(Activity activity, int innerLayout, List<String> images) {
        super(activity, innerLayout);
        this.activity = activity;
        if (images != null) {
            this.images.addAll(images);
        }
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        GalleryAdapter adapter = new GalleryAdapter(activity, R.layout.image_collection_listitem_inner);
        GridView gridGallery = (GridView) view.findViewById(R.id.grid_gallery);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                clickImageByIndex(position);
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
        for (String imageUrl: images) {
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
                String imageUrl = images.get(0);
                if (imageUrl.endsWith(".gif")) {
                    gifText.setVisibility(View.VISIBLE);
                }
                ImageLoaderUtils.displayImage(list.get(0).getUri(), imageGallery);
                singleImageLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void clickImageByIndex(int index) {
        String imageUrl = images.get(index);
        if (imageUrl.endsWith(".gif")) {
            final Intent intent = new Intent(activity, GifViewActivity.class);
            intent.putExtra(GifViewActivity.INTENT_IMAGE_URI, imageUrl);
            activity.startActivity(intent);
        } else if (images.size() > 1) {
            final Intent intent = new Intent(activity, ImageViewPagerActivity.class);
            intent.putExtra(ImageViewPagerActivity.INTENT_CURRENT_INDEX, index);
            intent.putStringArrayListExtra(ImageViewPagerActivity.INTENT_IMAGES, images);
            activity.startActivity(intent);
        } else {
            final Intent intent = new Intent(activity, ImageViewActivity.class);
            intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
            activity.startActivity(intent);
        }
    }
}
