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
import com.aumum.app.mobile.ui.user.UserActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.keyboard.utils.Utils;

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
    private ViewGroup nearestGallery;
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
        nearestGallery = (ViewGroup) view.findViewById(R.id.gallery_nearest);
        talentGallery = (ViewGroup) view.findViewById(R.id.gallery_talent);

        getLatestList();
        getHottestList();
        getNearestList();
        getTalentList();
    }

    private void getLatestList() {
        List<Moment> momentList = momentStore.getLocalLatestList();
        loadMomentsGalleryView(latestGallery, momentList);
    }

    private void getHottestList() {
        List<Moment> momentList = momentStore.getLocalHottestList();
        loadMomentsGalleryView(hottestGallery, momentList);
    }

    private void getNearestList() {
        List<UserInfo> userList = userStore.getLocalNearestList();
        loadUsersGalleryView(nearestGallery, userList);
    }

    private void getTalentList() {
        List<UserInfo> userList = userStore.getLocalTalentList();
        loadUsersGalleryView(talentGallery, userList);
    }

    private View getImageView(String imageUrl) {
        ImageView imageView = new ImageView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                Utils.dip2px(getActivity(), 100),
                Utils.dip2px(getActivity(), 100));
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
                                        List<Moment> momentList) {
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
        view.addView(getNextView());
    }

    private void loadUsersGalleryView(ViewGroup view,
                                      List<UserInfo> userList) {
        view.removeAllViews();
        for (final UserInfo user: userList) {
            View image = getImageView(user.getAvatarUrl());
            view.addView(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(getActivity(),
                            UserActivity.class);
                    intent.putExtra(UserActivity.INTENT_USER_ID,
                            user.getObjectId());
                    startActivity(intent);
                }
            });
        }
        view.addView(getNextView());
    }
}