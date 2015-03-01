package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.UserTag;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserTagListFragment extends ItemListFragment<UserTag>
        implements UserTagClickListener {

    @Inject RestService restService;

    private ArrayList<String> userTags;
    private final int MAX_COUNT = 3;

    private Button confirmButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        userTags = new ArrayList<String>();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_ok);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        confirmButton = (Button) view.findViewById(R.id.b_ok);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putStringArrayListExtra(UserTagListActivity.INTENT_USER_TAGS, userTags);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_tag_list, null);
    }

    @Override
    protected ArrayAdapter<UserTag> createAdapter(List<UserTag> items) {
        return new UserTagsAdapter(getActivity(), items, this);
    }

    @Override
    protected List<UserTag> loadDataCore(Bundle bundle) throws Exception {
        return restService.getUserTags();
    }

    @Override
    public boolean onUserTagClick(String name) {
        if (userTags.contains(name)) {
            userTags.remove(name);
        } else {
            if (userTags.size() >= MAX_COUNT) {
                Toaster.showShort(getActivity(),
                        getString(R.string.error_selection_no_more_than, MAX_COUNT));
                return false;
            }
            userTags.add(name);
        }
        return true;
    }

    @Override
    public boolean isSelected(String name) {
        return userTags.contains(name);
    }
}