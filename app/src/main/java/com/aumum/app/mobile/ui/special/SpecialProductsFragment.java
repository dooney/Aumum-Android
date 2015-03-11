package com.aumum.app.mobile.ui.special;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemGridFragment;
import com.aumum.app.mobile.ui.view.ListViewDialog;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialProductsFragment extends ItemGridFragment<SpecialProduct> {

    @Inject ApiKeyProvider apiKeyProvider;
    @Inject RestService restService;

    private String specialId;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        specialId = intent.getStringExtra(SpecialDetailsActivity.INTENT_SPECIAL_ID);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showActionDialog();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_special_products, null);
    }

    @Override
    protected ArrayAdapter<SpecialProduct> createAdapter(List<SpecialProduct> items) {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return new SpecialProductsAdapter(getActivity(), items, currentUserId);
    }

    @Override
    protected List<SpecialProduct> loadDataCore(Bundle bundle) throws Exception {
        return restService.getSpecialProductList(specialId);
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_special_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startMyFavoritesActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startMyFavoritesActivity() {
        final Intent intent = new Intent(getActivity(), FavoriteProductsActivity.class);
        intent.putExtra(FavoriteProductsActivity.INTENT_SPECIAL_ID, specialId);
        startActivity(intent);
    }
}
