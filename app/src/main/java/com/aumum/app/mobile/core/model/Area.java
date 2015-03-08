package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.ui.view.sort.Sortable;
import com.aumum.app.mobile.utils.Strings;

/**
 * Created by Administrator on 14/01/2015.
 */
public class Area extends AggregateRoot implements Sortable {

    private String name;

    public String getName() {
        return name;
    }

    @Override
    public String getSortLetters() {
        String pinyin = Strings.getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            return sortString.toUpperCase();
        } else {
            return "#";
        }
    }
}
