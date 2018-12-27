package com.adroitandroid.chipcloud;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class ChipCloud extends FlowLayout implements ChipListener {

    public enum Mode {
        SINGLE, MULTI, REQUIRED, NONE
    }

    private Context context;
    private int chipHeight;
    private int selectedColor = -1;
    private int selectedFontColor = -1;
    private int unselectedColor = -1;
    private int unselectedFontColor = -1;
    private int backgroundDrawable = -1;
    private int selectTransitionMS = 750;
    private int deselectTransitionMS = 500;
    private Mode mode = Mode.SINGLE;
    private Gravity gravity = Gravity.LEFT;
    private Typeface typeface;
    private boolean allCaps;
    private int textSizePx = -1;
    private int verticalSpacing;
    private int minHorizontalSpacing;

    private ChipListener chipListener;

    public ChipCloud(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ChipCloud(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChipCloud, 0, 0);
        int arrayReference = -1;
        try {
            selectedColor = a.getColor(R.styleable.ChipCloud_selectedColor, -1);
            selectedFontColor = a.getColor(R.styleable.ChipCloud_selectedFontColor, -1);
            unselectedColor = a.getColor(R.styleable.ChipCloud_deselectedColor, -1);
            unselectedFontColor = a.getColor(R.styleable.ChipCloud_deselectedFontColor, -1);
            selectTransitionMS = a.getInt(R.styleable.ChipCloud_selectTransitionMS, 750);
            deselectTransitionMS = a.getInt(R.styleable.ChipCloud_deselectTransitionMS, 500);
            backgroundDrawable = a.getResourceId(R.styleable.ChipCloud_backgroundDrawable, -1);
            String typefaceString = a.getString(R.styleable.ChipCloud_typeface);
            if (typefaceString != null) {
                typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceString);
            }
            textSizePx = a.getDimensionPixelSize(R.styleable.ChipCloud_textSize,
                    getResources().getDimensionPixelSize(R.dimen.default_textsize));
            allCaps = a.getBoolean(R.styleable.ChipCloud_allCaps, false);
            int selectMode = a.getInt(R.styleable.ChipCloud_selectMode, 1);
            switch (selectMode) {
                case 0:
                    mode = Mode.SINGLE;
                    break;
                case 1:
                    mode = Mode.MULTI;
                    break;
                case 2:
                    mode = Mode.REQUIRED;
                    break;
                case 3:
                    mode = Mode.NONE;
                    break;
                default:
                    mode = Mode.SINGLE;
            }
            int gravityType = a.getInt(R.styleable.ChipCloud_gravity, 0);
            switch (gravityType) {
                case 0:
                    gravity = Gravity.LEFT;
                    break;
                case 1:
                    gravity = Gravity.RIGHT;
                    break;
                case 2:
                    gravity = Gravity.CENTER;
                    break;
                case 3:
                    gravity = Gravity.STAGGERED;
                    break;
                default:
                    gravity = Gravity.LEFT;
                    break;
            }
            minHorizontalSpacing = a.getDimensionPixelSize(R.styleable.ChipCloud_minHorizontalSpacing,
                    getResources().getDimensionPixelSize(R.dimen.min_horizontal_spacing));
            verticalSpacing = a.getDimensionPixelSize(R.styleable.ChipCloud_verticalSpacing,
                    getResources().getDimensionPixelSize(R.dimen.vertical_spacing));
            arrayReference = a.getResourceId(R.styleable.ChipCloud_labels, -1);

        } finally {
            a.recycle();
        }

        init();

        if (arrayReference != -1) {
            String[] labels = getResources().getStringArray(arrayReference);
            addChips(labels);
        }
    }

    @Override
    protected int getMinimumHorizontalSpacing() {
        return minHorizontalSpacing;
    }

    @Override
    protected int getVerticalSpacing() {
        return verticalSpacing;
    }

    @Override
    protected Gravity getGravity() {
        return gravity;
    }

    private void init() {
        chipHeight = getResources().getDimensionPixelSize(R.dimen.material_chip_height);
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setSelectedFontColor(int selectedFontColor) {
        this.selectedFontColor = selectedFontColor;
    }

    public void setUnselectedColor(int unselectedColor) {
        this.unselectedColor = unselectedColor;
    }

    public void setUnselectedFontColor(int unselectedFontColor) {
        this.unselectedFontColor = unselectedFontColor;
    }

    public void setSelectTransitionMS(int selectTransitionMS) {
        this.selectTransitionMS = selectTransitionMS;
    }

    public void setDeselectTransitionMS(int deselectTransitionMS) {
        this.deselectTransitionMS = deselectTransitionMS;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        for (int i = 0; i < getChildCount(); i++) {
            Chip chip = (Chip) getChildAt(i);
            chip.deselect();
            chip.setLocked(false);
        }
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setTextSize(int textSize) {
        this.textSizePx = textSize;
    }

    public void setAllCaps(boolean isAllCaps) {
        this.allCaps = isAllCaps;
    }

    public void setMinimumHorizontalSpacing(int spacingInPx) {
        this.minHorizontalSpacing = spacingInPx;
    }

    public void setVerticalSpacing(int spacingInPx) {
        this.verticalSpacing = spacingInPx;
    }

    public void setChipListener(ChipListener chipListener) {
        this.chipListener = chipListener;
    }

    public void addChips(String[] labels) {
        for (String label : labels) {
            addChip(label);
        }
    }

    public void addChip(String label) {
        Chip chip = new Chip.ChipBuilder().index(getChildCount())
                .label(label)
                .typeface(typeface)
                .textSize(textSizePx)
                .allCaps(allCaps)
                .selectedColor(selectedColor)
                .selectedFontColor(selectedFontColor)
                .backgroundDrawable(backgroundDrawable)
                .unselectedColor(unselectedColor)
                .unselectedFontColor(unselectedFontColor)
                .selectTransitionMS(selectTransitionMS)
                .deselectTransitionMS(deselectTransitionMS)
                .chipHeight(chipHeight)
                .chipListener(this)
                .mode(mode)
                .build(context);

        addView(chip);
    }

    public void setSelectedChip(int index) {
        Chip chip = (Chip) getChildAt(index);
        chip.select();
        if (mode == Mode.REQUIRED) {
            for (int i = 0; i < getChildCount(); i++) {
                Chip chipp = (Chip) getChildAt(i);
                if (i == index) {
                    chipp.setLocked(true);
                } else {
                    chipp.setLocked(false);
                }
            }
        }
    }

    @Override
    public void chipSelected(int index) {

        switch (mode) {
            case SINGLE:
            case REQUIRED:
                for (int i = 0; i < getChildCount(); i++) {
                    Chip chip = (Chip) getChildAt(i);
                    if (i == index) {
                        if (mode == Mode.REQUIRED) chip.setLocked(true);
                    } else {
                        chip.deselect();
                        chip.setLocked(false);
                    }
                }
                break;
            default:
                //
        }

        if (chipListener != null) {
            chipListener.chipSelected(index);
        }
    }

    @Override
    public void chipDeselected(int index) {
        if (chipListener != null) {
            chipListener.chipDeselected(index);
        }
    }

    public boolean isSelected(int index) {
        if (index > 0 && index < getChildCount()) {
            Chip chip = (Chip) getChildAt(index);
            return chip.isSelected();
        }
        return false;
    }

    //Apparently using the builder pattern to configure an object has been labelled a 'Bloch Builder'.
    public static class Configure {
        private ChipCloud chipCloud;
        private int selectedColor = -1;
        private int backgroundDrawable = -1;
        private int selectedFontColor = -1;
        private int deselectedColor = -1;
        private int deselectedFontColor = -1;
        private int selectTransitionMS = -1;
        private int deselectTransitionMS = -1;
        private Mode mode = null;
        private String[] labels = null;
        private ChipListener chipListener;
        private Gravity gravity = null;
        private Typeface typeface;
        private Boolean allCaps = null;
        private int textSize = -1;
        private int minHorizontalSpacing = -1;
        private int verticalSpacing = -1;

        public Configure chipCloud(ChipCloud chipCloud) {
            this.chipCloud = chipCloud;
            return this;
        }

        public Configure mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Configure selectedColor(int selectedColor) {
            this.selectedColor = selectedColor;
            return this;
        }

        public Configure selectedFontColor(int selectedFontColor) {
            this.selectedFontColor = selectedFontColor;
            return this;
        }

        public Configure deselectedColor(int deselectedColor) {
            this.deselectedColor = deselectedColor;
            return this;
        }

        public Configure deselectedFontColor(int deselectedFontColor) {
            this.deselectedFontColor = deselectedFontColor;
            return this;
        }

        public Configure selectTransitionMS(int selectTransitionMS) {
            this.selectTransitionMS = selectTransitionMS;
            return this;
        }

        public Configure deselectTransitionMS(int deselectTransitionMS) {
            this.deselectTransitionMS = deselectTransitionMS;
            return this;
        }

        public Configure labels(String[] labels) {
            this.labels = labels;
            return this;
        }

        public Configure chipListener(ChipListener chipListener) {
            this.chipListener = chipListener;
            return this;
        }

        public Configure gravity(Gravity gravity) {
            this.gravity = gravity;
            return this;
        }

        public Configure typeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        /**
         * @param textSize value in pixels as obtained from @{@link android.content.res.Resources#getDimensionPixelSize(int)}
         */
        public Configure textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Configure allCaps(boolean isAllCaps) {
            this.allCaps = isAllCaps;
            return this;
        }

        public Configure minHorizontalSpacing(int spacingInPx) {
            this.minHorizontalSpacing = spacingInPx;
            return this;
        }

        public Configure verticalSpacing(int spacingInPx) {
            this.verticalSpacing = spacingInPx;
            return this;
        }

        public void build() {
            chipCloud.removeAllViews();
            if (mode != null) chipCloud.setMode(mode);
            if (gravity != null) chipCloud.setGravity(gravity);
            if (typeface != null) chipCloud.setTypeface(typeface);
            if (textSize != -1) chipCloud.setTextSize(textSize);
            if (allCaps != null) chipCloud.setAllCaps(allCaps);
            if (selectedColor != -1) chipCloud.setSelectedColor(selectedColor);
            if (selectedFontColor != -1) chipCloud.setSelectedFontColor(selectedFontColor);
            if (deselectedColor != -1) chipCloud.setUnselectedColor(deselectedColor);
            if (deselectedFontColor != -1) chipCloud.setUnselectedFontColor(deselectedFontColor);
            if (selectTransitionMS != -1) chipCloud.setSelectTransitionMS(selectTransitionMS);
            if (deselectTransitionMS != -1) chipCloud.setDeselectTransitionMS(deselectTransitionMS);
            if (minHorizontalSpacing != -1) chipCloud.setMinimumHorizontalSpacing(minHorizontalSpacing);
            if (verticalSpacing != -1) chipCloud.setVerticalSpacing(verticalSpacing);
            if (backgroundDrawable != -1) chipCloud.setBackgroundDrawable(backgroundDrawable);
            chipCloud.setChipListener(chipListener);
            chipCloud.addChips(labels);
        }

        public void update() {
            int childCount = chipCloud.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Chip chip = (Chip) chipCloud.getChildAt(i);
                chip.setText(labels[i]);
                chip.invalidate();
            }
            chipCloud.invalidate();
            chipCloud.requestLayout();
        }
    }

    private void setBackgroundDrawable(int backgroundDrawable) {
        this.backgroundDrawable=backgroundDrawable;
    }
}
