package com.choco_tyranno.team_tree;

import android.util.Log;

public interface Logger {

    static void hotfixMessage(String msg){
        Log.d("!!hotfix",msg);
    }
    static void message(String msg) {
        Log.d("!!!:", msg);
    }

}
