package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.async.ThrowableLoader;
import com.github.kevinsawicki.wishlist.Toaster;
import com.github.kevinsawicki.wishlist.ViewUtils;

/**
 * Created by Administrator on 5/10/2014.
 */
public abstract class LoaderFragment<E> extends Fragment
        implements LoaderManager.LoaderCallbacks<E> {
    private E data;
    private ProgressBar progressBar;
    private TextView emptyText;
    private boolean isShown;
    private boolean hasError;

    protected E getData() {
        return data;
    }

    protected void setData(E data) {
        this.data = data;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
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
        progressBar = null;
        emptyText = null;

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        emptyText = (TextView) view.findViewById(R.id.text_empty);
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
        final Exception exception = getException(loader);
        if (exception != null) {
            setHasError(true);
            showError(getErrorMessage(exception));
            show();
            return;
        } else {
            setHasError(false);
        }

        handleLoadResult(data);
        show();
    }

    @Override
    public void onLoaderReset(Loader<E> loader) {

    }

    protected void refresh(final Bundle args) {
        if (!isUsable()) {
            return;
        }
        getLoaderManager().restartLoader(0, args, this);
    }

    protected void showError(final int message) {
        Toaster.showShort(getActivity(), message);
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

    protected void hideEmpty() {
        hide(emptyText);
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
                    if (hasError) {
                        hide(emptyText).hide(mainView);
                    } else {
                        hide(mainView).show(emptyText);
                    }
                } else {
                    show(mainView);
                }
            }
            return;
        }

        isShown = shown;

        if (shown) {
            if (readyToShow()) {
                hide(progressBar).hide(emptyText).fadeIn(mainView, animate).show(mainView);
            } else {
                if (hasError) {
                    hide(progressBar).hide(emptyText).hide(mainView);
                } else {
                    hide(progressBar).hide(mainView).show(emptyText);
                }
            }
        } else {
            hide(emptyText).hide(mainView).fadeIn(progressBar, animate).show(progressBar);
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
        if (view != null) {
            ViewUtils.setGone(view, false);
        }
        return this;
    }

    private LoaderFragment<E> hide(final View view) {
        if (view != null) {
            ViewUtils.setGone(view, true);
        }
        return this;
    }

    protected abstract int getErrorMessage(final Exception exception);

    protected abstract boolean readyToShow();

    protected abstract View getMainView();

    protected abstract E loadDataCore(final Bundle bundle) throws Exception;

    protected abstract void handleLoadResult(E result);
}
