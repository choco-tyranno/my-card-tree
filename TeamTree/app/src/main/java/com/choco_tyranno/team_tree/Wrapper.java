package com.choco_tyranno.team_tree;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;


/*
* This class Wrapper is single item container.
* Method 'unwrap' is useful for return instance and auto remove reference immediately from this Wrapper.class.
* */
public class Wrapper<T>{
    T item;
    public T unwrap(){
        final T unwrappedItem = item;
        item = null;
        return unwrappedItem;
    }

    public T check(){
        return item;
    }

    public void wrap(@NonNull T item){
        this.item = item;
    }

    public boolean isContain(){
        return item != null;
    }
}
