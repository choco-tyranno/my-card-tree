package com.choco_tyranno.team_tree.presentation;

import android.view.View;

import java.util.function.Consumer;

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

    private void setBaseView(T view) {
        baseView = view;
    }

    public View getBaseView() {
        return baseView;
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

    public static class DependentUIResolverBuilder<T extends View> {
        private DependentUIResolver<T> instance;

        private void readyInstance() {
            if (instance == null)
                instance = new DependentUIResolver<T>();
        }

        public DependentUIResolverBuilder<T> baseView(T view) {
            readyInstance();
            instance.setBaseView(view);
            return this;
        }

        /*
        * Method 'with' has build code of DependentViewAction.
        *
        * Recommend :
        * Pass argument 'actions' using method reference the view method.
        * */
        @SafeVarargs
        public final DependentUIResolverBuilder<T> with(int viewId, Consumer<T>... actions) {
            readyInstance();
            instance.setAction(
                    new DependentViewAction.DependentViewActionBuilder<T>().
                            baseViewId(viewId).actions(actions).build()
            );
            return this;
        }

        private boolean isBuildReady() {
            return instance != null && instance.isSetBaseView() && instance.isSetAction();
        }

        /*
        * Build method has a responsibility of checking build code completion.
        * */
        public DependentUIResolver<T> build() {
            if (isBuildReady())
                return instance;
            throw new RuntimeException("build fail -> trace : !isBuildReady");
        }


        /*
        * This class DependentViewAction is view attribute setting action container
        * with baseView id matchable.
        *
        * param{view}
        * */
        private static class DependentViewAction<T extends View> {
            private int baseViewId = -1;
            private Consumer<T>[] actions = null;

            private void setBaseViewId(int viewId) {
                this.baseViewId = viewId;
            }

            private void setActions(Consumer<T>[] actions) {
                this.actions = actions;
            }

            private boolean isSetBaseViewId() {
                return baseViewId != -1;
            }

            private boolean isSetActions() {
                return actions != null;
            }

            private static class DependentViewActionBuilder<T extends View> {
                private DependentViewAction<T> instance;

                private DependentViewActionBuilder() {
                }

                private DependentViewActionBuilder<T> baseViewId(int viewId) {
                    readyInstance();
                    instance.baseViewId = viewId;
                    return this;
                }

                /*
                 * Recommend :
                 * Pass argument by method reference the view method.
                 * */
                private DependentViewActionBuilder<T> actions(Consumer<T>[] actions) {
                    readyInstance();
                    instance.actions = actions;
                    return this;
                }

                private void readyInstance() {
                    if (instance == null)
                        instance = new DependentViewAction<T>();
                }

                private boolean isBuildReady() {
                    return instance != null && instance.isSetActions() && instance.isSetBaseViewId();
                }

                /*
                 * Build method has a responsibility of checking build code completion.
                 * */
                private DependentViewAction<T> build() {
                    if (isBuildReady())
                        return instance;
                    throw new RuntimeException("DependentViewActionBuilder build fail : !isBuildReady");
                }

            }
        }

    }

}
