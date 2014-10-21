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
    private OnDropdownItemClickListener onDropdownItemClickListener;

    public static interface OnDropdownItemClickListener {
        public void onDropdownItemClick(int item);
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

    public void init(OnDropdownItemClickListener listener) {
        onDropdownItemClickListener = listener;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(getContext(), onDropdownItemClickListener.getItems(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onDropdownItemClickListener != null) {
                            onDropdownItemClickListener.onDropdownItemClick(i);
                        }
                    }
                });
            }
        });
    }
}
