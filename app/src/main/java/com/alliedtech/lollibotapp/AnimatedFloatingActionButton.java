package com.alliedtech.lollibotapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;

public class AnimatedFloatingActionButton extends FloatingActionButton {

    private FabState currentState = FabState.ADD;
    private HashMap<StateTransition, AnimatedVectorDrawableCompat> drawables;
    private AnimatedFloatingActionButton fab = this;

    public AnimatedFloatingActionButton(Context context) {
        super(context);
    }

    public AnimatedFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setUpDrawables(View.OnClickListener listener) {
        StateTransition a2ch = new StateTransition(FabState.ADD, FabState.CHECK);
        StateTransition a2cl = new StateTransition(FabState.ADD, FabState.CLOSE);
        StateTransition cl2a = new StateTransition(FabState.CLOSE, FabState.ADD);
        StateTransition cl2ch = new StateTransition(FabState.CLOSE, FabState.CHECK);
        StateTransition ch2a = new StateTransition(FabState.CHECK, FabState.ADD);
        StateTransition ch2cl = new StateTransition(FabState.CHECK, FabState.CLOSE);
        drawables = new HashMap<>();
        drawables.put(a2ch, AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_add_to_check));
        drawables.put(a2cl, AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_add_to_close));
        drawables.put(cl2a, AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_close_to_add));
        drawables.put(cl2ch, AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_close_to_check));
        drawables.put(ch2a, AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_check_to_add));
        drawables.put(ch2cl, AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_check_to_close));

        this.setOnClickListener(listener);
    }

    public void transition(final int resourceId, FabState to) {
        if (to == currentState)
            return;

        StateTransition transition = new StateTransition(currentState, to);
        this.setImageDrawable(drawables.get(transition));

        drawables.get(transition).registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                drawable.setCallback(null);

                fab.setImageResource(resourceId);
            }
        });

        drawables.get(transition).start();

        currentState = to;
    }

    class StateTransition {

        private FabState from;
        private FabState to;

        StateTransition(FabState from, FabState to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            switch(from) {
                case ADD:
                    return to == FabState.CLOSE ? 5 : 4;
                case CHECK:
                    return to == FabState.ADD ? 3 : 2;
                case CLOSE:
                    return to == FabState.ADD ? 1 : 0;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof StateTransition) && hashCode() == obj.hashCode();
        }
    }
}
