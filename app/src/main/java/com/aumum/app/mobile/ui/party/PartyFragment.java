package com.aumum.app.mobile.ui.party;

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
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Place;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.GooglePlaceUtils;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyFragment extends Fragment {

    @Inject ApiKeyProvider apiKeyProvider;

    @InjectView(R.id.tpi_header)protected PartyTabPageIndicator indicator;
    @InjectView(R.id.vp_pages)protected ViewPager pager;

    private PagerAdapter pagerAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party, null);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem search = menu.add(Menu.NONE, 0, Menu.NONE, null);
        search.setActionView(R.layout.menuitem_search);
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View searchView = search.getActionView();
        ImageView searchIcon = (ImageView) searchView.findViewById(R.id.b_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchPartyDialog();
            }
        });

        MenuItem more = menu.add(Menu.NONE, 1, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionDialog();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.inject(this, getView());
        pagerAdapter = new PagerAdapter(getResources(), getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
        indicator.setViewPager(pager);
    }

    private void showSearchPartyDialog() {
        final String options[] = getResources().getStringArray(R.array.label_search_party);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        String title = options[i];
                        switch (i) {
                            case 0:
                                startSearchPartyActivity(title);
                                break;
                            case 1:
                                showSearchAddressDialog(title);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startSearchPartyActivity(String title) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, title);
        intent.putExtra(SearchPartyActivity.INTENT_NEARBY_PARTIES, true);
        startActivity(intent);
    }

    private void showSearchAddressDialog(final String title) {
        EditTextDialog dialog = new EditTextDialog(getActivity(),
                R.layout.dialog_edit_text_multiline,
                R.string.hint_search_address,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        String address = (String) value;
                        final Place place = GooglePlaceUtils.getPlace(address);
                        if (place == null) {
                            throw new Exception(getString(R.string.error_invalid_party_address, address));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startSearchPartyActivity(title, place);
                            }
                        });
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                    }
                });
        dialog.getValueText().setAdapter(new PlacesAutoCompleteAdapter(getActivity(),
                R.layout.place_autocomplete_listitem));
        dialog.show();
    }

    private void showActionDialog() {
        final String options[] = getResources().getStringArray(R.array.label_party_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startNewPartyRequestActivity();
                                break;
                            case 1:
                                startNewPartyActivity();
                                break;
                            case 2:
                                showMyPartiesDialog();
                                break;
                            case 3:
                                startMyFavoritesActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startSearchPartyActivity(String title, Place place) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, title);
        intent.putExtra(SearchPartyActivity.INTENT_LOCATION_NEARBY_PARTIES, true);
        intent.putExtra(SearchPartyActivity.INTENT_LOCATION_LAT, place.getLatitude());
        intent.putExtra(SearchPartyActivity.INTENT_LOCATION_LNG, place.getLongitude());
        startActivity(intent);
    }

    private void showMyPartiesDialog() {
        final String options[] = getResources().getStringArray(R.array.label_my_parties);
        new ListViewDialog(getActivity(),
                getString(R.string.label_select_party_view),
                Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startPartyCalendarActivity();
                                break;
                            case 1:
                                startMyPartiesActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startNewPartyRequestActivity() {
        final Intent intent = new Intent(getActivity(), NewPartyRequestActivity.class);
        startActivityForResult(intent, Constants.RequestCode.NEW_PARTY_REQUEST_REQ_CODE);
    }

    private void startNewPartyActivity() {
        final Intent intent = new Intent(getActivity(), NewPartyActivity.class);
        startActivityForResult(intent, Constants.RequestCode.NEW_PARTY_REQ_CODE);
    }

    private void startPartyCalendarActivity() {
        final Intent intent = new Intent(getActivity(), PartyCalendarActivity.class);
        intent.putExtra(PartyCalendarActivity.INTENT_TITLE, getString(R.string.label_my_parties));
        startActivity(intent);
    }

    private void startMyPartiesActivity() {
        final String userId = apiKeyProvider.getAuthUserId();
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, getString(R.string.label_my_parties));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
        startActivity(intent);
    }

    private void startMyFavoritesActivity() {
        final String userId = apiKeyProvider.getAuthUserId();
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, getString(R.string.label_favorite_parties));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
        intent.putExtra(SearchPartyActivity.INTENT_IS_FAVORITE, true);
        startActivity(intent);
    }
}
