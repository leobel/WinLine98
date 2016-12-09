package org.freelectron.leobel.winline98.adapters;

import org.freelectron.leobel.winline98.models.WinLine;

/**
 * Created by leobel on 10/10/16.
 */
public interface RecyclerViewGameLoadAdapterListener {
    void onLoadGame(WinLine game);
    void onItemLongClicked(int adapterPosition, WinLine mItem);
    void onItemClickedInSelectMultipleItemsMode(int adapterPosition, WinLine mItem, boolean selected);
    void removeItem(int adapterPosition, WinLine mItem);
}
