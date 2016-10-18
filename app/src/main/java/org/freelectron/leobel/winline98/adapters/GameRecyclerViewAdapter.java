package org.freelectron.leobel.winline98.adapters;

import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import org.freelectron.leobel.winline98.BoardView;
import org.freelectron.leobel.winline98.R;
import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.utils.ActivityUtils;
import org.freelectron.winline.LogicWinLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link LogicWinLine} and makes a call to the
 * specified {@link RecyclerViewGameLoadAdapterListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.GameViewHolder> {

    private final List<LogicWinLine> mValues;
    private final RecyclerViewGameLoadAdapterListener mListener;
    private List<Boolean> leftContainers;

    public GameRecyclerViewAdapter(List<LogicWinLine> items, RecyclerViewGameLoadAdapterListener listener) {
        mValues = items;
        Collections.sort(mValues, (lhs, rhs) -> -lhs.getScore().compareTo(rhs.getScore()));
        mListener = listener;
        leftContainers = new ArrayList<>(Arrays.asList(new Boolean[items.size()]));
        Collections.fill(leftContainers, Boolean.FALSE);
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GameViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
        holder.setLeftContainerVisibility(leftContainers.get(position));

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

        holder.cancelDeleteGame.setOnClickListener( v -> {
            setLeftContainerVisibility(holder.getAdapterPosition(), false);
            holder.setLeftContainerVisibility(leftContainers.get(holder.getAdapterPosition()));
        });

        holder.deleteGame.setOnClickListener(v -> {
            setLeftContainerVisibility(holder.getAdapterPosition(), false);
            holder.setLeftContainerVisibility(leftContainers.get(holder.getAdapterPosition()));
            mListener.removeItem(holder.getAdapterPosition(), holder.mItem);
        });
    }

    public void removeItem(int position){
        leftContainers.remove(position);
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void setLeftContainerVisibility(int position, Boolean visibility){
        leftContainers.set(position, visibility);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        private BoardView boardView;
        private TextView gameDate;
        private Chronometer chronometer;
        private TextView score;
        private View leftContainer;
        private LogicWinLine mItem;
        private int leftContainerVisibility = View.GONE;
        public Button deleteGame;
        public Button cancelDeleteGame;


        public GameViewHolder(View view) {
            super(view);
            mView = view;
            boardView = (BoardView) view.findViewById(R.id.board_game);
            gameDate = (TextView) view.findViewById(R.id.game_date);
            chronometer = (Chronometer) view.findViewById(R.id.chronometer);
            score = (TextView) view.findViewById(R.id.score);


            leftContainer = view.findViewById(R.id.left_container);
            deleteGame = (Button) view.findViewById(R.id.delete_game);
            cancelDeleteGame = (Button) view.findViewById(R.id.cancel_delete_action);
            leftContainer.setVisibility(leftContainerVisibility);
        }

        public void setItem(LogicWinLine item){
            this.mItem = item;
            boardView.setBoard(mItem.getBoard());
            WinLine game = (WinLine)mItem;
            gameDate.setText(ActivityUtils.formatFullDate(game.getId()));
            long timeWhenStopped = -1L * game.getTime();
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            score.setText(game.getScore().toString());
        }

        @Override
        public String toString() {
            return super.toString();
        }

        public void setLeftContainerVisibility(boolean leftContainerVisibility) {
            this.leftContainerVisibility = leftContainerVisibility ? View.VISIBLE: View.GONE;
            leftContainer.setVisibility(this.leftContainerVisibility);
        }

        public boolean isSwipeable() {
            return leftContainerVisibility  == View.VISIBLE ? false : true;
        }
    }
}
