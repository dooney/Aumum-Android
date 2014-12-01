package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.events.AddAskingReplyEvent;
import com.aumum.app.mobile.events.AddAskingReplyFinishedEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.QuickReturnScrollView;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.Ln;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingDetailsFragment extends LoaderFragment<Asking>
        implements QuickReturnScrollView.OnScrollDirectionListener {

    @Inject UserStore userStore;
    @Inject AskingStore askingStore;
    @Inject Bus bus;

    private String askingId;

    private QuickReturnScrollView scrollView;
    private View mainView;
    private TextView questionText;
    private TextView userNameText;
    private TextView areaText;
    private TextView createdAtText;

    private ViewGroup layoutAction;
    private ViewGroup layoutReplyBox;
    private TextView replyText;
    private boolean isReplyBoxShow;
    private EditText editReply;
    private ImageView postReplyButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        askingId = intent.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking_details, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = (QuickReturnScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOnScrollDirectionListener(this);

        mainView = view.findViewById(R.id.main_view);
        questionText = (TextView) view.findViewById(R.id.text_question);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        areaText = (TextView) view.findViewById(R.id.text_area);
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);

        layoutAction = (ViewGroup) view.findViewById(R.id.layout_action);
        layoutReplyBox = (ViewGroup) view.findViewById(R.id.layout_reply_box);
        replyText = (TextView) view.findViewById(R.id.text_reply);
        replyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReplyBox();
            }
        });
        editReply = (EditText) view.findViewById(R.id.edit_reply);
        postReplyButton = (ImageView) view.findViewById(R.id.image_post_reply);
        postReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReply();
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
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_asking;
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
    protected Asking loadDataCore(Bundle bundle) throws Exception {
        Asking asking = askingStore.getAskingByIdFromServer(askingId);
        User user = userStore.getUserById(asking.getUserId());
        asking.setUser(user);
        return asking;
    }

    @Override
    protected void handleLoadResult(Asking asking) {
        try {
            setData(asking);

            userNameText.setText(asking.getUser().getScreenName());
            userNameText.setOnClickListener(new UserListener(getActivity(), asking.getUserId()));
            areaText.setText(Constants.Options.AREA_OPTIONS[asking.getUser().getArea()]);
            questionText.setText(asking.getQuestion());
            createdAtText.setText(asking.getCreatedAtFormatted());
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onScrollUp() {
        if (!isReplyBoxShow) {
            Animation.animateIconBar(layoutAction, true);
        }
    }

    @Override
    public void onScrollDown() {
        if (isReplyBoxShow) {
            return;
        }

        boolean canScrollDown = scrollView.canScrollDown();
        boolean canScrollUp = scrollView.canScrollUp();
        if (!canScrollDown) {
            Animation.animateIconBar(layoutAction, true);
        } else if (canScrollDown && canScrollUp) {
            Animation.animateIconBar(layoutAction, false);
        }
    }

    private void toggleReplyBox() {
        if (isReplyBoxShow) {
            hideReplyBox();
        } else {
            editReply.setHint(R.string.hint_new_reply);
            showReplyBox();
        }
        isReplyBoxShow = !isReplyBoxShow;
    }

    private void showReplyBox() {
        Animation.flyIn(layoutReplyBox);
        EditTextUtils.showSoftInput(editReply, true);
    }

    private void hideReplyBox() {
        EditTextUtils.hideSoftInput(editReply);
        editReply.setText(null);
        Animation.flyOut(layoutReplyBox);
    }

    private void enableSubmit() {
        postReplyButton.setEnabled(true);
    }

    private void disableSubmit() {
        postReplyButton.setEnabled(false);
    }

    private void submitReply() {
        String answer = editReply.getText().toString();
        bus.post(new AddAskingReplyEvent(answer));

        hideReplyBox();
        disableSubmit();
    }

    @Subscribe
    public void onAddAskingReplyFinishedEvent(AddAskingReplyFinishedEvent event) {
        enableSubmit();
    }
}
