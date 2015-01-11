package com.aumum.app.mobile.ui.image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 8/12/2014.
 */
public class GalleryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();

    private boolean isActionMultiplePick;
    private int itemLayoutResId;
    private int maxCount;

    public GalleryAdapter(Context context, int itemLayoutResId) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemLayoutResId = itemLayoutResId;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CustomGallery getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMultiplePick(boolean isMultiplePick) {
        this.isActionMultiplePick = isMultiplePick;
    }

    public void selectAll(boolean selection) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).isSelected = selection;

        }
        notifyDataSetChanged();
    }

    public boolean isAllSelected() {
        boolean isAllSelected = true;

        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).isSelected) {
                isAllSelected = false;
                break;
            }
        }

        return isAllSelected;
    }

    public boolean isAnySelected() {
        boolean isAnySelected = false;

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected) {
                isAnySelected = true;
                break;
            }
        }

        return isAnySelected;
    }

    public ArrayList<CustomGallery> getSelected() {
        ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected) {
                dataT.add(data.get(i));
            }
        }

        return dataT;
    }

    public void addAll(ArrayList<CustomGallery> files) {

        try {
            this.data.clear();
            this.data.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public boolean changeSelection(View v, int position) {

        if (data.get(position).isSelected) {
            data.get(position).isSelected = false;
        } else {
            if (getSelected().size() >= maxCount) {
                return false;
            }
            data.get(position).isSelected = true;
        }
        ImageView imgQueueMultiSelected = ((ViewHolder) v.getTag()).imgQueueMultiSelected;
        imgQueueMultiSelected.setSelected(data.get(position).isSelected);
        Animation.scaleIn(imgQueueMultiSelected, Animation.Duration.SHORT);
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(itemLayoutResId, null);
            holder = new ViewHolder();
            holder.imgQueue = (ImageView) convertView.findViewById(R.id.image_queue);
            holder.imgQueueMultiSelected = (ImageView) convertView
                    .findViewById(R.id.image_multi_selected_queue);

            if (isActionMultiplePick) {
                holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
            } else {
                holder.imgQueueMultiSelected.setVisibility(View.GONE);
            }

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imgQueue.setTag(position);

        ImageLoaderUtils.displayImage(data.get(position).getUri(),
                holder.imgQueue, R.drawable.image_placeholder);

        if (isActionMultiplePick) {
            holder.imgQueueMultiSelected
                    .setSelected(data.get(position).isSelected);
        }

        return convertView;
    }

    public class ViewHolder {
        ImageView imgQueue;
        ImageView imgQueueMultiSelected;
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
