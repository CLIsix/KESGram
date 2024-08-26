package com.clisix.kesgram;
import org.telegram.messenger.*;
import org.telegram.tgnet.TLRPC;
import java.util.ArrayList;

public class KESWaiter {
    public interface MessagesCallback {
        void onMessagesFetched(ArrayList<TLRPC.Message> messages);
    }

    public static String lastMessage(long chatId) {
        // Ensure valid chat ID
        if (chatId == 0) {
            return "";
        }

        // Retrieve the current account and account instance
        int currentAccount = UserConfig.selectedAccount;
        AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);

        // Create a runnable to fetch messages from storage
        Utilities.globalQueue.postRunnable(() -> {
            // Fetch messages using a callback
            accountInstance.getMessagesStorage().getMessages(chatId, 0, true, 0, 0, 0, 0, 0, 0, true, 0, 0, false, false, new MessagesStorage.MessagesCallback() {
                @Override
                public void onResult(ArrayList<TLRPC.Message> messages) {
                    // This callback is called once messages are fetched
                    AndroidUtilities.runOnUIThread(() -> {
                        if (messages != null && !messages.isEmpty()) {
                            TLRPC.Message lastMessage = messages.get(0);
                            String lastMessageText = lastMessage.message;

                            return lastMessageText;
                        }
                    });
                }
            });
        });
        return "";
    }
}