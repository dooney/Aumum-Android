package com.aumum.app.mobile.ui.moment;

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
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.TuSdkUtils;

import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 24/04/2015.
 */
public class MomentFragment extends RefreshItemListFragment<Moment>
    implements TuSdkUtils.CameraListener,
               TuSdkUtils.AlbumListener,
               TuSdkUtils.EditListener {

    @Inject MomentStore momentStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem camera = menu.add(Menu.NONE, 0, Menu.NONE, null);
        camera.setActionView(R.layout.menuitem_camera);
        camera.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View cameraView = camera.getActionView();
        ImageView plusIcon = (ImageView) cameraView.findViewById(R.id.b_camera);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showCameraOptions();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moment, null);
    }

    @Override
    protected ArrayAdapter<Moment> createAdapter(List<Moment> items) {
        return new MomentCardsAdapter(getActivity(), items);
    }

    @Override
    protected List<Moment> refresh(String after) {
        return null;
    }

    @Override
    protected List<Moment> loadMore(String before) {
        return null;
    }

    private void showCameraOptions() {
        String options[] = getResources().getStringArray(R.array.label_camera_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                TuSdkUtils.camera(getActivity(), MomentFragment.this);
                                break;
                            case 1:
                                TuSdkUtils.album(getActivity(), MomentFragment.this);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    @Override
    public void onCameraResult(ImageSqlInfo imageSqlInfo) {
        TuSdkUtils.edit(getActivity(), imageSqlInfo, false, true, true, this);
    }

    @Override
    public void onAlbumResult(ImageSqlInfo imageSqlInfo) {
        TuSdkUtils.edit(getActivity(), imageSqlInfo, false, true, true, this);
    }

    @Override
    public void onEditResult(File file) {

    }
}
