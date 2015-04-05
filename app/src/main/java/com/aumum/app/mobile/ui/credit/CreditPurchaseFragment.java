package com.aumum.app.mobile.ui.credit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CreditGift;
import com.aumum.app.mobile.core.model.CreditOrder;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 28/03/2015.
 */
public class CreditPurchaseFragment extends ItemListFragment<CreditGift>
        implements CreditGiftClickListener {

    @Inject UserStore userStore;
    @Inject RestService restService;

    private User currentUser;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_purchase, null);
    }

    @Override
    protected ArrayAdapter<CreditGift> createAdapter(List<CreditGift> items) {
        return new CreditGiftsAdapter(getActivity(), items, this);
    }

    @Override
    protected List<CreditGift> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        return restService.getCreditGiftList();
    }

    @Override
    public void onClick(CreditGift creditGift) {
        if (currentUser.getCredit() < creditGift.getCost()) {
            Toaster.showShort(getActivity(), R.string.info_insufficient_credit);
        } else if (!currentUser.getUsername().startsWith(creditGift.getCountry())) {
            Toaster.showShort(getActivity(), R.string.info_not_supported_by_your_country);
        } else {
            showDeliveryDetailsDialog(creditGift);
        }
    }

    private void showDeliveryDetailsDialog(final CreditGift creditGift) {
        new EditTextDialog(getActivity(),
                R.layout.dialog_edit_text_multiline,
                R.string.hint_delivery_details,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        currentUser = userStore.getCurrentUserFromServer();
                        int cost = creditGift.getCost();
                        int currentCredit = currentUser.getCredit();
                        if (currentCredit < Math.abs(cost)) {
                            throw new Exception(getString(R.string.info_insufficient_credit));
                        }
                        restService.updateUserCredit(currentUser.getObjectId(), cost);
                        currentUser.updateCredit(cost);
                        userStore.save(currentUser);
                        String deliveryDetails = (String) value;
                        CreditOrder creditOrder = new CreditOrder(currentUser.getObjectId(),
                                creditGift.getObjectId(), deliveryDetails, creditGift.getCost());
                        restService.newCreditOrder(creditOrder);
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        Toaster.showShort(getActivity(), R.string.info_gift_purchase_submitted);
                        final Intent intent = new Intent();
                        intent.putExtra(CreditPurchaseActivity.INTENT_CURRENT_CREDIT, currentUser.getCredit());
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                }).show();
    }
}
