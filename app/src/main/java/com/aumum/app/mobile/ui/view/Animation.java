package com.aumum.app.mobile.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 3/10/2014.
 */
public class Animation {
    public static enum Duration {
        SHORT,
        MEDIUM,
        LONG;

        private long toMillis(Context context) {
            switch (this) {
                case LONG:
                    return context.getResources().getInteger(android.R.integer.config_longAnimTime);
                case MEDIUM:
                    return context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
                default:
                    return context.getResources().getInteger(android.R.integer.config_shortAnimTime);
            }
        }
    }

    private static ObjectAnimator getFadeInAnim(final View target, Duration duration) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(target, View.ALPHA, 0.0f, 1.0f);
        fadeIn.setDuration(duration.toMillis(target.getContext()));
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                target.setVisibility(View.VISIBLE);
            }
        });
        return fadeIn;
    }

    private static ObjectAnimator getFadeOutAnim(final View target, Duration duration) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(target, View.ALPHA, 1.0f, 0.0f);
        fadeOut.setDuration(duration.toMillis(target.getContext()));
        fadeOut.setInterpolator(new LinearInterpolator());
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                target.setVisibility(View.GONE);
            }
        });
        return fadeOut;
    }

    public static void fadeIn(final View target, Duration duration) {
        if (target == null || duration == null) {
            return;
        }
        getFadeInAnim(target, duration).start();
    }

    public static void fadeOut(final View target, Duration duration) {
        if (target == null || duration == null) {
            return;
        }
        getFadeOutAnim(target, duration).start();
    }

    public static void fadeInFadeOut(final View target, Duration duration) {
        if (target == null || duration == null) {
            return;
        }

        ObjectAnimator fadeIn = getFadeInAnim(target, duration);
        ObjectAnimator fadeOut = getFadeOutAnim(target, duration);

        // keep view visible for passed duration before fading it out
        fadeOut.setStartDelay(duration.toMillis(target.getContext()));

        AnimatorSet set = new AnimatorSet();
        set.play(fadeOut).after(fadeIn);
        set.start();
    }

    public static void scaleIn(final View target, Duration duration) {
        if (target == null || duration == null) {
            return;
        }

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, scaleX, scaleY);
        animator.setDuration(duration.toMillis(target.getContext()));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        if (target.getVisibility() != View.VISIBLE) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    target.setVisibility(View.VISIBLE);
                }
            });
        }

        animator.start();
    }

    public static void scaleInScaleOut(final View target, Duration duration) {
        if (target == null || duration == null) {
            return;
        }

        ObjectAnimator animX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0f, 1f);
        animX.setRepeatMode(ValueAnimator.REVERSE);
        animX.setRepeatCount(1);
        ObjectAnimator animY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0f, 1f);
        animY.setRepeatMode(ValueAnimator.REVERSE);
        animY.setRepeatCount(1);

        AnimatorSet set = new AnimatorSet();
        set.play(animX).with(animY);
        set.setDuration(duration.toMillis(target.getContext()));
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                target.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                target.setVisibility(View.GONE);
            }
        });
        set.start();
    }

    public static void animateTextView(final View target) {
        if (target == null) {
            return;
        }

        float endScale = 1.35f;

        ObjectAnimator animX = ObjectAnimator.ofFloat(target, View.SCALE_X, 1f, endScale);
        animX.setRepeatMode(ValueAnimator.REVERSE);
        animX.setRepeatCount(1);

        ObjectAnimator animY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 1f, endScale);
        animY.setRepeatMode(ValueAnimator.REVERSE);
        animY.setRepeatCount(1);

        AnimatorSet set = new AnimatorSet();
        set.play(animX).with(animY);

        long durationMillis = Duration.SHORT.toMillis(target.getContext());
        set.setDuration(durationMillis);
        set.setInterpolator(new AccelerateDecelerateInterpolator());

        set.start();
    }

    public static void flyIn(View target) {
        Context context = target.getContext();
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(context, R.anim.fly_in);
        if (animation==null)
            return;

        // add small overshoot for bounce effect
        animation.setInterpolator(new OvershootInterpolator(0.9f));
        long duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
        animation.setDuration((long)(duration * 1.5f));

        target.startAnimation(animation);
        target.setVisibility(View.VISIBLE);
    }

    public static void flyOut(final View target) {
        android.view.animation.Animation.AnimationListener listener = new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) { }
            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                target.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) { }
        };
        startAnimation(target, R.anim.fly_out, listener);
    }

    public static void startAnimation(View target, int aniResId) {
        startAnimation(target, aniResId, null);
    }
    public static void startAnimation(View target, int aniResId, android.view.animation.Animation.AnimationListener listener) {
        if (target==null)
            return;
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(target.getContext(), aniResId);
        if (animation==null)
            return;
        if (listener!=null)
            animation.setAnimationListener(listener);

        target.startAnimation(animation);
    }

    public static void flyIn(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        flyIn(view);
    }

    public static void scaleIn(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        scaleIn(view, Duration.MEDIUM);
    }

    public static void animateIconBar(View view, boolean isAnimatingIn) {
        if (isAnimatingIn && view.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!isAnimatingIn && view.getVisibility() != View.VISIBLE) {
            return;
        }

        final android.view.animation.Animation animation;
        if (isAnimatingIn) {
            animation = new TranslateAnimation(android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f, android.view.animation.Animation.RELATIVE_TO_SELF,
                    1.0f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f);
        } else {
            animation = new TranslateAnimation(android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f, android.view.animation.Animation.RELATIVE_TO_SELF,
                    0.0f, android.view.animation.Animation.RELATIVE_TO_SELF, 1.0f);
        }

        animation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));

        view.clearAnimation();
        view.startAnimation(animation);
        view.setVisibility(isAnimatingIn ? View.VISIBLE : View.GONE);
    }
}
