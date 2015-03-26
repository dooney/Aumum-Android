package com.aumum.app.mobile.ui.view.sort;

import java.util.Comparator;

/**
 * Created by Administrator on 26/03/2015.
 */
public class SizeComparator implements Comparator<SizeSortable> {

    public int compare(SizeSortable o1, SizeSortable o2) {
        return o2.getSortSize().compareTo(o1.getSortSize());
    }
}
