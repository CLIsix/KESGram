/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.ui.preferences;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.clisix.kesgram.AyuConfig;
import com.clisix.kesgram.AyuFilter;
import com.clisix.kesgram.ui.preferences.utils.RegexFilterPopup;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;

import java.util.ArrayList;

public class RegexFiltersPreferencesActivity extends BasePreferencesActivity {

    private ArrayList<String> filters;

    private int generalHeaderRow;
    private int enableInChatsRow;
    private int caseInsensitiveRow;
    private int generalDividerRow;

    private int addFilterBtnRow;
    private int addFilterDividerRow;

    private int filtersHeaderRow;

    // .. filters


    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        generalHeaderRow = newRow();
        enableInChatsRow = newRow();
        caseInsensitiveRow = newRow();
        generalDividerRow = newRow();

        addFilterBtnRow = newRow();
        addFilterDividerRow = newRow();

        filtersHeaderRow = -1;

        filters = AyuConfig.getRegexFilters();
        var count = filters.size();

        if (count != 0) {
            filtersHeaderRow = newRow();
            rowCount += filters.size();
        }
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position > filtersHeaderRow && filtersHeaderRow != -1) {
            // clicked on a filter
            RegexFilterPopup.show(this, view, x, y, position - filtersHeaderRow - 1);
        } else if (position == enableInChatsRow) {
            AyuConfig.editor.putBoolean("regexFiltersInChats", AyuConfig.regexFiltersInChats ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.regexFiltersInChats);
        } else if (position == caseInsensitiveRow) {
            AyuConfig.editor.putBoolean("regexFiltersCaseInsensitive", AyuConfig.regexFiltersCaseInsensitive ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.regexFiltersCaseInsensitive);

            AyuFilter.rebuildCache();
        } else if (position == addFilterBtnRow) {
            presentFragment(new RegexFilterEditActivity());
        }
    }

    @Override
    public void onResume() {
        updateRowsId();

        super.onResume();
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString(R.string.RegexFilters);
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean payload) {
            switch (holder.getItemViewType()) {
                case 1:
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position > filtersHeaderRow && filtersHeaderRow != -1) {
                        var dividerNeeded = position != filtersHeaderRow + filters.size();
                        textCell.setText(filters.get(position - filtersHeaderRow - 1), dividerNeeded);
                    } else if (position == addFilterBtnRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.RegexFiltersAdd), R.drawable.msg_add, false);
                    }
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == generalHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.General));
                    } else if (position == filtersHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.RegexFiltersHeader));
                    }
                    break;
                case 5:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == enableInChatsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.RegexFiltersEnableInChats), AyuConfig.regexFiltersInChats, true);
                    } else if (position == caseInsensitiveRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.RegexFiltersCaseInsensitive), AyuConfig.regexFiltersCaseInsensitive, false);
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (
                    position == generalDividerRow ||
                            position == addFilterDividerRow
            ) {
                return 1;
            } else if (
                    position == generalHeaderRow ||
                            position == filtersHeaderRow
            ) {
                return 3;
            } else if (
                    position == enableInChatsRow ||
                            position == caseInsensitiveRow
            ) {
                return 5;
            }
            return 2;
        }
    }
}
