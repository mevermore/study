package com.smasher.draw.activity;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.smasher.draw.R;
import com.smasher.draw.view.BlurMaskFilterView;
import com.smasher.draw.view.RhythmView;
import com.smasher.draw.view.SuccessView;
import com.smasher.oa.core.utils.DensityUtil;
import com.smasher.oa.core.utils.TintUtil;


/**
 * @author matao
 */
public class DrawableActivity extends AppCompatActivity {

    private BlurMaskFilterView mBlurMask;
    private BlurMaskFilterView mBlurMask1;
    private SuccessView mSuccess;
    private RhythmView mRhythmView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        initView();
        initState();
        initListener();
    }

    private void initListener() {
        mBlurMask.setOnClickListener(mblurMaskListener1);
        mBlurMask1.setOnClickListener(mblurMaskListener2);
    }

    private void initState() {
        Drawable arrowOrigin = ContextCompat.getDrawable(this, R.drawable.icon_arrow_down_black);
        Drawable arrow = null;
        if (arrowOrigin != null) {
            arrow = TintUtil.tintDrawable(arrowOrigin, Color.WHITE);
        }
        if (arrow != null) {
            int width = DensityUtil.dip2px(this, 12);
            int height = DensityUtil.dip2px(this, 8);
            arrow.setBounds(0, 0, width, height);
        }
        Drawable background = ContextCompat.getDrawable(this, R.drawable.selector_blurmask_button);

        mBlurMask1.setCompoundDrawablePadding(DensityUtil.dip2px(this, 40));
        mBlurMask1.setCompoundDrawables(null, null, arrow, null);
        StateListDrawable listDrawable = getStateListDrawable();
    }

    private StateListDrawable getStateListDrawable() {
        float[] radiusArray = new float[8];
        for (int i = 0; i < 8; i++) {
            radiusArray[i] = DensityUtil.dip2px(this, 20);
        }
        int mTextPaddingLeft = DensityUtil.dip2px(this, 15);
        StateListDrawable listDrawable = new StateListDrawable();
        RoundRectShape normalShape = new RoundRectShape(radiusArray, null, null);
        ShapeDrawable normalDrawable = new ShapeDrawable(normalShape);
        normalDrawable.getPaint().setColor(Color.parseColor("#ED424B"));
        MaskFilter mMaskFilter = new BlurMaskFilter(mTextPaddingLeft, BlurMaskFilter.Blur.OUTER);
        normalDrawable.getPaint().setMaskFilter(mMaskFilter);
        normalDrawable.setPadding(mTextPaddingLeft, mTextPaddingLeft, mTextPaddingLeft, mTextPaddingLeft);
        int[] stateSet2 = new int[]{};
        listDrawable.addState(stateSet2, normalDrawable);
        return listDrawable;
    }

    private void initView() {

        try {
            mBlurMask = findViewById(R.id.blurMask);
            mBlurMask1 = findViewById(R.id.blurMask1);
            mSuccess = findViewById(R.id.success);
            mRhythmView = findViewById(R.id.rhythmView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    View.OnClickListener mblurMaskListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRhythmView.showAnimation();
        }
    };


    View.OnClickListener mblurMaskListener2 = v -> doActions();


    private void doActions() {
        mSuccess.showAnimation();

        Animation transAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0f,
                Animation.RELATIVE_TO_SELF,
                1f,
                Animation.RELATIVE_TO_SELF,
                0f,
                Animation.RELATIVE_TO_SELF,
                1f
        );
        transAnimation.setRepeatMode(Animation.REVERSE);
        transAnimation.setRepeatCount(5);


        Animation alpha = new AlphaAnimation(0.5f, 1f);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setRepeatCount(5);


        Animation rotate = new RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f

        );
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setRepeatCount(5);


        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(500);
        set.setFillEnabled(true);
        set.setFillAfter(true);
        set.addAnimation(alpha);
        set.addAnimation(rotate);

        AnimatorSet set1 = new AnimatorSet();
        ObjectAnimator trans = ObjectAnimator.ofFloat(
                mBlurMask,
                "translationX",
                mBlurMask.getTranslationX(), 500f, mBlurMask.getTranslationX());
        trans.setRepeatMode(ObjectAnimator.REVERSE);
        trans.setRepeatCount(0);

        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(
                mBlurMask,
                "rotation",
                mBlurMask.getRotation(), 720, mBlurMask.getRotation()
        );
        rotateAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        rotateAnimator.setRepeatCount(0);

        set1.playTogether(trans, rotateAnimator);
        set1.setDuration(2000);
        set1.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}