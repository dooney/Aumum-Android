package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.model.MomentComment;

/**
 * Created by Administrator on 3/03/2015.
 */
public class ReplyMomentCommentEvent {

    private MomentComment comment;

    public ReplyMomentCommentEvent(MomentComment comment) {
        this.comment = comment;
    }

    public MomentComment getComment() {
        return comment;
    }
}
