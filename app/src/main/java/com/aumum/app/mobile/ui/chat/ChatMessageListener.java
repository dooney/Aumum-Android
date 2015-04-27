package com.aumum.app.mobile.ui.chat;

import com.aumum.app.mobile.core.model.ChatMessage;

/**
 * Created by Administrator on 24/11/2014.
 */
public interface ChatMessageListener {

    void refresh(ChatMessage message, boolean showTimestamp, int position);
}
