package com.aumum.app.mobile.ui.message;

import android.os.Bundle;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Message;
import com.aumum.app.mobile.core.MessageHandler;
import com.aumum.app.mobile.core.MessageStore;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.ui.base.CardListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 3/10/2014.
 */
public class MessageListFragment extends CardListFragment {

    private List<Message> dataSet = new ArrayList<Message>();

    @Inject MessageHandler messageHandler;

    private UserStore userStore;

    private MessageStore dataStore;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        userStore = UserStore.getInstance(getActivity());
        dataStore = new MessageStore(getActivity());
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_messages);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dataStore.saveOfflineData(dataSet);
    }

    @Override
    protected String getLastItemTime() {
        return null;
    }

    @Override
    protected boolean hasOfflineData() {
        return dataStore.hasOfflineData();
    }

    @Override
    protected List<Card> loadCards(int mode, String time) throws Exception {
        List<Message> messageList = null;
        switch (mode) {
            case UPWARDS_REFRESH:
                User currentUser = userStore.getCurrentUser(false);
                List<String> messageIdList = currentUser.getMessages();
                if (messageIdList != null) {
                    messageList = dataStore.getMessageList(currentUser.getMessages());
                    for (Message message : messageList) {
                        dataSet.add(0, message);
                    }
                }
                break;
            case STATIC_REFRESH:
                messageList = dataStore.getOfflineList();
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
