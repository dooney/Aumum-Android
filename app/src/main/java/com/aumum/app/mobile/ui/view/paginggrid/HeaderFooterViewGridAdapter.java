package com.aumum.app.mobile.ui.view.paginggrid;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

public class HeaderFooterViewGridAdapter implements WrapperListAdapter, Filterable {

    // This is used to notify the container of updates relating to number of columns
    // or headers changing, which changes the number of placeholders needed
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    private final ListAdapter mAdapter;
    private int mNumColumns = 1;

    // This ArrayList is assumed to NOT be null.
    ArrayList<HeaderFooterGridView.FixedViewInfo> mHeaderViewInfos;

    ArrayList<HeaderFooterGridView.FixedViewInfo> mFooterViewInfos;

    boolean mAreAllFixedViewsSelectable;

    private final boolean mIsFilterable;

    public HeaderFooterViewGridAdapter(ArrayList<HeaderFooterGridView.FixedViewInfo> headerViewInfos, ArrayList<HeaderFooterGridView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
        mAdapter = adapter;
        mIsFilterable = adapter instanceof Filterable;

        if (headerViewInfos == null) {
            throw new IllegalArgumentException("headerViewInfos cannot be null");
        }
        if (footerViewInfos == null) {
            throw new IllegalArgumentException("footerViewInfos cannot be null");
        }
        mHeaderViewInfos = headerViewInfos;
        mFooterViewInfos = footerViewInfos;

        mAreAllFixedViewsSelectable = (areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos));
    }

    public int getHeadersCount() {
        return mHeaderViewInfos.size();
    }

    public int getFootersCount() {
        return mFooterViewInfos.size();
    }

    @Override
    public boolean isEmpty() {
        return (mAdapter == null || mAdapter.isEmpty()) && getHeadersCount() == 0 && getFootersCount() == 0;
    }

    public void setNumColumns(int numColumns) {
        if (numColumns < 1) {
            throw new IllegalArgumentException("Number of columns must be 1 or more");
        }
        if (mNumColumns != numColumns) {
            mNumColumns = numColumns;
            notifyDataSetChanged();
        }
    }

    private boolean areAllListInfosSelectable(ArrayList<HeaderFooterGridView.FixedViewInfo> infos) {
        if (infos != null) {
            for (HeaderFooterGridView.FixedViewInfo info : infos) {
                if (!info.isSelectable) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean removeHeader(View v) {
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            HeaderFooterGridView.FixedViewInfo info = mHeaderViewInfos.get(i);
            if (info.view == v) {
                mHeaderViewInfos.remove(i);

                mAreAllFixedViewsSelectable = (areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos));

                mDataSetObservable.notifyChanged();
                return true;
            }
        }

        return false;
    }

    public boolean removeFooter(View v) {
        for (int i = 0; i < mFooterViewInfos.size(); i++) {
            HeaderFooterGridView.FixedViewInfo info = mFooterViewInfos.get(i);
            if (info.view == v) {
                mFooterViewInfos.remove(i);

                mAreAllFixedViewsSelectable = (areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos));

                mDataSetObservable.notifyChanged();
                return true;
            }
        }

        return false;
    }

    @Override
    public int getCount() {
        if (mAdapter != null) {
            final int lastRowItemCount = (mAdapter.getCount() % mNumColumns);
            final int emptyItemCount = ((lastRowItemCount == 0) ? 0 : mNumColumns - lastRowItemCount);
            return (getHeadersCount() * mNumColumns) + mAdapter.getCount() + emptyItemCount + (getFootersCount() * mNumColumns);
        } else {
            return (getHeadersCount() * mNumColumns) + (getFootersCount() * mNumColumns);
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        if (mAdapter != null) {
            return mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
        } else {
            return true;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders) {
            return (position % mNumColumns == 0)
                    && mHeaderViewInfos.get(position / mNumColumns).isSelectable;
        }

        // Adapter
        if (position < numHeadersAndPlaceholders + mAdapter.getCount()) {
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.isEnabled(adjPosition);
                }
            }
        }

        // Empty item
        final int lastRowItemCount = (mAdapter.getCount() % mNumColumns);
        final int emptyItemCount = ((lastRowItemCount == 0) ? 0 : mNumColumns - lastRowItemCount);
        if (position < numHeadersAndPlaceholders + mAdapter.getCount() + emptyItemCount) {
            return false;
        }

        // Footer
        int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders + mAdapter.getCount() + emptyItemCount + numFootersAndPlaceholders) {
            return (position % mNumColumns == 0)
                    && mFooterViewInfos.get((position - numHeadersAndPlaceholders - mAdapter.getCount() - emptyItemCount) / mNumColumns).isSelectable;
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public Object getItem(int position) {
        // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders) {
            if (position % mNumColumns == 0) {
                return mHeaderViewInfos.get(position / mNumColumns).data;
            }
            return null;
        }

        // Adapter
        if (position < numHeadersAndPlaceholders + mAdapter.getCount()) {
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItem(adjPosition);
                }
            }
        }

        // Empty item
        final int lastRowItemCount = (mAdapter.getCount() % mNumColumns);
        final int emptyItemCount = ((lastRowItemCount == 0) ? 0 : mNumColumns - lastRowItemCount);
        if (position < numHeadersAndPlaceholders + mAdapter.getCount() + emptyItemCount) {
            return null;
        }

        // Footer
        int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders + mAdapter.getCount() + emptyItemCount + numFootersAndPlaceholders) {
            if (position % mNumColumns == 0) {
                return mFooterViewInfos.get((position - numHeadersAndPlaceholders - mAdapter.getCount() - emptyItemCount) / mNumColumns).data;
            }
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public long getItemId(int position) {
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (mAdapter != null) {
            if (position >= numHeadersAndPlaceholders && position < numHeadersAndPlaceholders + mAdapter.getCount()) {
                int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
        }
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        if (mAdapter != null) {
            return mAdapter.hasStableIds();
        }
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders) {
            View headerViewContainer = mHeaderViewInfos
                    .get(position / mNumColumns).viewContainer;
            if (position % mNumColumns == 0) {
                return headerViewContainer;
            } else {
                convertView = new View(parent.getContext());
                // We need to do this because GridView uses the height of the last item
                // in a row to determine the height for the entire row.
                convertView.setVisibility(View.INVISIBLE);
                convertView.setMinimumHeight(headerViewContainer.getHeight());
                return convertView;
            }
        }

        // Adapter
        if (position < numHeadersAndPlaceholders + mAdapter.getCount()) {
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    convertView = mAdapter.getView(adjPosition, convertView, parent);
                    convertView.setVisibility(View.VISIBLE);
                    return convertView;
                }
            }
        }

        // Empty item
        final int lastRowItemCount = (mAdapter.getCount() % mNumColumns);
        final int emptyItemCount = ((lastRowItemCount == 0) ? 0 : mNumColumns - lastRowItemCount);
        if (position < numHeadersAndPlaceholders + mAdapter.getCount() + emptyItemCount) {
            // We need to do this because GridView uses the height of the last item
            // in a row to determine the height for the entire row.
            // TODO Current implementation may not be enough in the case of 3 or more column. May need to be careful on the INVISIBLE View height.
            convertView = mAdapter.getView(mAdapter.getCount() - 1, convertView, parent);
            convertView.setVisibility(View.INVISIBLE);
            return convertView;
        }

        // Footer
        int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders + mAdapter.getCount()  + emptyItemCount + numFootersAndPlaceholders) {
            View footerViewContainer = mFooterViewInfos
                    .get((position - numHeadersAndPlaceholders - mAdapter.getCount() - emptyItemCount) / mNumColumns).viewContainer;
            if (position % mNumColumns == 0) {
                return footerViewContainer;
            } else {
                convertView = new View(parent.getContext());
                // We need to do this because GridView uses the height of the last item
                // in a row to determine the height for the entire row.
                convertView.setVisibility(View.INVISIBLE);
                convertView.setMinimumHeight(footerViewContainer.getHeight());
                return convertView;
            }
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public int getItemViewType(int position) {
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders && (position % mNumColumns != 0)) {
            // Placeholders get the last view type number
            return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
        }
        if (mAdapter != null && position >= numHeadersAndPlaceholders && position < numHeadersAndPlaceholders + mAdapter.getCount() + (mNumColumns - (mAdapter.getCount() % mNumColumns))) {
            int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemViewType(adjPosition);
            } else if (adapterCount != 0 && mNumColumns != 1) {
                return mAdapter.getItemViewType(adapterCount - 1);
            }
        }
        int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
        if (mAdapter != null && position < numHeadersAndPlaceholders + mAdapter.getCount() + numFootersAndPlaceholders) {
            return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
        }

        return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
    }

    @Override
    public int getViewTypeCount() {
        if (mAdapter != null) {
            return mAdapter.getViewTypeCount() + 1;
        }
        return 2;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public Filter getFilter() {
        if (mIsFilterable) {
            return ((Filterable) mAdapter).getFilter();
        }
        return null;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
}