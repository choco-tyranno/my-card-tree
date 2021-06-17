package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.widget.Toast;

import java.util.Optional;

public class SingleToastManager {
    public static Store toastStore;

    public static void show(Toast toast) {
        if(!Optional.ofNullable(toastStore).isPresent())
            toastStore = new Store();
        cancelIfNecessary();
        toastStore.register(toast);
        toastStore.get().show();
    }

    public static void cancelIfNecessary(){
        if (!Optional.ofNullable(toastStore).isPresent())
            return;

        if (Optional.ofNullable(toastStore.get()).isPresent())
            toastStore.get().cancel();
    }

    public static void clear(){
        cancelIfNecessary();
        if (!Optional.ofNullable(toastStore).isPresent())
            return;
        toastStore.register(null);
        toastStore = null;
    }

    private static class Store {
        private Toast singleToast;

        private void register(Toast toast) {
            singleToast = toast;
        }

        private Toast get() {
            return singleToast;
        }

    }
}
