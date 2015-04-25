package com.aumum.app.mobile.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.user.UserPickerFragment;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 26/03/2015.
 */
public class ContactPickerFragment extends UserPickerFragment {

    @Inject UserStore userStore;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_picker, null);
    }

    @Override
    protected List<UserInfo> loadDataCore(Bundle bundle) throws Exception {
        List<UserInfo> contacts = userStore.getContacts();
        Collections.sort(contacts, initialComparator);
        return contacts;
    }
}
