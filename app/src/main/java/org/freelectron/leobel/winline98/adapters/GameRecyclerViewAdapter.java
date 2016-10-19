package org.freelectron.leobel.winline98.adapters;

import android.os.SystemClock;
import android.support.v7.widget.AppCompatCheckBox;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WinLine} and makes a call to the
 * specified {@link RecyclerViewGameLoadAdapterListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.GameViewHolder> {

    private List<WinLine> mValues;
    private final RecyclerViewGameLoadAdapterListener mListener;
    private List<Boolean> leftContainers;
    private List<Boolean> checkedItems;
    private boolean selectMultipleItemsMode;

    public GameRecyclerViewAdapter(List<WinLine> items, RecyclerViewGameLoadAdapterListener listener) {
        mValues = items;
        mListener = listener;
        leftContainers = new ArrayList<>(Arrays.asList(new Boolean[items.size()]));
        checkedItems = new ArrayList<>(Arrays.asList(new Boolean[items.size()]));
        Collections.fill(leftContainers, Boolean.FALSE);
        Collections.fill(checkedItems, Boolean.FALSE);
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
        holder.setCheckBoxVisibility(selectMultipleItemsMode);
        if(selectMultipleItemsMode){
            holder.setCheckBoxChecked(checkedItems.get(position));
        }

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                if(selectMultipleItemsMode){
                    boolean selected = !holder.checkbox.isChecked();
                    checkedItems.set(holder.getAdapterPosition(), selected);
                    holder.setCheckBoxChecked(selected);
                    mListener.onItemClickedInSelectMultipleItemsMode(holder.getAdapterPosition(), holder.mItem, selected);
                }
                else{
                    mListener.onLoadGame(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener( v -> {
            if(mListener != null){
                if(!selectMultipleItemsMode){
                    setSelectMultipleItemsMode(true);
                    checkedItems.set(holder.getAdapterPosition(), true);
                    mListener.onItemLongClicked(holder.getAdapterPosition(), holder.mItem);
                    notifyDataSetChanged();
                }

            }
            return true;
        });

        holder.checkbox.setOnClickListener(v -> {
            boolean selected = holder.checkbox.isChecked();
            checkedItems.set(holder.getAdapterPosition(), selected);
            mListener.onItemClickedInSelectMultipleItemsMode(holder.getAdapterPosition(), holder.mItem, selected);
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
        checkedItems.remove(position);
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(List<Integer> positions){
        Collections.sort(positions, (lhs, rhs) -> -lhs.compareTo(rhs));
        for (int position: positions){
            leftContainers.remove(position);
            checkedItems.remove(position);
            mValues.remove(position);
        }
        notifyDataSetChanged();
    }

    public void setLeftContainerVisibility(int position, Boolean visibility){
        leftContainers.set(position, visibility);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<WinLine> getItems() {
        return mValues;
    }

    public void setItems(List<WinLine> items) {
        this.mValues = items;
    }

    public void checkAllItems() {
        for(int i = 0; i < checkedItems.size(); i++){
            checkedItems.set(i, true);
        }
        notifyDataSetChanged();
    }

    public void unCheckAllItems() {
        for(int i = 0; i < checkedItems.size(); i++){
            checkedItems.set(i, false);
        }
        notifyDataSetChanged();
    }

    public void setSelectMultipleItemsMode(boolean selectMultipleItemsMode) {
        this.selectMultipleItemsMode = selectMultipleItemsMode;
    }

    public class GameViewHolder extends RecyclerView.ViewHolder{
        public final View mView;


        private BoardView boardView;
        private TextView gameDate;
        private Chronometer chronometer;
        private TextView score;
        private View leftContainer;
        private WinLine mItem;
        private int checkBoxVisibility = View.GONE;
        private int leftContainerVisibility = View.GONE;

        public AppCompatCheckBox checkbox;
        public Button deleteGame;
        public Button cancelDeleteGame;


        public GameViewHolder(View view) {
            super(view);
            mView = view;
            boardView = (BoardView) view.findViewById(R.id.board_game);
            gameDate = (TextView) view.findViewById(R.id.game_date);
            chronometer = (Chronometer) view.findViewById(R.id.chronometer);
            score = (TextView) view.findViewById(R.id.score);
            checkbox = (AppCompatCheckBox) view.findViewById(R.id.delete_mark);

            leftContainer = view.findViewById(R.id.left_container);
            deleteGame = (Button) view.findViewById(R.id.delete_game);
            cancelDeleteGame = (Button) view.findViewById(R.id.cancel_delete_action);

            checkbox.setVisibility(checkBoxVisibility);
            leftContainer.setVisibility(leftContainerVisibility);
        }

        public void setItem(WinLine item){
            this.mItem = item;
            boardView.setBoard(mItem.getBoard());
            gameDate.setText(ActivityUtils.formatFullDate(mItem.getId()));
            long timeWhenStopped = -1L * mItem.getTime();
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            score.setText(mItem.getScore().toString());
        }

        @Override
        public String toString() {
            return super.toString();
        }

        public void setCheckBoxVisibility(boolean checkBoxVisibility){
            this.checkBoxVisibility = checkBoxVisibility ? View.VISIBLE: View.GONE;
            checkbox.setVisibility(this.checkBoxVisibility);
        }

        public void setLeftContainerVisibility(boolean leftContainerVisibility) {
            this.leftContainerVisibility = leftContainerVisibility ? View.VISIBLE: View.GONE;
            leftContainer.setVisibility(this.leftContainerVisibility);
        }

        public boolean isSwipeable() {
            return leftContainerVisibility  == View.VISIBLE ? false : true;
        }

        public void setCheckBoxChecked(boolean checked) {
           this.checkbox.setChecked(checked);
        }
    }
}
