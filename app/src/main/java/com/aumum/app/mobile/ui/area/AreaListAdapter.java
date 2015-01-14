package com.aumum.app.mobile.ui.area;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Area;

import java.util.List;

/**
 * Created by Administrator on 14/01/2015.
 */
public class AreaListAdapter extends ArrayAdapter<Area>
        implements SectionIndexer {

    private AreaClickListener areaClickListener;

    public AreaListAdapter(Context context, List<Area> objects,
                           AreaClickListener areaClickListener) {
        super(context, 0, objects);
        this.areaClickListener = areaClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AreaCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.area_listitem_inner, parent, false);
            card = new AreaCard(convertView, areaClickListener);
            convertView.setTag(card);
        } else {
            card = (AreaCard) convertView.getTag();
        }

        Area area = getItem(position);
        card.refresh(area);

        int section = getSectionForPosition(position);
        if(position == getPositionForSection(section)){
            card.refreshCatalog(area.getSortLetters());
        } else {
            card.refreshCatalog(null);
        }

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getItem(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return getItem(position).getSortLetters().charAt(0);
    }
}
