package com.aumum.app.mobile.ui.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/05/2015.
 */
public class AlbumAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> data = new ArrayList<>();

    public AlbumAdapter(Context context) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<String> urlList) {
        this.data.clear();
        this.data.addAll(urlList);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.album_listitem_inner, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoaderUtils.displayImage(data.get(position), holder.imageView);
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
    }
}
