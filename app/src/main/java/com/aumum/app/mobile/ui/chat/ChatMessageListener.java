package com.aumum.app.mobile.ui.chat;

import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 24/11/2014.
 */
public interface ChatMessageListener {

    void refresh(EMMessage message, boolean showTimestamp, int position);
}
