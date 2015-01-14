package com.aumum.app.mobile.ui.area;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Area;

/**
 * Created by Administrator on 14/01/2015.
 */
public class AreaCard {

    private TextView catalogText;
    private View areaCardLayout;
    private TextView nameText;
    private AreaClickListener areaClickListener;

    public AreaCard(View view, AreaClickListener areaClickListener) {
        catalogText = (TextView) view.findViewById(R.id.text_catalog);
        areaCardLayout = view.findViewById(R.id.layout_area_card);
        nameText = (TextView) view.findViewById(R.id.text_name);
        this.areaClickListener = areaClickListener;
    }

    public void refresh(final Area area) {
        nameText.setText(area.getName());
        areaCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areaClickListener != null) {
                    areaClickListener.onAreaClick(area.getName());
                }
            }
        });
    }

    public void refreshCatalog(String catalog) {
        if (catalog != null) {
            catalogText.setText(catalog);
            catalogText.setVisibility(View.VISIBLE);
        } else {
            catalogText.setVisibility(View.GONE);
        }
    }
}
