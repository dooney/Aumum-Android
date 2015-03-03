package com.aumum.app.mobile.ui.moment;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.List;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentCommentsFragment extends ItemListFragment<MomentComment> {

    private Moment moment;

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    @Override
    protected ArrayAdapter<MomentComment> createAdapter(List<MomentComment> items) {
        return null;
    }

    @Override
    protected List<MomentComment> loadDataCore(Bundle bundle) throws Exception {
        return null;
    }
}
