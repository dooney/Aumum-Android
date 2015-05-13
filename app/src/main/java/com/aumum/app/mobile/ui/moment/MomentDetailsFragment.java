package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.model.Share;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Emoticons.EmoticonsUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ShareUtils;
import com.keyboard.XhsEmoticonsSendBoxBar;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 11/05/2015.
 */
public class MomentDetailsFragment extends ItemListFragment<MomentComment>
    implements XhsEmoticonsSendBoxBar.KeyBoardBarViewListener {

    @Inject MomentStore momentStore;
    @Inject UserStore userStore;

    private Moment moment;
    private String momentId;

    private View headerView;
    private XhsEmoticonsSendBoxBar emoticonsSendBoxBar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        momentId = intent.getStringExtra(MomentDetailsActivity.INTENT_MOMENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moment_details, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        headerView = inflater.inflate(R.layout.listview_moment_header, null);
        listView.addHeaderView(headerView, null, false);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        emoticonsSendBoxBar.hideAutoView();
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }
        });

        EmoticonsUtils.initEmoticonsDB(getActivity());
        emoticonsSendBoxBar = (XhsEmoticonsSendBoxBar) view.findViewById(R.id.kv_bar);
        emoticonsSendBoxBar.setBuilder(EmoticonsUtils.getBuilder(getActivity()));
        emoticonsSendBoxBar.setOnKeyBoardBarViewListener(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected ArrayAdapter<MomentComment> createAdapter(List<MomentComment> items) {
        return null;
    }

    @Override
    protected List<MomentComment> loadDataCore(Bundle bundle) throws Exception {
        moment = momentStore.getById(momentId);
        User currentUser = userStore.getCurrentUser();
        UserInfo user = userStore.getUserInfoById(moment.getUserId());
        moment.setUser(user);
        moment.setOwner(currentUser.getObjectId());
        moment.setLiked(currentUser.getObjectId());
        List<UserInfo> users = userStore.getUserInfoList(moment.getLikes());
        moment.setLikesInfo(users);
        return momentStore.getComments(momentId);
    }

    @Override
    protected void handleLoadResult(List<MomentComment> result) {
        final UserInfo user = moment.getUser();

        UserListener userListener = new UserListener(getActivity(), user.getObjectId());
        AvatarImageView avatarImage =
                (AvatarImageView) headerView.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());
        avatarImage.setOnClickListener(userListener);

        TextView userNameText = (TextView) headerView.findViewById(R.id.text_user_name);
        userNameText.setText(user.getScreenName());
        userNameText.setOnClickListener(userListener);

        TextView createdAtText = (TextView) headerView.findViewById(R.id.text_createdAt);
        createdAtText.setText(moment.getCreatedAtFormatted());

        ImageView imageView = (ImageView) headerView.findViewById(R.id.image);
        ImageLoaderUtils.displayImage(moment.getImageUrl(), imageView);

        TextView textView = (TextView) headerView.findViewById(R.id.text);
        textView.setText(moment.getText());

        LikeTextView likeText = (LikeTextView) headerView.findViewById(R.id.text_like);
        likeText.init(moment.isLiked());
        MomentLikeListener likeListener = new MomentLikeListener(getActivity(), moment);
        ViewGroup likesLayout = (ViewGroup) headerView.findViewById(R.id.layout_likes);
        likeListener.updateLikesLayout(likesLayout, moment.getLikesInfo());
        likeText.setLikeListener(likeListener);

        TextView chatText = (TextView) headerView.findViewById(R.id.text_chat);
        if (moment.isOwner()) {
            chatText.setVisibility(View.GONE);
        } else {
            chatText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(ChatActivity.INTENT_TITLE, user.getScreenName());
                    intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
                    intent.putExtra(ChatActivity.INTENT_ID, user.getChatId());
                    startActivity(intent);
                }
            });
            chatText.setVisibility(View.VISIBLE);
        }

        TextView shareText = (TextView) headerView.findViewById(R.id.text_share);
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = getString(R.string.label_share_content);
                Share share = new Share(moment.getText(), content, moment.getImageUrl());
                ShareUtils.show(getActivity(), share);
            }
        });

        super.handleLoadResult(result);
    }

    @Override
    protected boolean readyToShow() {
        return true;
    }

    @Override
    public void OnKeyBoardStateChange(int state, int height) {

    }

    @Override
    public void OnSendBtnClick(String msg) {

    }
}
