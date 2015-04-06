package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.ui.view.sort.SizeSortable;
import com.aumum.app.mobile.utils.Ln;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupDetails implements SizeSortable {

    private String id;
    private String name;
    private GroupDescription description;
    private int size;
    private boolean isMember;

    public GroupDetails(String id,
                        String name,
                        String description,
                        int size,
                        boolean isMember) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.isMember = isMember;

        try {
            Gson gson = new Gson();
            this.description = gson.fromJson(description,
                    new TypeToken<GroupDescription>() {
                    }.getType());
        } catch (Exception e) {
            Ln.i(e);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public boolean isMember() {
        return isMember;
    }

    @Override
    public Integer getSortSize() {
        return size;
    }

    public String getAvatarUrl() {
        if (description != null) {
            return description.getAvatarUrl();
        }
        return null;
    }

    public String getDescription() {
        if (description != null) {
            return description.getDescription();
        }
        return name;
    }
}
