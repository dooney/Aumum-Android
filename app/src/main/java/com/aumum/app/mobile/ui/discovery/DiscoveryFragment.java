package com.aumum.app.mobile.ui.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.moment.MomentDetailsActivity;
import com.aumum.app.mobile.ui.moment.MomentGridActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.keyboard.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 9/05/2015.
 */
public class DiscoveryFragment extends Fragment {

    @Inject MomentStore momentStore;
    @Inject UserStore userStore;

    private ViewGroup latestGallery;
    private ViewGroup hottestGallery;
    private ViewGroup nearByGallery;
    private ViewGroup talentGallery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        latestGallery = (ViewGroup) view.findViewById(R.id.gallery_latest);
        hottestGallery = (ViewGroup) view.findViewById(R.id.gallery_hottest);
        nearByGallery = (ViewGroup) view.findViewById(R.id.gallery_nearby);
        talentGallery = (ViewGroup) view.findViewById(R.id.gallery_talent);

        getLatestList();
        getHottestList();
        getNearByList();
        getTalentList();
    }

    private void getLatestList() {
        List<Moment> momentList = momentStore.getLocalLatestList();
        loadMomentsGalleryView(latestGallery,
                momentList,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startMomentGridActivity(MomentGridActivity.QUERY_LATEST);
                    }
                });
    }

    private void getHottestList() {
        List<Moment> momentList = momentStore.getLocalHottestList();
        loadMomentsGalleryView(hottestGallery,
                momentList,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startMomentGridActivity(MomentGridActivity.QUERY_HOTTEST);
                    }
                });
    }

    private void getNearByList() {
        List<UserInfo> userList = userStore.getLocalNearByList();
        List<Moment> momentList = getMomentsByUserList(userList);
        loadMomentsGalleryView(nearByGallery,
                momentList,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startMomentGridActivity(MomentGridActivity.QUERY_NEARBY);
                    }
                });
    }

    private void getTalentList() {
        List<UserInfo> userList = userStore.getLocalTalentList();
        List<Moment> momentList = getMomentsByUserList(userList);
        loadMomentsGalleryView(talentGallery,
                momentList,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startMomentGridActivity(MomentGridActivity.QUERY_TALENT);
                    }
                });
    }

    private List<Moment> getMomentsByUserList(List<UserInfo> users) {
        List<String> userIds = new ArrayList<>();
        for (UserInfo user : users) {
            userIds.add(user.getObjectId());
        }
        return momentStore.getLocalListByUsers(userIds);
    }

    private View getImageView(String imageUrl) {
        ImageView imageView = new ImageView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                Utils.dip2px(getActivity(), 100),
                Utils.dip2px(getActivity(), 100));
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoaderUtils.displayImage(imageUrl, imageView);
        return imageView;
    }

    private View getNextView() {
        ImageView imageView = new ImageView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                Utils.dip2px(getActivity(), 30),
                Utils.dip2px(getActivity(), 100));
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(R.drawable.next);
        return imageView;
    }

    private void loadMomentsGalleryView(ViewGroup view,
                                        List<Moment> momentList,
                                        View.OnClickListener nextClickListener) {
        view.removeAllViews();
        for (final Moment moment: momentList) {
            View image = getImageView(moment.getImageUrl());
            view.addView(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(getActivity(),
                            MomentDetailsActivity.class);
                    intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID,
                            moment.getObjectId());
                    startActivity(intent);
                }
            });
        }
        View nextView = getNextView();
        nextView.setOnClickListener(nextClickListener);
        view.addView(nextView);
    }

    private void startMomentGridActivity(int query) {
        final Intent intent = new Intent(getActivity(), MomentGridActivity.class);
        intent.putExtra(MomentGridActivity.INTENT_QUERY, query);
        startActivity(intent);
    }
}