package com.adroitandroid.chipcloud;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

public class Chip extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

    private int index = -1;
    private boolean selected = false;
    private ChipListener listener = null;
    private int selectedFontColor = -1;
    private int backgroundDrawable = -1;
    private int unselectedFontColor = -1;
    private TransitionDrawable crossfader;
    private int selectTransitionMS = 750;
    private int deselectTransitionMS = 500;
    private boolean isLocked = false;
    private ChipCloud.Mode mode;

    public void setChipListener(ChipListener listener) {
        this.listener = listener;
    }

    public Chip(Context context) {
        super(context);
        init();
    }

    public Chip(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Chip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void initChip(Context context, int index, String label, Typeface typeface, int textSizePx,
                         boolean allCaps, int selectedColor, int selectedFontColor, int unselectedColor,
                         int unselectedFontColor, ChipCloud.Mode mode,int backgroundDrawable) {

        this.index = index;
        this.selectedFontColor = selectedFontColor;
        this.unselectedFontColor = unselectedFontColor;
        this.mode = mode;
        this.backgroundDrawable=backgroundDrawable;

        Drawable selectedDrawable = ContextCompat.getDrawable(context, R.drawable.chip_selected);

        if (selectedColor == -1) {
            selectedDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.dark_grey), PorterDuff.Mode.MULTIPLY));
        } else {
            selectedDrawable.setColorFilter(new PorterDuffColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY));
        }

        if (selectedFontColor == -1) {
            this.selectedFontColor = ContextCompat.getColor(context, R.color.white);
        }

        Drawable unselectedDrawable = ContextCompat.getDrawable(context, R.drawable.chip_selected);
        if (unselectedColor == -1) {
            unselectedDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.light_grey), PorterDuff.Mode.MULTIPLY));
        } else {
            unselectedDrawable.setColorFilter(new PorterDuffColorFilter(unselectedColor, PorterDuff.Mode.MULTIPLY));
        }

        if (unselectedFontColor == -1) {
            this.unselectedFontColor = ContextCompat.getColor(context, R.color.chip);
        }

        Drawable backgrounds[] = new Drawable[2];
        backgrounds[0] = unselectedDrawable;
        backgrounds[1] = selectedDrawable;

        crossfader = new TransitionDrawable(backgrounds);

        //Bug reported on KitKat where padding was removed, so we read the padding values then set again after setting background
        int leftPad = getPaddingLeft();
        int topPad = getPaddingTop();
        int rightPad = getPaddingRight();
        int bottomPad = getPaddingBottom();

        setBackgroundCompat(crossfader);

        setPadding(leftPad, topPad, rightPad, bottomPad);

        setText(label);
        unselect();

        if (typeface != null) {
            setTypeface(typeface);
        }
        setAllCaps(allCaps);
        if (textSizePx > 0) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);
        }
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setSelectTransitionMS(int selectTransitionMS) {
        this.selectTransitionMS = selectTransitionMS;
    }

    public void setDeselectTransitionMS(int deselectTransitionMS) {
        this.deselectTransitionMS = deselectTransitionMS;
    }

    private void init() {
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mode != ChipCloud.Mode.NONE)
            if (selected && !isLocked) {
                //set as unselected
                unselect();
                if (listener != null) {
                    listener.chipDeselected(index);
                }
            } else if (!selected) {
                //set as selected
                crossfader.startTransition(selectTransitionMS);

                setTextColor(selectedFontColor);
                if (listener != null) {
                    listener.chipSelected(index);
                }
            }

        selected = !selected;
    }

    public void select() {
        selected = true;
        crossfader.startTransition(selectTransitionMS);
        setTextColor(selectedFontColor);
        if (listener != null) {
            listener.chipSelected(index);
        }
    }

    private void unselect() {
        if (selected) {
            crossfader.reverseTransition(deselectTransitionMS);
        } else {
            crossfader.resetTransition();
        }

        setTextColor(unselectedFontColor);
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(Drawable background) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(background);
        } else {
            setBackground(background);
        }
        if (backgroundDrawable != -1)
            setBackgroundResource(backgroundDrawable);
    }

    public void deselect() {
        unselect();
        selected = false;
    }

    public static class ChipBuilder {
        private int index;
        private String label;
        private Typeface typeface;
        private int textSizePx;
        private boolean allCaps;
        private int selectedColor;
        private int selectedFontColor;
        private int unselectedColor;
        private int unselectedFontColor;
        private int chipHeight;
        private int selectTransitionMS = 750;
        private int deselectTransitionMS = 500;

        private ChipListener chipListener;
        private ChipCloud.Mode mode;
        private int backgroundDrawable;

        public ChipBuilder index(int index) {
            this.index = index;
            return this;
        }

        public ChipBuilder selectedColor(int selectedColor) {
            this.selectedColor = selectedColor;
            return this;
        }

        public ChipBuilder selectedFontColor(int selectedFontColor) {
            this.selectedFontColor = selectedFontColor;
            return this;
        }

        public ChipBuilder unselectedColor(int unselectedColor) {
            this.unselectedColor = unselectedColor;
            return this;
        }

        public ChipBuilder unselectedFontColor(int unselectedFontColor) {
            this.unselectedFontColor = unselectedFontColor;
            return this;
        }

        public ChipBuilder label(String label) {
            this.label = label;
            return this;
        }

        public ChipBuilder typeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public ChipBuilder allCaps(boolean allCaps) {
            this.allCaps = allCaps;
            return this;
        }

        public ChipBuilder textSize(int textSizePx) {
            this.textSizePx = textSizePx;
            return this;
        }

        public ChipBuilder chipHeight(int chipHeight) {
            this.chipHeight = chipHeight;
            return this;
        }

        public ChipBuilder chipListener(ChipListener chipListener) {
            this.chipListener = chipListener;
            return this;
        }

        public ChipBuilder mode(ChipCloud.Mode mode) {
            this.mode = mode;
            return this;
        }

        public ChipBuilder selectTransitionMS(int selectTransitionMS) {
            this.selectTransitionMS = selectTransitionMS;
            return this;
        }

        public ChipBuilder deselectTransitionMS(int deselectTransitionMS) {
            this.deselectTransitionMS = deselectTransitionMS;
            return this;
        }

        public Chip build(Context context) {
            Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.chip, null);
            chip.initChip(context, index, label, typeface, textSizePx, allCaps, selectedColor,
                    selectedFontColor, unselectedColor, unselectedFontColor, mode,backgroundDrawable);
            chip.setSelectTransitionMS(selectTransitionMS);
            chip.setDeselectTransitionMS(deselectTransitionMS);
            chip.setChipListener(chipListener);
            chip.setHeight(chipHeight);
            return chip;
        }

        public ChipBuilder backgroundDrawable(int backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
            return this;

        }
    }
}
