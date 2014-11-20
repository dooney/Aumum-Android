package com.aumum.app.mobile.ui.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMContact;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CircleFragment extends ItemListFragment<EMContact> {

    @Inject ChatService chatService;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.info_no_circles);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        SubMenu search = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.hint_search_circle));
        MenuItem item = search.getItem();
        item.setIcon(R.drawable.ic_fa_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        String searchOptions[] = getResources().getStringArray(R.array.label_search_circle);
        DialogUtils.showDialog(getActivity(), searchOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        final Intent intent = new Intent(getActivity(), SearchCircleActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        });
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_circle, null);
    }

    @Override
    protected ArrayAdapter<EMContact> createAdapter(List<EMContact> items) {
        return new ConversationsAdapter(getActivity(), items, chatService);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_circles;
    }

    @Override
    protected List<EMContact> loadDataCore(Bundle bundle) throws Exception {
        return chatService.getAllContacts();
    }

    @Override
    protected void handleLoadResult(List<EMContact> result) {
        try {
            if (result != null) {
                getData().clear();
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }
}
