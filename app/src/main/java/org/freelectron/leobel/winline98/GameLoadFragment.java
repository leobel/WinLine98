package org.freelectron.leobel.winline98;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.freelectron.leobel.winline98.adapters.GameRecyclerViewAdapter;
import org.freelectron.leobel.winline98.adapters.RecyclerViewGameLoadAdapterListener;
import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.utils.ActivityUtils;
import org.freelectron.leobel.winline98.widgets.DividerItemDecoration;
import org.freelectron.winline.LogicWinLine;

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

    @Inject
    public GameService gameService;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameLoadFragment() {
    }

    // TODO: Customize parameter initialization
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

        if (getArguments() != null) {
        }
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

        recyclerView.setAdapter(new GameRecyclerViewAdapter(gameService.findAll(), this));
        return view;
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
    public void onLoadGame(LogicWinLine game) {
        mListener.loadGame(game);
    }

    @Override
    public void removeItem(int adapterPosition, LogicWinLine mItem) {
        WinLine game = (WinLine) mItem;
        if(gameService.remove(game.getId())){
            ((GameRecyclerViewAdapter)recyclerView.getAdapter()).removeItem(adapterPosition);
            ActivityUtils.showDialog(getActivity(), getString(R.string.delete_game_success), getString(R.string.success_title));
        }
        else{
            ActivityUtils.showDialog(getActivity(), getString(R.string.delete_game_fail), getString(R.string.error_title));
        }
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
    }
}
