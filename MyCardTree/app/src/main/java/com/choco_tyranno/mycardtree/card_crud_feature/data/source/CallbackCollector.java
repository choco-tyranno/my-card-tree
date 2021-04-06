package com.choco_tyranno.mycardtree.card_crud_feature.data.source;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CallbackCollector {
    public static CallbackCollector instance;

    private final HashMap<String, Object> callbacks;

    public CallbackCollector() {
        this.callbacks = new HashMap<>();
    }

    public void collect(String key, Object callback){
        this.callbacks.put(key, callback);
    }

    public Object consume(String key){
        return this.callbacks.get(key);
    }

    public boolean hasCallback(String key){
        return this.callbacks.containsKey(key);
    }

    public CallbackCollector getInstance(){
        return this;
    }


}
