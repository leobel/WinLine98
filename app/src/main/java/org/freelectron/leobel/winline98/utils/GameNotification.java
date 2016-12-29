package org.freelectron.leobel.winline98.utils;

import android.os.Message;

/**
 * Created by leobel on 12/29/16.
 */

public interface GameNotification {
    void sendBoardAlertHandler(Message message);
    void sendNextAlertHandler(Message message);
    void sendScoreAlertHandler(Message message);
    void sendEndAlertHandler(Message message);
}
