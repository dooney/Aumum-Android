package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.ThrowableLoader;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;
import com.github.kevinsawicki.wishlist.ViewUtils;

/**
 * Created by Administrator on 5/10/2014.
 */
public abstract class LoaderFragment<E> extends Fragment
        implements LoaderManager.LoaderCallbacks<E> {
    private E data;
    private TextView emptyView;
    private ProgressBar progressBar;
    private boolean isShown;

    protected E getData() {
        return data;
    }

    protected void setData(E data) {
        this.data = data;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (readyToShow()) {
            setShown(true, false);
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroyView() {
        isShown = false;
        emptyView = null;
        progressBar = null;

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        emptyView = (TextView) view.findViewById(android.R.id.empty);
    }

    @Override
    public Loader<E> onCreateLoader(int i, final Bundle bundle) {
        return new ThrowableLoader<E>(getActivity(), data) {
            @Override
            public E loadData() throws Exception {
                return loadDataCore(bundle);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        getActionBarActivity().setSupportProgressBarIndeterminateVisibility(false);

        final Exception exception = getException(loader);
        if (exception != null) {
            showError(getErrorMessage(exception));
            show();
            return;
        }

        try {
            handleLoadResult(data);
        } catch (Exception e) {
            Ln.d(e);
        }
        show();
    }

    @Override
    public void onLoaderReset(Loader<E> loader) {

    }

    protected void refresh(final Bundle args) {
        if (!isUsable()) {
            return;
        }
        getActionBarActivity().setSupportProgressBarIndeterminateVisibility(true);
        getLoaderManager().restartLoader(0, args, this);
    }

    private ActionBarActivity getActionBarActivity() {
        return ((ActionBarActivity) getActivity());
    }

    protected void showError(final int message) {
        Toaster.showLong(getActivity(), message);
    }

    private Exception getException(final Loader<E> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<E>) loader).clearException();
        } else {
            return null;
        }
    }

    protected void show() {
        setShown(true, isResumed());
    }

    private void setShown(final boolean shown, final boolean animate) {
        if (!isUsable()) {
            return;
        }

        View mainView = getMainView();
        if (shown == isShown) {
            if (shown) {
                // List has already been shown so hide/show the empty view with
                // no fade effect
                if (!readyToShow()) {
                    hide(mainView).show(emptyView);
                } else {
                    hide(emptyView).show(mainView);
                }
            }
            return;
        }

        isShown = shown;

        if (shown) {
            if (readyToShow()) {
                hide(progressBar).hide(emptyView).fadeIn(mainView, animate)
                        .show(mainView);
            } else {
                hide(progressBar).hide(mainView).fadeIn(emptyView, animate)
                        .show(emptyView);
            }
        } else {
            hide(mainView).hide(emptyView).fadeIn(progressBar, animate)
                    .show(progressBar);
        }
        return;
    }

    protected boolean isUsable() {
        return getActivity() != null;
    }

    private LoaderFragment<E> fadeIn(final View view, final boolean animate) {
        if (view != null) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
        }
        return this;
    }

    private LoaderFragment<E> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    private LoaderFragment<E> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    protected LoaderFragment<E> setEmptyText(final int resId) {
        if (emptyView != null) {
            emptyView.setText(resId);
        }
        return this;
    }

    protected abstract int getErrorMessage(final Exception exception);

    protected abstract boolean readyToShow();

    protected abstract View getMainView();

    protected abstract E loadDataCore(final Bundle bundle) throws Exception;

    protected abstract void handleLoadResult(E result) throws Exception;
}
