package com.aumum.app.mobile.ui.special;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemGridFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 11/03/2015.
 */
public class FavoriteProductsFragment extends ItemGridFragment<SpecialProduct> {

    @Inject ApiKeyProvider apiKeyProvider;
    @Inject RestService restService;
    @Inject UserStore userStore;

    private String specialId;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        specialId = intent.getStringExtra(FavoriteProductsActivity.INTENT_SPECIAL_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_products, null);
    }

    @Override
    protected ArrayAdapter<SpecialProduct> createAdapter(List<SpecialProduct> items) {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return new SpecialProductsAdapter(getActivity(), items, currentUserId);
    }

    @Override
    protected List<SpecialProduct> loadDataCore(Bundle bundle) throws Exception {
        User currentUser = userStore.getCurrentUser();
        return restService.getFavoriteProductList(currentUser.getFavSpecials(), specialId);
    }
}
