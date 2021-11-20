package com.choco_tyranno.team_tree.presentation;

import android.view.View;
import android.view.ViewTreeObserver;

import com.choco_tyranno.team_tree.Wrapper;

import java.util.function.Consumer;
import java.util.stream.Stream;

/*
* This class DependentUIResolver is created with consider of dynamic view attribute setting
* and the attribute is depends on another view has attribute not static.
*
* param {baseView : view what is handling dependency.
* action : dependent views attribute setting action what consuming baseView.
* }
* */
public class DependentUIResolver<T extends View> {
    private T baseView = null;
    private DependentUIResolverBuilder.DependentViewAction<T> action = null;

    public void resolve(){
        if (baseView==null)
            throw new RuntimeException("resolve() - baseView is null");
        if (action==null)
            throw new RuntimeException("resolve() - action is null");
        baseView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                baseView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (baseView.getId()==action.getBaseViewId())
                    Stream.of(action.actions).forEach(action->action.accept(baseView));
            }
        });
    }

    private void setBaseView(T view) {
        baseView = view;
    }

    private boolean isSetBaseView() {
        return baseView != null;
    }

    private boolean isSetAction() {
        return action != null;
    }

    private DependentUIResolver() {

    }

    private void setAction(DependentUIResolverBuilder.DependentViewAction<T> action) {
        this.action = action;
    }

    /*
    *
    * */
    public static class DependentUIResolverBuilder<T extends View> {
        private final Wrapper<DependentUIResolver<T>> instanceWrapper = new Wrapper<>();

        private void readyInstance() {
            if (!instanceWrapper.isContain())
                instanceWrapper.wrap(new DependentUIResolver<T>());
        }

        public DependentUIResolverBuilder<T> baseView(T view) {
            readyInstance();
            instanceWrapper.check().setBaseView(view);
            return this;
        }

        /*
        * Method 'with' has build code of DependentViewAction.
        *
        * Recommend :
        * Pass argument 'actions' using method reference the view method.
        * */
        @SafeVarargs
        public final DependentUIResolverBuilder<T> with(int viewId, Consumer<View>... actions) {
            readyInstance();
            instanceWrapper.check().setAction(
                    new DependentViewAction.DependentViewActionBuilder<T>().
                            baseViewId(viewId).actions(actions).build()
            );
            return this;
        }

        private boolean isBuildReady() {
            return instanceWrapper.isContain() && instanceWrapper.check().isSetBaseView() && instanceWrapper.check().isSetAction();
        }

        /*
        * Build method has a responsibility of checking build code completion.
        * */
        public DependentUIResolver<T> build(){
            if (isBuildReady()){
                return instanceWrapper.unwrap();
            }
            throw new RuntimeException("build fail -> trace : !isBuildReady");
        }


        /*
        * This class DependentViewAction is view attribute setting action container
        * with baseView id matchable.
        *
        * param{baseViewId : used when running action for baseView match.
        * actions : dependent views attribute setting action.
        * }
        * */
        private static class DependentViewAction<T extends View> {
            private int baseViewId = -1;
            private Consumer<View>[] actions = null;

            public int getBaseViewId(){return baseViewId;}

            private void setBaseViewId(int viewId) {
                this.baseViewId = viewId;
            }

            private void setActions(Consumer<View>[] actions) {
                this.actions = actions;
            }

            private boolean isSetBaseViewId() {
                return baseViewId != -1;
            }

            private boolean isSetActions() {
                return actions != null;
            }

            private static class DependentViewActionBuilder<T extends View> {
                private final Wrapper<DependentViewAction<T>> instanceWrapper = new Wrapper<>();

                private DependentViewActionBuilder() {
                }

                private DependentViewActionBuilder<T> baseViewId(int viewId) {
                    readyInstance();
                    instanceWrapper.check().setBaseViewId(viewId);
                    return this;
                }

                /*
                 * Recommend :
                 * Pass argument by method reference the view method.
                 * */
                private DependentViewActionBuilder<T> actions(Consumer<View>[] actions) {
                    readyInstance();
                    instanceWrapper.check().setActions(actions);
                    return this;
                }

                private void readyInstance() {
                    if (!instanceWrapper.isContain())
                        instanceWrapper.wrap(new DependentViewAction<T>());
                }

                private boolean isBuildReady() {
                    return instanceWrapper.isContain() && instanceWrapper.check().isSetActions() && instanceWrapper.check().isSetBaseViewId();
                }

                /*
                 * Build method has a responsibility of checking build code completion.
                 * */
                private DependentViewAction<T> build() {
                    if (isBuildReady())
                        return instanceWrapper.unwrap();
                    throw new RuntimeException("DependentViewActionBuilder build fail : !isBuildReady");
                }

            }
        }

    }

}
