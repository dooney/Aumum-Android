package com.aumum.app.mobile.ui.message;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.base.CardListFragment;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    protected List<Card> loadCards(int mode) throws Exception {
        List<Message> messageList;
        User currentUser = userStore.getCurrentUser(false);
        switch (mode) {
            case UPWARDS_REFRESH:
                messageList = getUpwardsList(currentUser);
                break;
            case BACKWARDS_REFRESH:
                messageList = getBackwardsList(currentUser);
                break;
            case STATIC_REFRESH:
                messageList = getStaticList();
                break;
            default:
                throw new Exception("Invalid refresh mode: " + mode);
        }
        if (messageList != null) {
            for (Message message : messageList) {
                User user = userStore.getUserById(message.getFromUserId(), false);
                message.setFromUser(user);
            }
        }
        return buildCards();
    }

    private List<Message> getUpwardsList(User currentUser) {
        List<String> messageIdList = currentUser.getMessages();
        if (messageIdList != null) {
            String after = null;
            if (dataSet.size() > 0) {
                after = dataSet.get(0).getCreatedAt();
            }
            List<Message> messageList = messageStore.getUpwardsList(currentUser.getMessages(), after);
            Collections.reverse(messageList);
            for (Message message : messageList) {
                dataSet.add(0, message);
            }
            return messageList;
        }
        return null;
    }

    private List<Message> getBackwardsList(User currentUser) {
        if (dataSet.size() > 0) {
            Message last = dataSet.get(dataSet.size() - 1);
            List<Message> messageList = messageStore.getBackwardsList(currentUser.getMessages(), last.getCreatedAt());
            dataSet.addAll(messageList);
            if (messageList.size() > 0) {
                setLoadMore(true);
            } else {
                setLoadMore(false);
                Toaster.showShort(getActivity(), R.string.message_all_loaded);
            }
            return messageList;
        }
        return null;
    }

    private List<Message> getStaticList() {
        List<Message> messageList = messageStore.getOfflineList();
        dataSet.addAll(messageList);
        return messageList;
    }

    private List<Card> buildCards() {
        List<Card> cards = new ArrayList<Card>();
        if (dataSet.size() > 0) {
            for (Message message : dataSet) {
                Card card = new MessageCard(getActivity(), message);
                cards.add(card);
            }
        }
        return cards;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_messages;
    }
}
