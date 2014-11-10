package com.aumum.app.mobile.ui.circle;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.core.model.Conversation;

import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ConversationsAdapter extends ArrayAdapter<Conversation> {

    public ConversationsAdapter(Context context, List<Conversation> objects) {
        super(context, 0, objects);
    }
}
