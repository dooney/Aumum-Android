package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 2/12/2014.
 */
public class FavoriteTextView extends AnimateTextView {

    private boolean isFavorite;
    private int favoriteResId;
    private int favoritedResId;

    private OnFavoriteListener favoriteListener;

    public void setFavoriteResId(int favoriteResId) {
        this.favoriteResId = favoriteResId;
    }

    public void setFavoritedResId(int favoritedResId) {
        this.favoritedResId = favoritedResId;
    }

    public void setFavoriteListener(OnFavoriteListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }

    public FavoriteTextView(Context context) {
        super(context);
    }

    public FavoriteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavoriteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static interface OnFavoriteListener {
        public void onUnFavorite(FavoriteTextView view);
        public void onFavorite(FavoriteTextView view);
    }

    public void init(int favorites, boolean isFavorite) {
        setText(favorites > 0 ?
                String.valueOf(favorites) :
                getResources().getString(R.string.label_favorite));
        int drawableId = (isFavorite ? favoritedResId : favoriteResId);
        setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
    }

    @Override
    public void onClick(View view) {
        boolean oldValue = isFavorite;
        update(!isFavorite);

        // animation
        super.onClick(view);

        if (favoriteListener != null) {
            if (oldValue) {
                favoriteListener.onUnFavorite(FavoriteTextView.this);
            } else {
                favoriteListener.onFavorite(FavoriteTextView.this);
            }
        }
    }

    @Override
    public void update(boolean newValue) {
        isFavorite = newValue;
        int drawableId = (isFavorite ? favoritedResId : favoriteResId);
        setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
        String currentText = getText().toString();
        try {
            Integer currentFavorites = Integer.parseInt(currentText);
            if (isFavorite) {
                currentFavorites++;
            } else {
                currentFavorites--;
            }
            if (currentFavorites > 0) {
                setText(currentFavorites.toString());
            } else {
                setText(getResources().getString(R.string.label_favorite));
            }
        } catch (NumberFormatException e) {
            setText("1");
        }
    }
}
