package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentComment extends Comment {

    public MomentComment(String parentId,
                         String repliedId,
                         String content,
                         String userId) {
        super(parentId, repliedId, content, userId);
    }
}
