package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.TuSdkUtils;

import org.lasque.tusdk.core.utils.image.BitmapHelper;
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
               TuSdkUtils.CropListener,
               TuSdkUtils.EditListener {

    @Inject MomentStore momentStore;
    @Inject UserStore userStore;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(ImageLoaderUtils.getOnScrollListener());
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Moment moment = (Moment) adapterView.getAdapter().getItem(i);
                final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, moment.getObjectId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.NEW_MOMENT_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            autoRefresh();
        }
    }

    @Override
    protected ArrayAdapter<Moment> createAdapter(List<Moment> items) {
        return new MomentCardsAdapter(getActivity(), items);
    }

    @Override
    protected List<Moment> refresh(String after) throws Exception {
        List<Moment> momentList = momentStore.refresh(after);
        loadUserInfo(momentList);
        return momentList;
    }

    @Override
    protected List<Moment> loadMore(String before) throws Exception {
        List<Moment> momentList = momentStore.loadMore(before);
        loadUserInfo(momentList);
        return momentList;
    }

    private void loadUserInfo(List<Moment> momentList) throws Exception {
        User currentUser = userStore.getCurrentUser();
        for (Moment moment: momentList) {
            UserInfo user = userStore.getUserInfoById(moment.getUserId());
            moment.setUser(user);
            moment.setOwner(currentUser.getObjectId());
            moment.setLiked(currentUser.getObjectId());
            List<UserInfo> users = userStore.getUserInfoList(moment.getLikes());
            moment.setLikesInfo(users);
        }
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

    private void onPhotoResult(ImageSqlInfo imageSqlInfo) {
        Bitmap bitmap = BitmapHelper.getBitmap(imageSqlInfo);
        if (bitmap.getHeight() > bitmap.getWidth()) {
            TuSdkUtils.crop(getActivity(), imageSqlInfo, false, this);
        } else {
            TuSdkUtils.edit(getActivity(), bitmap, true, true, this);
        }
    }

    @Override
    public void onCameraResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
    }

    @Override
    public void onAlbumResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
    }

    @Override
    public void onCropResult(File file) {
        Bitmap bitmap = BitmapHelper.getBitmap(file);
        TuSdkUtils.edit(getActivity(), bitmap, true, true, this);
    }

    @Override
    public void onEditResult(File file) {
        String localUri = file.getAbsolutePath();
        final Intent intent = new Intent(getActivity(), NewMomentActivity.class);
        intent.putExtra(NewMomentActivity.INTENT_IMAGE_URI, localUri);
        startActivityForResult(intent, Constants.RequestCode.NEW_MOMENT_REQ_CODE);
    }
}
