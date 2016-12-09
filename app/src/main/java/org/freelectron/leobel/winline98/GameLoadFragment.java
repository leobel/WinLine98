package org.freelectron.leobel.winline98;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.freelectron.leobel.winline98.adapters.GameRecyclerViewAdapter;
import org.freelectron.leobel.winline98.adapters.RecyclerViewGameLoadAdapterListener;
import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.services.PreferenceService;
import org.freelectron.leobel.winline98.utils.ActivityUtils;
import org.freelectron.leobel.winline98.utils.CollectionsUtils;
import org.freelectron.leobel.winline98.widgets.DividerItemDecoration;
import org.freelectron.winline.LogicWinLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GameLoadFragment extends Fragment implements RecyclerViewGameLoadAdapterListener {

    private OnListFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private GameRecyclerViewAdapter adapter;

    @Inject
    public GameService gameService;

    @Inject
    public PreferenceService preferenceService;

    private HashMap<Integer, WinLine> selectedItems;
    private ProgressDialog mProgressDialog;

    private MediaPlayer mpTrash;
    private Integer record;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameLoadFragment() {
    }

    @SuppressWarnings("unused")
    public static GameLoadFragment newInstance(int columnCount) {
        GameLoadFragment fragment = new GameLoadFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WinLineApp.getInstance().getComponent().inject(this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Your request is being processes");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mpTrash = MediaPlayer.create(getActivity(), R.raw.trash);

        selectedItems = new HashMap<>();

        record = preferenceService.getHighRecord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        // Set the adapter
        Context context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        // set divider between items
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.game_list_divider));

        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.red));
        //p.setARGB(255, 255, 0, 0);
        Boolean[] swiped = new Boolean[]{false};

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                GameRecyclerViewAdapter adapter = (GameRecyclerViewAdapter)recyclerView.getAdapter();
                adapter.setLeftContainerVisibility(position, true);
                adapter.notifyItemChanged(position);
            }

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return .3f;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                if(((GameRecyclerViewAdapter.GameViewHolder) viewHolder).isSwipeable() && actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    View itemView = viewHolder.itemView;
                    if (dX > 0) {
                        c.drawRect(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom(), p);
                    } else {
                        c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), p);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        List<WinLine> items = (List<WinLine>)(List<?>) gameService.findAll();
        Collections.sort(items, (lhs, rhs) -> -lhs.getScore().compareTo(rhs.getScore()));
        adapter = new GameRecyclerViewAdapter(items, record, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void orderGameBy(int option){
        List<WinLine> items = adapter.getItems();
        if(option == R.id.game_saved_date){
            Collections.sort(items, (lhs, rhs) -> -lhs.getId().compareTo(rhs.getId()));
        }
        else if(option == R.id.game_saved_score){
            Collections.sort(items, (lhs, rhs) -> -lhs.getScore().compareTo(rhs.getScore()));
        }
        else{
            Collections.sort(items, (lhs, rhs) -> -lhs.getTime().compareTo(rhs.getTime()));
        }

        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLoadGame(WinLine game) {
        mListener.loadGame(game);
    }

    @Override
    public void onItemLongClicked(int adapterPosition, WinLine mItem) {
        selectedItems.clear();
        selectedItems.put(adapterPosition, mItem);
        mListener.selectMultipleItems(true);
    }

    @Override
    public void onItemClickedInSelectMultipleItemsMode(int adapterPosition, WinLine mItem, boolean selected) {
        if(selected) {
            selectedItems.put(adapterPosition, mItem);
        }
        else{
            selectedItems.remove(adapterPosition);
        }
        mListener.notifySelectMultipleItemsClicked(selectedItems.size());
    }

    @Override
    public void removeItem(int adapterPosition, WinLine mItem) {
        if(gameService.remove(mItem.getId())){
            if(preferenceService.getAllowTouchSoundPreference()){
                mpTrash.start();
            }
            adapter.removeItem(adapterPosition);
            if(mListener.isSelectMultipleItemsStatus()){ // verify that are in multiple delete status
                if(selectedItems.containsKey(adapterPosition)){
                    selectedItems.remove(adapterPosition);
                }
                mListener.notifySelectMultipleItemsClicked(selectedItems.size());
            }
            ActivityUtils.showDialog(getActivity(), getString(R.string.delete_game_success), getString(R.string.success_title));
        }
        else{
            ActivityUtils.showDialog(getActivity(), getString(R.string.delete_game_fail), getString(R.string.error_title));
        }
    }

    public void removeItems() {
        int size = selectedItems.values().size();
        if(gameService.remove(CollectionsUtils.map(selectedItems.values(), WinLine::getId))){
            if(preferenceService.getAllowTouchSoundPreference()){
                mpTrash.start();
            }
            adapter.setSelectMultipleItemsMode(false);
            adapter.removeItem(new ArrayList<>(selectedItems.keySet()));
            selectedItems.clear();
            mListener.selectMultipleItems(false);
            ActivityUtils.showDialog(getActivity(), getString(size > 1 ? R.string.delete_games_success : R.string.delete_game_success), getString(R.string.success_title));
        }
        else{
            ActivityUtils.showDialog(getActivity(), getString(size > 1 ? R.string.delete_games_fail : R.string.delete_game_fail), getString(R.string.error_title));
        }
    }

    public void unCheckAllItems() {
        adapter.unCheckAllItems();
        selectedItems.clear();
    }

    public void checkAllItems() {
        adapter.checkAllItems();
        List<WinLine> games = adapter.getItems();
        for(int i = 0; i < games.size(); i++){
            selectedItems.put(i, games.get(i));
        }
    }

    public int getItemsCount() {
        return adapter.getItemCount();
    }

    public void cancelSelectMultipleItemsMode() {
        adapter.setSelectMultipleItemsMode(false);
        adapter.unCheckAllItems();
        selectedItems.clear();
    }

    public List<Bitmap> getScreenShots() {
        List<Bitmap> result = new ArrayList<>(selectedItems.size());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_game, null);

        BoardView boardView = (BoardView) view.findViewById(R.id.board_game);
        TextView gameDate = (TextView) view.findViewById(R.id.game_date);
        ImageView timerView = (ImageView) view.findViewById(R.id.timer);
        Chronometer chronometerView = (Chronometer) view.findViewById(R.id.chronometer);
        TextView scoreView = (TextView) view.findViewById(R.id.score);
        ImageView scoreImageView = (ImageView) view.findViewById(R.id.score_image);
        for(Integer position: selectedItems.keySet()){
            WinLine mItem = selectedItems.get(position);

            RelativeLayout rl = new RelativeLayout(getContext());
            rl.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rl.setPadding(10, 0, 10, 0);
            rl.setBackgroundColor(getResources().getColor(R.color.windowBackground));

            TextView date = new TextView(getContext());
            date.setLayoutParams(gameDate.getLayoutParams());
            date.setId(gameDate.getId());
            date.setTextColor(getResources().getColor(R.color.black));
            date.setText(ActivityUtils.formatFullDate(mItem.getId()));
            date.setGravity(Gravity.CENTER_HORIZONTAL);

            BoardView board = new BoardView(getContext());
            board.setBoard(mItem.getBoard());
            board.setLayoutParams(boardView.getLayoutParams());
            board.setId(boardView.getId());

            ImageView timer = new ImageView(getContext());
            timer.setLayoutParams(timerView.getLayoutParams());
            timer.setId(timerView.getId());
            timer.setImageResource(R.drawable.ic_timer);

            Chronometer chronometer = new Chronometer(getContext());
            chronometer.setLayoutParams(chronometerView.getLayoutParams());
            chronometer.setId(chronometerView.getId());
            long timeWhenStopped = -1L * mItem.getTime();
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chronometer.setGravity(Gravity.CENTER | Gravity.LEFT);

            ImageView start = new ImageView(getContext());
            start.setLayoutParams(scoreImageView.getLayoutParams());
            start.setId(scoreImageView.getId());
            start.setImageResource(R.drawable.ic_star);
            start.setColorFilter(getResources().getColor(R.color.star_color));

            TextView score = new TextView(getContext());
            score.setLayoutParams(scoreView.getLayoutParams());
            score.setId(scoreView.getId());
            score.setTextColor(getResources().getColor(R.color.black));
            score.setText(mItem.getScore().toString());
            score.setGravity(Gravity.CENTER | Gravity.RIGHT);

            rl.addView(date);
            rl.addView(board);
            rl.addView(timer);
            rl.addView(chronometer);
            rl.addView(start);
            rl.addView(score);

            rl.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            rl.layout(0, 0, rl.getMeasuredWidth(), rl.getMeasuredHeight());

            Bitmap bitmap = Bitmap.createBitmap(rl.getMeasuredWidth(), rl.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            rl.draw(c);

            result.add(bitmap);
        }
        return result;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        void loadGame(LogicWinLine item);

        void selectMultipleItems(boolean status);

        void notifySelectMultipleItemsClicked(Integer count);

        boolean isSelectMultipleItemsStatus();
    }
}
