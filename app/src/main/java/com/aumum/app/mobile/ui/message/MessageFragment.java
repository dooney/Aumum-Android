package com.aumum.app.mobile.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.service.ScheduleService;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MessageFragment extends Fragment
        implements ScheduleService.OnScheduleListener{

    @Inject MessageStore messageStore;
    private ScheduleService scheduleService;
    private SafeAsyncTask<Boolean> task;

    private int unreadPartyMembershipCount;
    private int unreadPartyCommentsCount;
    private int unreadPartyLikesCount;

    private ImageView partyMembershipUnreadImage;
    private ImageView partyCommentsUnreadImage;
    private ImageView partyLikesUnreadImage;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        scheduleService = new ScheduleService(this, Constants.Schedule.DELAY);
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleService.start();
    }

    @Override
    public void onPause() {
        super.onDestroy();
        scheduleService.shutDown();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView partyMembershipText = (TextView) view.findViewById(R.id.text_party_membership);
        partyMembershipUnreadImage = (ImageView) view.findViewById(R.id.image_unread_party_membership_message);
        if (unreadPartyMembershipCount > 0) {
            partyMembershipUnreadImage.setVisibility(View.VISIBLE);
        }
        partyMembershipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMessageListActivity(Message.SubCategory.PARTY_MEMBERSHIP, R.string.label_party_join);
                unreadPartyMembershipCount = 0;
                partyMembershipUnreadImage.setVisibility(View.GONE);
            }
        });

        TextView partyCommentsText = (TextView) view.findViewById(R.id.text_party_comments);
        partyCommentsUnreadImage = (ImageView) view.findViewById(R.id.image_unread_party_comments_message);
        if (unreadPartyCommentsCount > 0) {
            partyCommentsUnreadImage.setVisibility(View.VISIBLE);
        }
        partyCommentsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMessageListActivity(Message.SubCategory.PARTY_COMMENTS, R.string.label_party_comments);
                unreadPartyCommentsCount = 0;
                partyCommentsUnreadImage.setVisibility(View.GONE);
            }
        });

        TextView partyLikesText = (TextView) view.findViewById(R.id.text_party_likes);
        partyLikesUnreadImage = (ImageView) view.findViewById(R.id.image_unread_party_likes_message);
        if (unreadPartyLikesCount > 0) {
            partyLikesUnreadImage.setVisibility(View.VISIBLE);
        }
        partyLikesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMessageListActivity(Message.SubCategory.PARTY_LIKES, R.string.label_party_likes);
                unreadPartyLikesCount = 0;
                partyLikesUnreadImage.setVisibility(View.GONE);
            }
        });
    }

    private void startMessageListActivity(int category, int title) {
        final Intent intent = new Intent(getActivity(), MessageListActivity.class);
        intent.putExtra(MessageListActivity.INTENT_TITLE, title);
        intent.putExtra(MessageListActivity.INTENT_MESSAGE_TYPE, category);
        startActivity(intent);
    }

    @Override
    public void onAction() {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                unreadPartyMembershipCount = messageStore.getUnreadPartyMembershipCount();
                if (unreadPartyMembershipCount > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            partyMembershipUnreadImage.setVisibility(View.VISIBLE);
                        }
                    });
                }
                unreadPartyCommentsCount = messageStore.getUnreadPartyCommentsCount();
                if (unreadPartyCommentsCount > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            partyCommentsUnreadImage.setVisibility(View.VISIBLE);
                        }
                    });
                }
                unreadPartyLikesCount = messageStore.getUnreadPartyLikesCount();
                if (unreadPartyLikesCount > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            partyLikesUnreadImage.setVisibility(View.VISIBLE);
                        }
                    });
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }
}
