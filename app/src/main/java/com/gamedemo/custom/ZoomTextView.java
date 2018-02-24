package com.gamedemo.custom;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import com.gamedemo.BuildConfig;

public class ZoomTextView extends android.support.v7.widget.AppCompatTextView {

    float minScale = 1.3f;
    float maxScale = 5f;
    
    
    //private float mScaleSpan = 1.0f;
    private float mScaleFactor = 1.0f;
    
    ScaleGestureDetector mScaleDetector;
    
    TextView tv;
	
    public ZoomTextView(Context context) {
      super(context);
      tv = this;
      this.setTypeface(setFount(context));
      this.invalidate();
      sharedConstructing(context);
    }

    public ZoomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tv = this;
        this.setTypeface(setFount(context));
        this.invalidate();
        sharedConstructing(context);
    }

    public ZoomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        tv = this;
        this.setTypeface(setFount(context));
        this.invalidate();
        sharedConstructing(context);
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} catch (IndexOutOfBoundsException e) {
			fixOnMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}	
    
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
    
    private void fixOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		CharSequence text = getText();
		if (text instanceof Spanned) {
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			fixSpannedWithSpaces(builder, widthMeasureSpec, heightMeasureSpec);
		} else {
			if (BuildConfig.DEBUG) {
				Log.d("", "The text isn't a Spanned");
			}
			fallbackToString(widthMeasureSpec, heightMeasureSpec);
		}
	}
 
	/**
	 * Add spaces around spans until the text is fixed, and then removes the
	 * unneeded spaces
	 */
	private void fixSpannedWithSpaces(SpannableStringBuilder builder, int widthMeasureSpec, int heightMeasureSpec) {
		long startFix = System.currentTimeMillis();
 
		FixingResult result = addSpacesAroundSpansUntilFixed(builder, widthMeasureSpec, heightMeasureSpec);
 
		if (result.fixed) {
			removeUnneededSpaces(widthMeasureSpec, heightMeasureSpec, builder, result);
		} else {
			fallbackToString(widthMeasureSpec, heightMeasureSpec);
		}
 
		if (BuildConfig.DEBUG) {
			long fixDuration = System.currentTimeMillis() - startFix;
			Log.d("", "fixSpannedWithSpaces() duration in ms: " + fixDuration);
		}
	}
 
	@SuppressLint("LongLogTag")
	private FixingResult addSpacesAroundSpansUntilFixed(SpannableStringBuilder builder, int widthMeasureSpec, int heightMeasureSpec) {
 
		Object[] spans = builder.getSpans(0, builder.length(), Object.class);
		List<Object> spansWithSpacesBefore = new ArrayList<Object>(spans.length);
		List<Object> spansWithSpacesAfter = new ArrayList<Object>(spans.length);
 
		for (Object span : spans) {
			int spanStart = builder.getSpanStart(span);
			if (isNotSpace(builder, spanStart - 1)) {
				builder.insert(spanStart, " ");
				spansWithSpacesBefore.add(span);
			}
 
			int spanEnd = builder.getSpanEnd(span);
			if (isNotSpace(builder, spanEnd)) {
				builder.insert(spanEnd, " ");
				spansWithSpacesAfter.add(span);
			}
 
			try {
				setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
				return FixingResult.fixed(spansWithSpacesBefore, spansWithSpacesAfter);
			} catch (IndexOutOfBoundsException notFixed) {
				Log.e("IndexOutOfBoundsException", ":"+notFixed.getMessage());

			}
		}
		if (BuildConfig.DEBUG) {
			Log.d("", "Could not fix the Spanned by adding spaces around spans");
		}
		return FixingResult.notFixed();
	}
 
	private boolean isNotSpace(CharSequence text, int where) {
		return text.charAt(where) != ' ';
	}
 
	private void setTextAndMeasure(CharSequence text, int widthMeasureSpec, int heightMeasureSpec) {
		setText(text);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
 
	private void removeUnneededSpaces(int widthMeasureSpec, int heightMeasureSpec, SpannableStringBuilder builder, FixingResult result) {
 
		for (Object span : result.spansWithSpacesAfter) {
			int spanEnd = builder.getSpanEnd(span);
			builder.delete(spanEnd, spanEnd + 1);
			try {
				setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
			} catch (IndexOutOfBoundsException ignored) {
				builder.insert(spanEnd, " ");
			}
		}
 
		boolean needReset = true;
		for (Object span : result.spansWithSpacesBefore) {
			int spanStart = builder.getSpanStart(span);
			builder.delete(spanStart - 1, spanStart);
			try {
				setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
				needReset = false;
			} catch (IndexOutOfBoundsException ignored) {
				needReset = true;
				int newSpanStart = spanStart - 1;
				builder.insert(newSpanStart, " ");
			}
		}
 
		if (needReset) {
			setText(builder);
//			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
 
	private void fallbackToString(int widthMeasureSpec, int heightMeasureSpec) {
		if (BuildConfig.DEBUG) {
			Log.d("", "Fallback to unspanned text");
		}
		String fallbackText = getText().toString();
		setTextAndMeasure(fallbackText, widthMeasureSpec, heightMeasureSpec);
	}

    private Typeface setFount(Context context){
    	Typeface face=Typeface.createFromAsset(context.getAssets(), "font/MyriadPro-Bold.otf");
    	return face;
    }
    
    private void sharedConstructing(Context context) {
        super.setClickable(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        
        setOnTouchListener(new OnTouchListener() {
 
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                mScaleDetector.onTouchEvent(event);
                return true; // indicate event was handled
            }
 
        });
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			//mScaleSpan = detector.getCurrentSpan();
			//Log.v("distance between fingers", String.valueOf(mScaleSpan));
			mScaleFactor *= detector.getScaleFactor();
			Log.v("ScaleFactor", String.valueOf(mScaleFactor));
			if(mScaleFactor > minScale && mScaleFactor < maxScale){
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mScaleFactor*10);
				//CIMMAplication.putFontSize(mScaleFactor*10);
//				System.gc();
			} 
			return true;
		}
	}
       
    private static class FixingResult {
		public final boolean fixed;
		public final List<Object> spansWithSpacesBefore;
		public final List<Object> spansWithSpacesAfter;
 
		public static FixingResult fixed(List<Object> spansWithSpacesBefore, List<Object> spansWithSpacesAfter) {
			return new FixingResult(true, spansWithSpacesBefore, spansWithSpacesAfter);
		}
 
		public static FixingResult notFixed() {
			return new FixingResult(false, null, null);
		}
 
		private FixingResult(boolean fixed, List<Object> spansWithSpacesBefore, List<Object> spansWithSpacesAfter) {
			this.fixed = fixed;
			this.spansWithSpacesBefore = spansWithSpacesBefore;
			this.spansWithSpacesAfter = spansWithSpacesAfter;
		}
	}
    
}