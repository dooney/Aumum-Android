package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.ui.view.ListViewDialog;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingFragment extends Fragment {

    @Inject ApiKeyProvider apiKeyProvider;

    @InjectView(R.id.tpi_header)protected AskingTabPageIndicator indicator;
    @InjectView(R.id.vp_pages)protected ViewPager pager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (getActivity() == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.inject(this, getView());
        pager.setAdapter(new PagerAdapter(getResources(), getChildFragmentManager()));
        indicator.setViewPager(pager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.NEW_ASKING_REQ_CODE && resultCode == Activity.RESULT_OK) {
            pager.setTag(pager.getCurrentItem());
        }
    }

    private void showActionDialog() {
        final String options[] = getResources().getStringArray(R.array.label_asking_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startNewAskingActivity();
                                break;
                            case 1:
                                startMyAskingsActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startNewAskingActivity() {
        final Intent intent = new Intent(getActivity(), NewAskingActivity.class);
        intent.putExtra(NewAskingActivity.INTENT_CATEGORY, pager.getCurrentItem());
        startActivityForResult(intent, Constants.RequestCode.NEW_ASKING_REQ_CODE);
    }

    private void startMyAskingsActivity() {
        String currentUserId = apiKeyProvider.getAuthUserId();
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE, getString(R.string.label_my_askings));
        intent.putExtra(SearchAskingActivity.INTENT_USER_ID, currentUserId);
        startActivity(intent);
    }
}