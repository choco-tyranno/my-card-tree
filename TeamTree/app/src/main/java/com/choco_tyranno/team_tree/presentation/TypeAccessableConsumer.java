package com.choco_tyranno.team_tree.presentation;

import java.util.function.Consumer;

@FunctionalInterface
public interface TypeAccessableConsumer<T> extends Consumer<T> {

    @Override
    void accept(T t);

    @Override
    default Consumer<T> andThen(Consumer<? super T> after) {
        return null;
    }

    default T returnType(T type){
        return type;
    }
}
