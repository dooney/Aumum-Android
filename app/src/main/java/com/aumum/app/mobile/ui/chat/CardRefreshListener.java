package com.aumum.app.mobile.ui.chat;

import com.easemob.chat.EMConversation;

/**
 * Created by Administrator on 24/11/2014.
 */
public interface CardRefreshListener {

    void refresh(EMConversation conversation, int position);
}
