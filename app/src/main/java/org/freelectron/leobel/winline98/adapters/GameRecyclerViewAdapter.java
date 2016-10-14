package org.freelectron.leobel.winline98.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.freelectron.leobel.winline98.BoardView;
import org.freelectron.leobel.winline98.R;
import org.freelectron.winline.LogicWinLine;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link LogicWinLine} and makes a call to the
 * specified {@link RecyclerViewGameLoadAdapterListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.ViewHolder> {

    private final List<LogicWinLine> mValues;
    private final RecyclerViewGameLoadAdapterListener mListener;

    public GameRecyclerViewAdapter(List<LogicWinLine> items, RecyclerViewGameLoadAdapterListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onLoadGame(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        private BoardView boardView;
        private LogicWinLine mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            boardView = (BoardView) view.findViewById(R.id.board_game);
        }

        public void setItem(LogicWinLine item){
            this.mItem = item;
            boardView.setBoard(mItem.getBoard());
            //boardView.invalidate();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
