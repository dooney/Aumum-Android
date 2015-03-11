package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.model.SavingComment;

/**
 * Created by Administrator on 12/03/2015.
 */
public class ReplySavingCommentEvent {

    private SavingComment comment;

    public ReplySavingCommentEvent(SavingComment comment) {
        this.comment = comment;
    }

    public SavingComment getComment() {
        return comment;
    }
}
