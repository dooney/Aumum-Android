package com.aumum.app.mobile.ui.message;

import android.os.Bundle;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.base.CardListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 3/10/2014.
 */
public class MessageListFragment extends CardListFragment {

    private List<Message> dataSet = new ArrayList<Message>();

    private UserStore userStore;

    private MessageStore messageStore;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        userStore = UserStore.getInstance(getActivity());
        messageStore = new MessageStore(getActivity());
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_messages);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        messageStore.saveOfflineData(dataSet);
    }

    @Override
    protected String getLastItemTime() {
        if (dataSet.size() > 0) {
            Message last = dataSet.get(dataSet.size() - 1);
            return last.getCreatedAt();
        }
        return null;
    }

    @Override
    protected List<Card> loadCards(int mode, String time) throws Exception {
        List<Message> messageList = null;
        User currentUser = userStore.getCurrentUser(false);
        switch (mode) {
            case UPWARDS_REFRESH:
                List<String> messageIdList = currentUser.getMessages();
                if (messageIdList != null) {
                    messageList = messageStore.getUpwardsList(currentUser.getMessages());
                    Collections.reverse(messageList);
                    for (Message message : messageList) {
                        dataSet.add(0, message);
                    }
                }
                break;
            case BACKWARDS_REFRESH:
                messageList = messageStore.getBackwardsList(currentUser.getMessages(), time);
                dataSet.addAll(messageList);
                break;
            case STATIC_REFRESH:
                messageList = messageStore.getOfflineList();
                dataSet.addAll(messageList);
                break;
            default:
                throw new Exception("Invalid refresh mode: " + mode);
        }
        if (messageList != null) {
            for(Message message: messageList) {
                User user = userStore.getUserById(message.getFromUserId(), false);
                message.setFromUser(user);
            }
            return buildCards(messageList);
        }
        return new ArrayList<Card>();
    }

    private List<Card> buildCards(List<Message> messageList) {
        List<Card> cards = new ArrayList<Card>();
        for(Message message: messageList) {
            Card card = new MessageCard(getActivity(), message);
            cards.add(card);
        }
        return cards;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_messages;
    }
}
