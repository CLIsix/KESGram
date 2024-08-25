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
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clisix.kesgram.AyuConfig;
import com.clisix.kesgram.AyuConstants;
import com.clisix.kesgram.messages.AyuMessagesController;
import com.clisix.kesgram.sync.AyuSyncState;
import com.clisix.kesgram.ui.preferences.utils.AyuUi;
import com.clisix.kesgram.utils.AyuState;
import com.exteragram.messenger.preferences.BasePreferencesActivity;

import org.jetbrains.annotations.NotNull;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.RecyclerListView;

import java.util.Locale;

public class KESGramPreferencesActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {

    private static final int TOGGLE_BUTTON_VIEW = 1000;

    private int ghostEssentialsHeaderRow;
    private int ghostModeToggleRow;
    private int sendReadPacketsRow;
    private int sendOnlinePacketsRow;
    private int sendUploadProgressRow;
    private int sendOfflinePacketAfterOnlineRow;
    private int markReadAfterSendRow;
    private int useScheduledMessagesRow;
    private int ghostDividerRow;

    private int spyHeaderRow;
    private int saveDeletedMessagesRow;
    private int saveMessagesHistoryRow;
    private int spyDivider1Row;
    private int messageSavingBtnRow;
    private int spyDivider2Row;

    private int qolHeaderRow;
    private int keepAliveServiceRow;
    private int disableAdsRow;
    private int localPremiumRow;
    private int filtersRow;
    private int qolDividerRow;

    private int customizationHeaderRow;
    private int deletedMarkTextRow;
    private int editedMarkTextRow;
    private int showGhostToggleInDrawerRow;
    private int showKillButtonInDrawerRow;
    private int customizationDividerRow;

    private int ayuSyncHeaderRow;
    private int ayuSyncStatusBtnRow;
    private int ayuSyncDividerRow;

    private int debugHeaderRow;
    private int WALModeRow;
    private int buttonsDividerRow;
    private int clearAyuDatabaseBtnRow;
    private int eraseLocalDatabaseBtnRow;

    private boolean ghostModeMenuExpanded;

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        ghostEssentialsHeaderRow = newRow();
        ghostModeToggleRow = newRow();
        if (ghostModeMenuExpanded) {
            sendReadPacketsRow = newRow();
            sendOnlinePacketsRow = newRow();
            sendUploadProgressRow = newRow();
            sendOfflinePacketAfterOnlineRow = newRow();
        } else {
            sendReadPacketsRow = -1;
            sendOnlinePacketsRow = -1;
            sendUploadProgressRow = -1;
            sendOfflinePacketAfterOnlineRow = -1;
        }
        markReadAfterSendRow = newRow();
        useScheduledMessagesRow = newRow();
        ghostDividerRow = newRow();

        spyHeaderRow = newRow();
        saveDeletedMessagesRow = newRow();
        saveMessagesHistoryRow = newRow();
        spyDivider1Row = newRow();
        messageSavingBtnRow = newRow();
        spyDivider2Row = newRow();

        qolHeaderRow = newRow();
        keepAliveServiceRow = newRow();
        disableAdsRow = newRow();
        localPremiumRow = newRow();
        filtersRow = newRow();
        qolDividerRow = newRow();

        customizationHeaderRow = newRow();
        deletedMarkTextRow = newRow();
        editedMarkTextRow = newRow();
        showGhostToggleInDrawerRow = newRow();
        showKillButtonInDrawerRow = newRow();
        customizationDividerRow = newRow();

        ayuSyncHeaderRow = newRow();
        ayuSyncStatusBtnRow = newRow();
        ayuSyncDividerRow = newRow();

        debugHeaderRow = newRow();
        WALModeRow = newRow();
        buttonsDividerRow = newRow();
        clearAyuDatabaseBtnRow = newRow();
        eraseLocalDatabaseBtnRow = newRow();
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        // todo: register `MESSAGES_DELETED_NOTIFICATION` on all notification centers, not only on the current account

        NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, AyuConstants.MESSAGES_DELETED_NOTIFICATION);
        NotificationCenter.getGlobalInstance().addObserver(this, AyuConstants.AYUSYNC_STATE_CHANGED);

        return true;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == AyuConstants.MESSAGES_DELETED_NOTIFICATION) {
            // recalculate database size
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(clearAyuDatabaseBtnRow);
            }
        } else if (id == AyuConstants.AYUSYNC_STATE_CHANGED) {
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(ayuSyncStatusBtnRow);
            }
        }
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, AyuConstants.MESSAGES_DELETED_NOTIFICATION);
        NotificationCenter.getGlobalInstance().removeObserver(this, AyuConstants.AYUSYNC_STATE_CHANGED);
    }

    private void updateGhostViews() {
        var isActive = AyuConfig.isGhostModeActive();

        listAdapter.notifyItemChanged(ghostModeToggleRow, payload);
        listAdapter.notifyItemChanged(sendReadPacketsRow, !isActive);
        listAdapter.notifyItemChanged(sendOnlinePacketsRow, !isActive);
        listAdapter.notifyItemChanged(sendUploadProgressRow, !isActive);
        listAdapter.notifyItemChanged(sendOfflinePacketAfterOnlineRow, isActive);

        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
    }

    private void toggleLocalPremium() {
        var newState = !AyuConfig.localPremium;

        AyuConfig.editor.putBoolean("localPremium", AyuConfig.localPremium = newState).apply();
        listAdapter.notifyItemChanged(localPremiumRow, AyuConfig.localPremium);

        getMessagesController().updatePremium(AyuConfig.localPremium);
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.premiumStatusChangedGlobal);

        getMediaDataController().loadPremiumPromo(false);
        getMediaDataController().loadReactions(false, true);
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == ghostModeToggleRow) {
            ghostModeMenuExpanded ^= true;
            updateRowsId();
            listAdapter.notifyItemChanged(ghostModeToggleRow, payload);
            if (ghostModeMenuExpanded) {
                listAdapter.notifyItemRangeInserted(ghostModeToggleRow + 1, 4);
            } else {
                listAdapter.notifyItemRangeRemoved(ghostModeToggleRow + 1, 4);
            }
        } else if (position == sendReadPacketsRow) {
            AyuConfig.editor.putBoolean("sendReadPackets", AyuConfig.sendReadPackets ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendReadPackets, true);

            AyuState.setAllowReadPacket(false, -1);
            updateGhostViews();
        } else if (position == sendOnlinePacketsRow) {
            AyuConfig.editor.putBoolean("sendOnlinePackets", AyuConfig.sendOnlinePackets ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendOnlinePackets, true);

            updateGhostViews();
        } else if (position == sendUploadProgressRow) {
            AyuConfig.editor.putBoolean("sendUploadProgress", AyuConfig.sendUploadProgress ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendUploadProgress, true);

            updateGhostViews();
        } else if (position == sendOfflinePacketAfterOnlineRow) {
            AyuConfig.editor.putBoolean("sendOfflinePacketAfterOnline", AyuConfig.sendOfflinePacketAfterOnline ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendOfflinePacketAfterOnline, true);

            updateGhostViews();
        } else if (position == markReadAfterSendRow) {
            AyuConfig.editor.putBoolean("markReadAfterSend", AyuConfig.markReadAfterSend ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.markReadAfterSend);

            AyuState.setAllowReadPacket(false, -1);

            if (AyuConfig.markReadAfterSend && AyuConfig.useScheduledMessages) {
                AyuConfig.editor.putBoolean("useScheduledMessages", AyuConfig.useScheduledMessages ^= true).apply();

                listAdapter.notifyItemChanged(useScheduledMessagesRow, false);
            }
        } else if (position == useScheduledMessagesRow) {
            AyuConfig.editor.putBoolean("useScheduledMessages", AyuConfig.useScheduledMessages ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.useScheduledMessages);

            AyuState.setAutomaticallyScheduled(false, -1);

            if (AyuConfig.useScheduledMessages && AyuConfig.markReadAfterSend) {
                AyuConfig.editor.putBoolean("markReadAfterSend", AyuConfig.markReadAfterSend ^= true).apply();

                listAdapter.notifyItemChanged(markReadAfterSendRow, false);
            }
        } else if (position == saveDeletedMessagesRow) {
            AyuConfig.editor.putBoolean("saveDeletedMessages", AyuConfig.saveDeletedMessages ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.saveDeletedMessages);
        } else if (position == saveMessagesHistoryRow) {
            AyuConfig.editor.putBoolean("saveMessagesHistory", AyuConfig.saveMessagesHistory ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.saveMessagesHistory);
        } else if (position == messageSavingBtnRow) {
            presentFragment(new MessageSavingPreferencesActivity());
        } else if (position == keepAliveServiceRow) {
            AyuConfig.editor.putBoolean("keepAliveService", AyuConfig.keepAliveService ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.keepAliveService);
        } else if (position == disableAdsRow) {
            AyuConfig.editor.putBoolean("disableAds", AyuConfig.disableAds ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.disableAds);
        } else if (position == localPremiumRow) {
            toggleLocalPremium();
        } else if (position == filtersRow) {
            NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
            if (LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76)) {
                AyuConfig.editor.putBoolean("regexFiltersEnabled", AyuConfig.regexFiltersEnabled ^= true).apply();
                checkCell.setChecked(AyuConfig.regexFiltersEnabled, 0);
            } else {
                presentFragment(new RegexFiltersPreferencesActivity());
            }
        } else if (position == showGhostToggleInDrawerRow) {
            AyuConfig.editor.putBoolean("showGhostToggleInDrawer", AyuConfig.showGhostToggleInDrawer ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.showGhostToggleInDrawer);

            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        } else if (position == showKillButtonInDrawerRow) {
            AyuConfig.editor.putBoolean("showKillButtonInDrawer", AyuConfig.showKillButtonInDrawer ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.showKillButtonInDrawer);

            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        } else if (position == deletedMarkTextRow) {
            AyuUi.spawnEditBox(
                    getParentActivity(),
                    ((TextCell) view),
                    LocaleController.getString(R.string.DeletedMarkText),
                    AyuConfig::getDeletedMark,
                    "deletedMarkText",
                    AyuConstants.DEFAULT_DELETED_MARK
            );
        } else if (position == editedMarkTextRow) {
            AyuUi.spawnEditBox(
                    getParentActivity(),
                    ((TextCell) view),
                    LocaleController.getString(R.string.EditedMarkText),
                    AyuConfig::getEditedMark,
                    "editedMarkText",
                    LocaleController.getString("EditedMessage", R.string.EditedMessage) // don't remove key
            );
        } else if (position == ayuSyncStatusBtnRow) {
            presentFragment(new AyuSyncPreferencesActivity());
        } else if (position == WALModeRow) {
            AyuConfig.editor.putBoolean("WALMode", AyuConfig.WALMode ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.WALMode);
        } else if (position == clearAyuDatabaseBtnRow) {
            AyuMessagesController.getInstance().clean();

            // reset size
            ((TextCell) view).setValue("…");

            BulletinFactory.of(this).createSimpleBulletin(R.raw.info, LocaleController.getString(R.string.ClearAyuDatabaseNotification)).show();
        } else if (position == eraseLocalDatabaseBtnRow) {
            getMessagesStorage().clearLocalDatabase();

            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM messages_v2").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.ErrorOccurred)).show();
            }

            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM dialogs").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.ErrorOccurred)).show();
            }

            BulletinFactory.of(this).createSimpleBulletin(R.raw.info, LocaleController.getString(R.string.RestartRequired)).show();
        }
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString(R.string.KesPreferences);
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private int getGhostModeSelectedCount() {
        int count = 0;
        if (!AyuConfig.sendReadPackets) count++;
        if (!AyuConfig.sendOnlinePackets) count++;
        if (!AyuConfig.sendUploadProgress) count++;
        if (AyuConfig.sendOfflinePacketAfterOnline) count++;

        return count;
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
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == messageSavingBtnRow) {
                        textCell.setText(LocaleController.getString(R.string.MessageSavingBtn), false);
                    } else if (position == deletedMarkTextRow) {
                        textCell.setTextAndValue(LocaleController.getString(R.string.DeletedMarkText), AyuConfig.getDeletedMark(), true);
                    } else if (position == editedMarkTextRow) {
                        textCell.setTextAndValue(LocaleController.getString(R.string.EditedMarkText), AyuConfig.getEditedMark(), true);
                    } else if (position == ayuSyncStatusBtnRow) {
                        var status = AyuSyncState.getConnectionStateString();

                        textCell.setTextAndValue(LocaleController.getString(R.string.AyuSyncStatusTitle), status, false);
                    } else if (position == clearAyuDatabaseBtnRow) {
                        var file = ApplicationLoader.applicationContext.getDatabasePath(AyuConstants.AYU_DATABASE);
                        var size = file.exists() ? file.length() : 0;

                        textCell.setTextAndValueAndIcon(LocaleController.getString(R.string.ClearAyuDatabase), AndroidUtilities.formatFileSize(size), R.drawable.msg_clear_solar, true);
                        textCell.setColors(Theme.key_text_RedBold, Theme.key_text_RedBold);
                    } else if (position == eraseLocalDatabaseBtnRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.EraseLocalDatabase), R.drawable.msg_archive, false);
                        textCell.setColors(Theme.key_text_RedBold, Theme.key_text_RedBold);
                    }
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ghostEssentialsHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.GhostEssentialsHeader));
                    } else if (position == spyHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.SpyEssentialsHeader));
                    } else if (position == qolHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.QoLTogglesHeader));
                    } else if (position == customizationHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.CustomizationHeader));
                    } else if (position == ayuSyncHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.AyuSyncHeader));
                    } else if (position == debugHeaderRow) {
                        headerCell.setText(LocaleController.getString("SettingsDebug", R.string.SettingsDebug));
                    }
                    break;
                case 5:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == markReadAfterSendRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.MarkReadAfterSend), AyuConfig.markReadAfterSend, true);
                    } else if (position == useScheduledMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.UseScheduledMessages), AyuConfig.useScheduledMessages, false);
                    } else if (position == saveDeletedMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.SaveDeletedMessages), AyuConfig.saveDeletedMessages, true);
                    } else if (position == saveMessagesHistoryRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.SaveMessagesHistory), AyuConfig.saveMessagesHistory, false);
                    } else if (position == keepAliveServiceRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.KeepAliveService), AyuConfig.keepAliveService, true);
                    } else if (position == disableAdsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.DisableAds), AyuConfig.disableAds, true);
                    } else if (position == localPremiumRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.LocalPremium) + " β", AyuConfig.localPremium, true);
                    } else if (position == showGhostToggleInDrawerRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.ShowGhostToggleInDrawer), AyuConfig.showGhostToggleInDrawer, true);
                    } else if (position == showKillButtonInDrawerRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.ShowKllButtonInDrawer), AyuConfig.showKillButtonInDrawer, false);
                    } else if (position == WALModeRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.WALMode), AyuConfig.WALMode, false);
                    }
                    break;
                case 18:
                    TextCheckCell2 checkCell = (TextCheckCell2) holder.itemView;
                    if (position == ghostModeToggleRow) {
                        int selectedCount = getGhostModeSelectedCount();
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.GhostModeToggle), AyuConfig.isGhostModeActive(), true, true);
                        checkCell.setCollapseArrow(String.format(Locale.US, "%d/4", selectedCount), !ghostModeMenuExpanded, () -> {
                            AyuConfig.toggleGhostMode();
                            updateGhostViews();
                        });
                    }
                    checkCell.getCheckBox().setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
                    checkCell.getCheckBox().setDrawIconType(0);
                    break;
                case 19:
                    CheckBoxCell checkBoxCell = (CheckBoxCell) holder.itemView;
                    if (position == sendReadPacketsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendReadPackets), "", !AyuConfig.sendReadPackets, true, true);
                    } else if (position == sendOnlinePacketsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendOnlinePackets), "", !AyuConfig.sendOnlinePackets, true, true);
                    } else if (position == sendUploadProgressRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendUploadProgress), "", !AyuConfig.sendUploadProgress, true, true);
                    } else if (position == sendOfflinePacketAfterOnlineRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendOfflinePacketAfterOnline), "", AyuConfig.sendOfflinePacketAfterOnline, true, true);
                    }
                    checkBoxCell.setPad(1);
                    break;
                case TOGGLE_BUTTON_VIEW:
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) holder.itemView;
                    if (position == filtersRow) {
                        var count = AyuConfig.getRegexFilters().size();
                        notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString(R.string.RegexFilters), count + " " + LocaleController.getString(R.string.RegexFiltersAmount), AyuConfig.regexFiltersEnabled, false);
                    }
                    break;
            }
        }

        @NonNull
        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            if (viewType == TOGGLE_BUTTON_VIEW) {
                var view = new NotificationsCheckCell(mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                return new RecyclerListView.Holder(view);
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public int getItemViewType(int position) {
            if (
                    position == ghostDividerRow ||
                            position == spyDivider1Row ||
                            position == spyDivider2Row ||
                            position == qolDividerRow ||
                            position == customizationDividerRow ||
                            position == ayuSyncDividerRow ||
                            position == buttonsDividerRow
            ) {
                return 1;
            } else if (
                    position == messageSavingBtnRow ||
                            position == deletedMarkTextRow ||
                            position == editedMarkTextRow ||
                            position == ayuSyncStatusBtnRow ||
                            position == clearAyuDatabaseBtnRow ||
                            position == eraseLocalDatabaseBtnRow
            ) {
                return 2;
            } else if (
                    position == ghostEssentialsHeaderRow ||
                            position == spyHeaderRow ||
                            position == qolHeaderRow ||
                            position == customizationHeaderRow ||
                            position == ayuSyncHeaderRow ||
                            position == debugHeaderRow
            ) {
                return 3;
            } else if (
                    position == ghostModeToggleRow
            ) {
                return 18;
            } else if (
                    position >= sendReadPacketsRow && position <= sendOfflinePacketAfterOnlineRow
            ) {
                return 19;
            } else if (
                    position == filtersRow
            ) {
                return TOGGLE_BUTTON_VIEW;
            }
            return 5;
        }
    }
}
