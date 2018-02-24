package com.alliedtech.lollibotapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;

public class AnimatedFloatingActionButton extends FloatingActionButton {

    private AnimatedVectorDrawableCompat[] drawables;
    private int currentDrawable = 0;
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
        drawables = new AnimatedVectorDrawableCompat[6];
        drawables[0] = AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_add_to_close);
        drawables[1] = AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_close_to_add);
        drawables[2] = AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_add_to_check);
        drawables[3] = AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_check_to_add);
        drawables[4] = AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_close_to_check);
        drawables[5] = AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.ic_animated_check_to_close);

        this.setOnClickListener(listener);
    }

    public void transition(final int resourceId, int newCurrentDrawable) {
        this.setImageDrawable(drawables[currentDrawable]);

        drawables[currentDrawable].registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                drawable.setCallback(null);

                fab.setImageResource(resourceId);
            }
        });

        drawables[currentDrawable].start();

        currentDrawable = newCurrentDrawable;
    }
}
