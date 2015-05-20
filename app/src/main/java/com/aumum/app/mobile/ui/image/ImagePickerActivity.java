package com.aumum.app.mobile.ui.image;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;

public class ImagePickerActivity extends BaseActionBarActivity {

    GridView gridGallery;
    Handler handler;
    GalleryAdapter adapter;

    ImageView noMediaImage;
    View confirmButton;

    int action;

    public static final String INTENT_ALL_PATH = "allPath";
    public static final String INTENT_SINGLE_PATH = "singlePath";
    public static final String INTENT_ACTION = "action";
    public static final int ACTION_PICK = 1;
    public static final int ACTION_MULTIPLE_PICK = 2;
    private static final int MAX_COUNT = 9;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        action = getIntent().getIntExtra(INTENT_ACTION, 0);
        switch (action) {
            case ACTION_PICK:
                setTitle(R.string.title_activity_choose_image);
                break;
            case ACTION_MULTIPLE_PICK:
                setTitle(R.string.title_activity_add_image);
                break;
            default:
                finish();
                break;
        }
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action == ACTION_MULTIPLE_PICK) {
            MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
            menuItem.setActionView(R.layout.menuitem_button_ok);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            View view = menuItem.getActionView();
            confirmButton = view.findViewById(R.id.b_ok);
            confirmButton.setOnClickListener(mOkClickListener);
            confirmButton.setEnabled(false);
        }
        return true;
    }

    private void init() {

        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.grid_gallery);
        adapter = new GalleryAdapter(getApplicationContext(),
                R.layout.gallery_listitem_inner);
        adapter.setMaxCount(MAX_COUNT);
        gridGallery.setOnScrollListener(ImageLoaderUtils.getOnScrollListener());

        if (action == ACTION_MULTIPLE_PICK) {
            gridGallery.setOnItemClickListener(mItemMulClickListener);
            adapter.setMultiplePick(true);

        } else if (action == ACTION_PICK) {
            gridGallery.setOnItemClickListener(mItemSingleClickListener);
            adapter.setMultiplePick(false);
        }

        gridGallery.setAdapter(adapter);
        noMediaImage = (ImageView) findViewById(R.id.image_no_media);

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.addAll(getGalleryPhotos());
                        checkImageStatus();
                    }
                });
                Looper.loop();
            };

        }.start();

    }

    private void checkImageStatus() {
        if (adapter.isEmpty()) {
            noMediaImage.setVisibility(View.VISIBLE);
        } else {
            noMediaImage.setVisibility(View.GONE);
        }
    }

    View.OnClickListener mOkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ArrayList<CustomGallery> selected = adapter.getSelected();

            String[] allPath = new String[selected.size()];
            for (int i = 0; i < allPath.length; i++) {
                allPath[i] = selected.get(i).imageUri;
            }

            Intent data = new Intent().putExtra(INTENT_ALL_PATH, allPath);
            setResult(RESULT_OK, data);
            finish();
        }
    };
    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            if (adapter.changeSelection(v, position)) {
                confirmButton.setEnabled(adapter.isAnySelected());
            } else {
                Toaster.showShort(ImagePickerActivity.this,
                        getString(R.string.error_selection_no_more_than, MAX_COUNT));
            }
        }
    };

    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            CustomGallery item = adapter.getItem(position);
            String ImageUri = item.imageUri;
            Intent data = new Intent().putExtra(INTENT_SINGLE_PATH, ImageUri);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

        try {
            final String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID };
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imageCursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imageCursor != null && imageCursor.getCount() > 0) {

                while (imageCursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();

                    int dataColumnIndex = imageCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.imageUri = imageCursor.getString(dataColumnIndex);
                    item.type = CustomGallery.FILE;

                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            Ln.e(e);
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }
}
