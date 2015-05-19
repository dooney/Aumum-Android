package com.aumum.app.mobile.ui.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.keyboard.utils.Utils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 9/05/2015.
 */
public class DiscoveryFragment extends Fragment {

    @Inject MomentStore momentStore;

    private ScrollView scrollView;
    private HorizontalScrollView latestScroll;
    private HorizontalScrollView hottestScroll;
    private HorizontalScrollView nearestScroll;
    private HorizontalScrollView talentScroll;
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

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        latestScroll = (HorizontalScrollView) view.findViewById(R.id.scroll_latest);
        latestScroll.setHorizontalScrollBarEnabled(false);
        latestScroll.setVerticalScrollBarEnabled(false);
        latestGallery = (ViewGroup) view.findViewById(R.id.gallery_latest);

        hottestScroll = (HorizontalScrollView) view.findViewById(R.id.scroll_hottest);
        hottestScroll.setHorizontalScrollBarEnabled(false);
        hottestScroll.setVerticalScrollBarEnabled(false);
        hottestGallery = (ViewGroup) view.findViewById(R.id.gallery_hottest);

        nearestScroll = (HorizontalScrollView) view.findViewById(R.id.scroll_nearest);
        nearestScroll.setHorizontalScrollBarEnabled(false);
        nearestScroll.setVerticalScrollBarEnabled(false);
        nearestGallery = (ViewGroup) view.findViewById(R.id.gallery_nearest);

        talentScroll = (HorizontalScrollView) view.findViewById(R.id.scroll_talent);
        talentScroll.setHorizontalScrollBarEnabled(false);
        talentScroll.setVerticalScrollBarEnabled(false);
        talentGallery = (ViewGroup) view.findViewById(R.id.gallery_talent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLatestList();
        getHottestList();
        getSameCityList();
        getTalentList();
    }

    private void getLatestList() {
        List<Moment> momentList = momentStore.getLatestList();
        for (Moment moment: momentList) {
            String imageUrl = moment.getImageUrl();
            latestGallery.addView(getImageView(imageUrl));
        }
    }

    private void getHottestList() {
        List<Moment> momentList = momentStore.getLatestList();
        for (Moment moment: momentList) {
            String imageUrl = moment.getImageUrl();
            hottestGallery.addView(getImageView(imageUrl));
        }
    }

    private void getSameCityList() {
        List<Moment> momentList = momentStore.getLatestList();
        for (Moment moment: momentList) {
            String imageUrl = moment.getImageUrl();
            nearestGallery.addView(getImageView(imageUrl));
        }
    }

    private void getTalentList() {
        List<Moment> momentList = momentStore.getLatestList();
        for (Moment moment: momentList) {
            String imageUrl = moment.getImageUrl();
            talentGallery.addView(getImageView(imageUrl));
        }
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
}