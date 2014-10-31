package com.aumum.app.mobile.ui.message;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.base.CardListFragment;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 3/10/2014.
 */
public class MessageListFragment extends CardListFragment
        implements DeleteMessageListener.OnActionListener{

    private List<Message> dataSet = new ArrayList<Message>();

    private User currentUser;

    private UserStore userStore;

    private MessageStore messageStore;

    private int subCategory;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userStore = UserStore.getInstance(getActivity());
        messageStore = new MessageStore();
        final Intent intent = getActivity().getIntent();
        subCategory = intent.getIntExtra(MessageListActivity.INTENT_MESSAGE_TYPE, 0);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_messages);
    }

    @Override
    protected List<Card> loadCards(int mode) throws Exception {
        List<Message> messageList;
        currentUser = userStore.getCurrentUser(false);
        switch (mode) {
            case STATIC_REFRESH:
            case UPWARDS_REFRESH:
                messageList = getUpwardsList(currentUser);
                break;
            case BACKWARDS_REFRESH:
                messageList = getBackwardsList(currentUser);
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
            List<Message> messageList = messageStore.getUpwardsList(currentUser.getMessages(),
                    Message.getSubCategoryTypes(subCategory), after);
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
            List<Message> messageList = messageStore.getBackwardsList(currentUser.getMessages(),
                    Message.getSubCategoryTypes(subCategory), last.getCreatedAt());
            dataSet.addAll(messageList);
            if (messageList.size() > 0) {
                setLoadMore(true);
            } else {
                setLoadMore(false);
                Toaster.showShort(getActivity(), R.string.info_all_loaded);
            }
            return messageList;
        }
        return null;
    }

    private List<Card> buildCards() {
        List<Card> cards = new ArrayList<Card>();
        if (dataSet.size() > 0) {
            for (Message message : dataSet) {
                Card card = new MessageCard(getActivity(), message, currentUser.getObjectId(), this);
                cards.add(card);
            }
        }
        return cards;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_messages;
    }

    @Override
    public void onMessageDeletedSuccess(String messageId) {
        try {
            List<Card> cardList = getData();
            for (Card card : cardList) {
                Message message = ((MessageCard) card).getMessage();
                if (message.getObjectId().equals(messageId)) {
                    dataSet.remove(message);
                    cardList.remove(card);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListAdapter().notifyDataSetChanged();
                        }
                    });
                    Toaster.showShort(getActivity(), R.string.info_message_deleted);
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
        Toaster.showLong(getActivity(), R.string.error_delete_message);
    }
}
