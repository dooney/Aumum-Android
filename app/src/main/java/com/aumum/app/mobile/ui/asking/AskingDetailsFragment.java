package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.view.QuickReturnScrollView;
import com.aumum.app.mobile.utils.Ln;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingDetailsFragment extends LoaderFragment<Asking>
        implements QuickReturnScrollView.OnScrollDirectionListener {

    @Inject UserStore userStore;
    @Inject AskingStore askingStore;

    private String askingId;

    private QuickReturnScrollView scrollView;
    private View mainView;
    private TextView questionText;
    private TextView userNameText;
    private TextView createdAtText;
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
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
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
        Asking asking = askingStore.getById(askingId);
        User user = userStore.getUserById(asking.getUserId());
        asking.setUser(user);
        return asking;
    }

    @Override
    protected void handleLoadResult(Asking asking) {
        try {
            setData(asking);

            userNameText.setText(asking.getUser().getScreenName());
            questionText.setText(asking.getQuestion());
            createdAtText.setText(asking.getCreatedAtFormatted());
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onScrollUp() {

    }

    @Override
    public void onScrollDown() {

    }
}
