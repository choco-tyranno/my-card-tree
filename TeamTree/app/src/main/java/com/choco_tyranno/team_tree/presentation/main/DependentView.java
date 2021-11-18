package com.choco_tyranno.team_tree.presentation.main;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public interface DependentView {
    AtomicBoolean ready = new AtomicBoolean(false);
    Queue<Runnable> attributeSettingActions = new LinkedList<>();

    /*
    * Recommend :
    * Use in Constructor.
    * For making this method ready() act like keyword private,
    * add <code> if(ready.get()) return; </code> in 1st line of this method body.
    * */
    void ready();

    default void postAttributeSettingAction(Runnable action){
        if (!ready.get()) {
            attributeSettingActions.offer(action);
            return;
        }
        action.run();
    }
}
