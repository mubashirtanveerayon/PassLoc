package com.loc.service.apputils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;


import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import services.generator.password.PasswordStrengthEvaluator;

// taken from https://stackoverflow.com/questions/61212174/horizontal-progress-bar-shift-between-colros-while-loading-for-password-streng
public class PasswordStrengthTextWatcher implements TextWatcher {



    private static final long ANIMATION_DURATION = 500;

    private LinearProgressIndicator progressBar;


    private int lastColor;

    public PasswordStrengthTextWatcher(LinearProgressIndicator progressBar) {
        this.progressBar = progressBar;


    }

    @Override
    public void beforeTextChanged(@Nullable CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(@Nullable CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(@Nullable Editable s) {
        if(s == null)return;
        int newColor = getColorForStrength(s.toString());
        if (lastColor != newColor) {
            // Animate progress
            ObjectAnimator.ofInt(progressBar, "progress", PasswordStrengthEvaluator.evaluatePasswordStrength(s.toString()) * 100)
                    .setDuration(ANIMATION_DURATION)
                    .start();

            // Animate color
            ColorStateList progressTint = progressBar.getProgressTintList();
            int currentColor = progressTint == null ? lastColor : progressTint.getDefaultColor();
            ValueAnimator colorAnim = ValueAnimator.ofArgb(currentColor,newColor)
                    .setDuration(ANIMATION_DURATION);
            colorAnim.addUpdateListener(anim -> {
                progressBar.setProgressTintList(new ColorStateList(
                        new int[][]{{0}}, new int[]{(int) anim.getAnimatedValue()}));
            });
            colorAnim.start();

            lastColor = newColor;
        }
    }

    private int getColorForStrength(String password) {
        float strength = PasswordStrengthEvaluator.evaluatePasswordStrength(password);

        if(strength>=0.8f){
            return 0x00af3f;
        }else if(strength>=0.6f){
            return 0x007fff;
        }else if(strength>=0.5f){
            return 0xffaf00;
        }else if(strength>=0.4f){
            return 0xff7f00;
        }else if(strength>=0.2f){
            return 0xff0000;
        }else{
            return Color.TRANSPARENT;
        }
    }



}