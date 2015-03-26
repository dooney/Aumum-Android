package com.aumum.app.mobile.ui.view.sort;

import java.util.Comparator;

/**
 * Created by Administrator on 18/12/2014.
 */
public class InitialComparator implements Comparator<InitialSortable> {

    public int compare(InitialSortable o1, InitialSortable o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
