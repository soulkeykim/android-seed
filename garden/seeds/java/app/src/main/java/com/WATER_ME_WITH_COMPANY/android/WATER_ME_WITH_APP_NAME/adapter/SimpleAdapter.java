package com.{{company_name}}.android.{{app_package_name_prefix}}.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.{{company_name}}.android.{{app_package_name_prefix}}.adapter.SimpleAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Generic simple adapter implementation, subclass to implement
 * Should work out of the box for majority of lists unless you need different item types
 */
public abstract class SimpleAdapter<T, V extends View> extends RecyclerView.Adapter<ViewHolder> {

    protected List<T> mList = new ArrayList<>();

    @VisibleForTesting OnItemClickListener<T> mPublishListener;
    @VisibleForTesting ViewHolder.OnViewHolderClickListener mViewHolderListener = new ViewHolder.OnViewHolderClickListener() {
        @Override
        public void onViewHolderClicked(int position, View view) {
            if (mPublishListener != null) {
                mPublishListener.onItemClicked(position, getItemAt(position), view);
            }
        }
    };

    protected abstract V createView(ViewGroup parent, int viewType);

    protected abstract void bindView(V view, int position);

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final V view = createView(parent, viewType);
        view.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        ViewHolder holder = new ViewHolder(view);
        holder.setListener(mViewHolderListener);
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        bindView((V) holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public T getItemAt(int position) {
        return mList.get(position);
    }

    public List<T> getAllItems() {
        return new ArrayList<>(mList);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mPublishListener = listener;
    }

    public void setData(@NonNull List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        mList = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener<O> {
        void onItemClicked(int position, O item, View view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @VisibleForTesting OnViewHolderClickListener mListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onViewHolderClicked(this.getAdapterPosition(), v);
            }
        }

        public void setListener(OnViewHolderClickListener listener) {
            mListener = listener;
        }

        interface OnViewHolderClickListener {
            void onViewHolderClicked(int position, View view);
        }
    }

}