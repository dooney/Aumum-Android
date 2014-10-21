package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.aumum.app.mobile.utils.DialogUtils;

/**
 * Created by Administrator on 21/10/2014.
 */
public class DropdownImageView extends ImageView {
    private OnItemClickListener onItemClickListener;

    public static interface OnItemClickListener {
        public void onItemClick(int item);
        public String[] getItems();
    }

    public DropdownImageView(Context context) {
        super(context);
    }

    public DropdownImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DropdownImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(OnItemClickListener listener) {
        onItemClickListener = listener;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(getContext(), onItemClickListener.getItems(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(i);
                        }
                    }
                });
            }
        });
    }
}
