package org.freelectron.leobel.winline98;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.freelectron.leobel.winline98.adapters.GameRecyclerViewAdapter;
import org.freelectron.leobel.winline98.adapters.RecyclerViewGameLoadAdapterListener;
import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.widgets.DividerItemDecoration;
import org.freelectron.winline.LogicWinLine;

import javax.inject.Inject;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GameLoadFragment extends Fragment implements RecyclerViewGameLoadAdapterListener {

    private OnListFragmentInteractionListener mListener;

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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // set divider between items
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.game_list_divider));

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
