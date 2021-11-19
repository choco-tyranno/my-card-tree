package com.choco_tyranno.team_tree;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;


/*
* This class Box is single item container.
* Method 'unbox' is useful for return instance and auto remove reference immediately from this Box.class.
* */
public class Box<T>{
    T item;
    public T unbox(){
        final T unBoxedItem = item;
        item = null;
        return unBoxedItem;
    }

    public T check(){
        return item;
    }

    public void box(@NonNull T item){
        this.item = item;
    }

    public boolean isContain(){
        return item != null;
    }
}
