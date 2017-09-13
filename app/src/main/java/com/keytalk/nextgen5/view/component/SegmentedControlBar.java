package com.keytalk.nextgen5.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.keytalk.nextgen5.R;

/*
 * Class  :  SegmentedControlBar
 * Description : SegmentedControlBar class for showing customized radio buttons
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class SegmentedControlBar  extends RadioGroup implements RadioGroup.OnCheckedChangeListener {

    public interface SegmentedButtonListener {

        /**
         * @param buttonValue
         */
        String onSegmentButtonClicked(View view, String buttonValue);
    }

    private RadioButton radioButton1;
    private RadioButton radioButton3;
    private Drawable backgroundSelected;
    private int lineHeightUnselected;
    private Drawable backgroundUnselected;
    private SegmentedButtonListener mSegmentBtnListener;

    public SegmentedControlBar(Context context) {
        this(context, null);
    }

    public SegmentedControlBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentedControlBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init(attrs);
    }

    public Drawable getBackgroundSelected() {
        return backgroundSelected;
    }

    public int getLineHeightUnselected() {
        return lineHeightUnselected;
    }

    private void init(AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.segmented_button_bar, this, true);
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs,
                    R.styleable.SegmentedControlButton);
            if (backgroundSelected == null) {
                Drawable d = attributes
                        .getDrawable(R.styleable.SegmentedControlButton_backgroundSelected);
                backgroundSelected = d == null ? getBackground() : d;
            }
            if (backgroundUnselected == null) {
                backgroundUnselected = getBackground();
            }
            radioButton1 = (RadioButton)findViewById(R.id.radio_btn_1);
            radioButton3 = (RadioButton)findViewById(R.id.radio_btn_3);
            setCheckedStatus(R.id.radio_btn_3);
            setOnCheckedChangeListener(this);
            attributes.recycle();
        }
    }

    public final void setCheckedStatus(int checkedId) {
        if (checkedId == R.id.radio_btn_1) {
            radioButton1.setChecked(true);
            radioButton3.setChecked(false);
        } else if (checkedId == R.id.radio_btn_3) {
            radioButton1.setChecked(false);
            radioButton3.setChecked(true);
        }
    }

    public final void setTextStatus(String radioButtonText1 , String radioButtonText3) {
        radioButton1.setText(radioButtonText1);
        radioButton3.setText(radioButtonText3);
    }

    public final void setVisibilityStatus(int visibility1,  int visibility3) {
        radioButton1.setVisibility(visibility1);
        radioButton3.setVisibility(visibility3);
    }

    public final void setClickableStatus(boolean visibility1, boolean visibility3) {
        radioButton1.setClickable(visibility1);
        radioButton3.setClickable(visibility3);
    }
/*    public final void setHeight(int btnHeight){
        android.view.ViewGroup.LayoutParams btn1params = radioButton1.getLayoutParams();
        btn1params.height = PixelSize.getDimensionInDP(btnHeight);
        radioButton1.setLayoutParams(btn1params);
        android.view.ViewGroup.LayoutParams btn3params = radioButton3.getLayoutParams();
        btn3params.height = PixelSize.getDimensionInDP(btnHeight);
        radioButton3.setLayoutParams(btn3params);
    }
    public final void setWidth(int btn1Width,int btn3Width){
        android.view.ViewGroup.LayoutParams btn1params = radioButton1.getLayoutParams();
        btn1params.width = PixelSize.getDimensionInDP(btn1Width);
        radioButton1.setLayoutParams(btn1params);
        android.view.ViewGroup.LayoutParams btn3params = radioButton3.getLayoutParams();
        btn3params.width = PixelSize.getDimensionInDP(btn3Width);
        radioButton3.setLayoutParams(btn3params);
    }*/

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String value = null;
        View view = getViewById(group.getCheckedRadioButtonId());
        if (checkedId == R.id.radio_btn_1) {
            value = getResources().getString(R.string.embedded_browser).toString();
        } else if (checkedId == R.id.radio_btn_3) {
            value = getResources().getString(R.string.native_browser).toString();
        }
        if (mSegmentBtnListener != null) {
            mSegmentBtnListener.onSegmentButtonClicked(view, value);
        }
    }

    private RadioButton getViewById(int id) {
        switch (id) {
            case R.id.radio_btn_1:
                return radioButton1;
            case R.id.radio_btn_3:
                return radioButton3;
            default:
                return null;
        }
    }

    public void setSegmentBtnListener(SegmentedButtonListener segmentButtonListener) {
        mSegmentBtnListener = segmentButtonListener;
    }
}
