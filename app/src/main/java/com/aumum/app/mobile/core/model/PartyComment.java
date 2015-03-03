package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 10/10/2014.
 */
public class PartyComment extends Comment {

    public PartyComment(String parentId,
                        String repliedId,
                        String content,
                        String userId) {
        super(parentId, repliedId, content, userId);
    }
}
