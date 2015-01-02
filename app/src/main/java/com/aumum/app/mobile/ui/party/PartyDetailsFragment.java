package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.events.AddPartyReasonEvent;
import com.aumum.app.mobile.events.AddPartyReasonFinishedEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.base.ProgressListener;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.UpYunUtils;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 16/10/2014.
 */
public class PartyDetailsFragment extends LoaderFragment<Party> {
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject Bus bus;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;
    @Inject RestService restService;
    @Inject ChatService chatService;
    private ShareService shareService;

    private Party party;
    private String partyId;
    private String currentUserId;

    private GPSTracker gpsTracker;

    private ScrollView scrollView;
    private View mainView;
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private SpannableTextView titleText;
    private TextView distanceText;
    private TextView cityText;
    private TextView createdAtText;
    private TextView timeText;
    private TextView addressText;
    private SpannableTextView detailsText;
    private GridView gridGallery;
    private ViewGroup membersLayout;
    private ViewGroup likesLayout;

    private ViewGroup actionLayout;
    private ViewGroup joinBoxLayout;
    private JoinTextView joinText;
    private TextView commentText;
    private LikeTextView likeText;
    private FavoriteTextView favoriteText;
    private EditText editReason;
    private Button postReasonButton;
    private boolean isJoinBoxShow;

    private MembersLayoutListener membersLayoutListener;
    private LikesLayoutListener likesLayoutListener;

    GalleryAdapter adapter;
    private SafeAsyncTask<Boolean> task;
    private ProgressListener progressListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
        currentUserId = apiKeyProvider.getAuthUserId();
        membersLayoutListener = new MembersLayoutListener(getActivity(), currentUserId);
        likesLayoutListener = new LikesLayoutListener(getActivity(), currentUserId);
        gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
        shareService = new ShareService(getActivity());
        progressListener = (ProgressListener) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable() || party == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog(party.isOwner(currentUserId));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_details, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        mainView = view.findViewById(R.id.main_view);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        titleText = (SpannableTextView) view.findViewById(R.id.text_title);
        distanceText = (TextView) view.findViewById(R.id.text_distance);
        cityText = (TextView) view.findViewById(R.id.text_city);
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        timeText = (TextView) view.findViewById(R.id.text_time);
        addressText = (TextView) view.findViewById(R.id.text_address);
        detailsText = (SpannableTextView) view.findViewById(R.id.text_details);

        adapter = new GalleryAdapter(getActivity(), R.layout.image_collection_listitem_inner, ImageLoaderUtils.getInstance());
        gridGallery = (GridView) view.findViewById(R.id.grid_gallery);
        gridGallery.setAdapter(adapter);

        membersLayout = (ViewGroup) view.findViewById(R.id.layout_members);
        likesLayout = (ViewGroup) view.findViewById(R.id.layout_likes);

        actionLayout = (ViewGroup) view.findViewById(R.id.layout_action);
        joinBoxLayout = (ViewGroup) view.findViewById(R.id.layout_join_box);
        joinText = (JoinTextView) view.findViewById(R.id.text_join);
        joinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleJoinBox();
            }
        });
        commentText = (TextView) view.findViewById(R.id.text_comment);

        likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);

        favoriteText = (FavoriteTextView) view.findViewById(R.id.text_favorite);
        favoriteText.setFavoriteResId(R.drawable.ic_fa_star_o);
        favoriteText.setFavoritedResId(R.drawable.ic_fa_star);

        editReason = (EditText) view.findViewById(R.id.edit_reason);
        postReasonButton = (Button) view.findViewById(R.id.b_post_reason);
        postReasonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        mainView = null;

        super.onDestroyView();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.GET_PARTY_COMMENTS_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String partyId = data.getStringExtra(PartyCommentsActivity.INTENT_PARTY_ID);
            onPartyRefresh(partyId);
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_party_details;
    }

    @Override
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected Party loadDataCore(Bundle bundle) throws Exception {
        party = partyStore.getPartyByIdFromServer(partyId);
        return party;
    }

    @Override
    protected void handleLoadResult(final Party party) {
        try {
            if (party != null) {
                setData(party);
                updateParty(party);
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void updateParty(Party party) throws Exception {
        User user = userStore.getUserById(party.getUserId());
        avatarImage.getFromUrl(user.getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

        userNameText.setText(user.getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));
        titleText.setSpannableText(party.getTitle());

        gpsTracker.getLocation();
        party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        if (party.isNearBy()) {
            distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_walk, 0, 0, 0);
            distanceText.setTextColor(getResources().getColor(R.color.bbutton_danger));
        } else {
            distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_car, 0, 0, 0);
            distanceText.setTextColor(getResources().getColor(R.color.text_light));
        }
        distanceText.setText(getString(R.string.label_distance, party.getDistance()));

        cityText.setText(user.getCity());
        createdAtText.setText(party.getCreatedAtFormatted());
        timeText.setText(party.getDateTimeText());
        addressText.setText(party.getAddress());
        if (party.getDetails() != null && party.getDetails().length() > 0) {
            detailsText.setSpannableText(party.getDetails());
        } else {
            detailsText.setVisibility(View.GONE);
        }

        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String imageUrl: party.getImages()) {
            CustomGallery item = new CustomGallery();
            item.type = CustomGallery.HTTP;
            item.imageUri = UpYunUtils.getThumbnailUrl(imageUrl);
            list.add(item);
        }
        if (list.size() > 0) {
            adapter.addAll(list);
        } else {
            gridGallery.setVisibility(View.GONE);
        }

        if (!party.isExpired() && !party.isOwner(currentUserId)) {
            actionLayout.setVisibility(View.VISIBLE);
            joinText.update(party.isMember(currentUserId));
        }

        int comments = party.getCommentsCount();
        commentText.setText(comments > 0 ? String.valueOf(comments) : getString(R.string.label_comment));
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation.animateTextView(view);
                final Intent intent = new Intent(getActivity(), PartyCommentsActivity.class);
                intent.putExtra(PartyCommentsActivity.INTENT_PARTY_ID, partyId);
                startActivityForResult(intent, Constants.RequestCode.GET_PARTY_COMMENTS_REQ_CODE);
            }
        });

        likeText.init(party.getLikesCount(), party.isLiked(currentUserId));
        PartyLikeListener likeListener = new PartyLikeListener(party);
        likeListener.setOnLikeFinishedListener(new PartyLikeListener.LikeFinishedListener() {
            @Override
            public void OnLikeFinished(Party party) {
                likesLayoutListener.update(likesLayout, party.getLikes());
            }

            @Override
            public void OnUnLikeFinished(Party party) {
                likesLayoutListener.update(likesLayout, party.getLikes());
            }
        });
        likeText.setLikeListener(likeListener);

        favoriteText.init(party.getFavoritesCount(), party.isFavorited(currentUserId));
        favoriteText.setFavoriteListener(new PartyFavoriteListener(party));

        membersLayoutListener.update(membersLayout, party.getMembers());
        likesLayoutListener.update(likesLayout, party.getLikes());
    }

    private void onPartyRefresh(String partyId) {
        try {
            party = partyStore.getPartyById(partyId);
            updateParty(party);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    private void showActionDialog(final boolean isOwner) {
        List<String> options = new ArrayList<String>();
        options.add(getString(R.string.label_share));
        if (isOwner) {
            options.add(getString(R.string.label_delete));
        } else {
            options.add(getString(R.string.label_report));
        }
        new ListViewDialog(getActivity(), null, options,
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        showShare();
                        break;
                    case 1:
                        if (isOwner) {
                            deleteParty();
                        } else {
                            reportParty();
                        }
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void showShare() {
        shareService.show(getActivity());
    }

    private void deleteParty() {
        if (task != null) {
            return;
        }
        progressListener.showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteParty(party.getObjectId());
                partyStore.deleteParty(party.getObjectId());
                for(String userId: party.getMembers()) {
                    restService.removeUserParty(userId, party.getObjectId());
                }

                sendCancelMessage();
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(getActivity(), R.string.error_delete_party);
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                Toaster.showShort(getActivity(), R.string.info_party_deleted);

                final Intent intent = new Intent();
                intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, partyId);
                intent.putExtra(PartyDetailsActivity.INTENT_DELETED, true);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                progressListener.hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    private void hideJoinBox() {
        EditTextUtils.hideSoftInput(editReason);
        editReason.setText(null);
        Animation.flyOut(joinBoxLayout);
    }

    private void showJoinBox() {
        Animation.flyIn(joinBoxLayout);
        EditTextUtils.showSoftInput(editReason, true);
    }

    private void toggleJoinBox() {
        if (isJoinBoxShow) {
            hideJoinBox();
        } else {
            if (joinText.isMember()) {
                editReason.setHint(R.string.hint_quit_reason);
            } else {
                editReason.setHint(R.string.hint_join_reason);
            }
            showJoinBox();
        }
        isJoinBoxShow = !isJoinBoxShow;
    }

    private void enableSubmit() {
        postReasonButton.setEnabled(true);
    }

    private void disableSubmit() {
        postReasonButton.setEnabled(false);
    }

    private void submit() {
        String reason = editReason.getText().toString();
        if (joinText.isMember()) {
            bus.post(new AddPartyReasonEvent(PartyReason.QUIT, reason));
        } else {
            bus.post(new AddPartyReasonEvent(PartyReason.JOIN, reason));
        }

        hideJoinBox();
        disableSubmit();
    }

    @Subscribe
    public void onAddPartyReasonFinishedEvent(AddPartyReasonFinishedEvent event) {
        if (event.getType() == PartyReason.JOIN) {
            joinText.update(true);
        } else if (event.getType() == PartyReason.QUIT) {
            joinText.update(false);
        }
        membersLayoutListener.update(membersLayout, event.getParty().getMembers());
        enableSubmit();
    }

    private void sendCancelMessage() throws Exception {
        User partyOwner = userStore.getUserById(party.getUserId());
        String title = getString(R.string.label_cancel_party_message,
                partyOwner.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_CANCEL,
                title, party.getTitle(), partyId);
        for (String memberId: party.getMembers()) {
            User member = userStore.getUserById(memberId);
            if (!memberId.equals(currentUserId)) {
                chatService.sendCmdMessage(member.getChatId(), cmdMessage, false, null);
            }
        }
    }

    private void reportParty() {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_PARTY);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, partyId);
        startActivity(intent);
    }
}
