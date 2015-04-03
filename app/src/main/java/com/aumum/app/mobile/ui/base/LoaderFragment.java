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
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 5/10/2014.
 */
public abstract class LoaderFragment<E> extends Fragment
        implements LoaderManager.LoaderCallbacks<E> {
    protected E data;
    protected ProgressBar progressBar;
    protected TextView emptyText;
    protected TextView reloadText;
    protected boolean isShown;
    protected boolean hasError;

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
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        emptyText = (TextView) view.findViewById(R.id.text_empty);
        reloadText = (TextView) view.findViewById(R.id.text_reload);
        if (reloadText != null) {
            reloadText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reload();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getFragmentName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getFragmentName());
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

    protected void reload() {
        hide(emptyText).hide(reloadText).show(progressBar);
        refresh(null);
    }

    protected void refresh(final Bundle args) {
        if (getActivity() == null) {
            return;
        }
        getLoaderManager().restartLoader(0, args, this);
    }

    protected void showError(final String message) {
        if (message != null) {
            Toaster.showShort(getActivity(), message);
        }
    }

    protected String getErrorMessage(final Exception e) {
        final Throwable cause = e.getCause() != null ? e.getCause() : e;
        if(cause != null) {
            return(cause.getMessage());
        }
        return null;
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
        if (getActivity() == null) {
            return;
        }

        View mainView = getMainView();
        if (shown == isShown) {
            if (shown) {
                // List has already been shown so hide/show the empty view with
                // no fade effect
                if (!readyToShow()) {
                    if (hasError) {
                        hide(progressBar).hide(emptyText).hide(mainView).show(reloadText);
                    } else {
                        hide(progressBar).hide(reloadText).hide(mainView).show(emptyText);
                    }
                } else {
                    hide(progressBar).hide(emptyText).hide(reloadText).show(mainView);
                }
            }
            return;
        }

        isShown = shown;

        if (shown) {
            if (readyToShow()) {
                hide(progressBar).hide(emptyText).hide(reloadText)
                        .fadeIn(mainView, animate).show(mainView);
            } else {
                if (hasError) {
                    hide(progressBar).hide(emptyText).hide(mainView).show(reloadText);
                } else {
                    hide(progressBar).hide(reloadText).hide(mainView).show(emptyText);
                }
            }
        } else {
            hide(emptyText).hide(reloadText).hide(mainView)
                    .fadeIn(progressBar, animate).show(progressBar);
        }
        return;
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

    protected String getFragmentName() {
        return getClass().getSimpleName();
    }

    protected abstract boolean readyToShow();

    protected abstract View getMainView();

    protected abstract E loadDataCore(final Bundle bundle) throws Exception;

    protected abstract void handleLoadResult(E result);
}
