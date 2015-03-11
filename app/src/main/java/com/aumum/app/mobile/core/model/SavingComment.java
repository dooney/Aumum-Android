package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingComment extends Comment {

    public SavingComment(String parentId,
                         String repliedId,
                         String content,
                         String userId) {
        super(parentId, repliedId, content, userId);
    }
}
