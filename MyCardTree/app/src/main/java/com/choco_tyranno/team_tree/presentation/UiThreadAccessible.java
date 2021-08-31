package com.choco_tyranno.team_tree.presentation;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import java.util.Optional;

public interface UiThreadAccessible {
    default void runOnUiThread(Runnable action, Context context) {
        if (!(context instanceof Activity))
            throw new RuntimeException("[Interface] - UiThreadAccessible #runOnUiThread#context is not instance of Activity.");
        ((Activity) context).runOnUiThread(action);
    }

    default void throwToMainHandlerWithDelay(Runnable action, int delay, Context context) {
        Handler mainHandler = ((MainCardActivity) context).getMainHandler();
        if (!Optional.ofNullable(mainHandler).isPresent()) {
            throw new RuntimeException("[Interface] - UiThreadAccessible #throwToMainHandlerWithDelay - getMainHandler is null");
        }
        mainHandler.postDelayed(action, delay);
    }
}
