package com.klokisoft.mathdokuext.painter;

import com.klokisoft.mathdokuext.painter.Painter.GridTheme;

import android.graphics.Paint;
import android.graphics.Typeface;

public class CagePainter extends BorderPainter {

	private final Paint mBorderSelectedPaint;
	private final Paint mBorderBadMathPaint;
	private final Paint mBorderSelectedBadMathPaint;

	private final Paint mTextPaint;
	private float mTextLeftOffset;
	private float mTextBottomOffset;

	/**
	 * Creates a new instance of {@link CagePainter}.
	 * 
	 * @param painter
	 *            The global container for all painters.
	 */
	public CagePainter(Painter painter) {
		super(painter);

		mBorderPaint = new Paint();
		mBorderSelectedPaint = new Paint();
		mBorderBadMathPaint = new Paint();
		mBorderSelectedBadMathPaint = new Paint();
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(14);
	}

	@Override
	public void setTheme(GridTheme theme) {
		// Set text paint
		switch (theme) {
		case EXTENDED:
			mTextPaint.setColor(0xFF2C2CA9); //SASSA
			mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
			break;
		case LIGHT:
			mTextPaint.setColor(0xFF212121);
			mTextPaint.setTypeface(mPainter.getTypeface());
			break;
		case DARK:
			mTextPaint.setColor(0xFFFFFFC0);
			mTextPaint.setTypeface(mPainter.getTypeface());
			break;
		}

		// Set border for a cage which is not selected and which does not have
		// bad maths
		switch (theme) {
		case LIGHT:
			mBorderPaint.setColor(0xFF000000);
			mBorderPaint.setAntiAlias(false);
			break;
		case DARK:
			mBorderPaint.setColor(0xFFFFFFFF);
			mBorderPaint.setAntiAlias(true);
			break;
		}

		// Set border for a cage which is not selected but which is having bad
		// maths
		switch (theme) {
		case LIGHT:
			mBorderBadMathPaint.setColor(0xffff4444);
			mBorderBadMathPaint.setAntiAlias(true);
			break;
		case DARK:
			mBorderBadMathPaint.setColor(0xFFBB0000);
			mBorderBadMathPaint.setAntiAlias(true);
			break;
		}

		// Set border for a cage which is selected
		switch (theme) {
		case LIGHT:
			mBorderSelectedPaint.setColor(0xFF000000);
			mBorderSelectedPaint.setAntiAlias(false);
			break;
		case DARK:
			mBorderSelectedPaint.setColor(0xFFA0A030);
			mBorderSelectedPaint.setAntiAlias(true);
			break;
		}

		// Set border for a cages which is selected and is having bad maths
		switch (theme) {
		case LIGHT:
			mBorderSelectedBadMathPaint.setColor(0xFFff4444);
			mBorderSelectedBadMathPaint.setAntiAlias(true);
			break;
		case DARK:
			mBorderSelectedBadMathPaint.setColor(0xFFBB0000);
			mBorderSelectedBadMathPaint.setAntiAlias(true);
			break;
		}
	}

	@Override
	protected void setBorderSizes(boolean thin) {
		if (thin) {
			mBorderPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_NORMAL);
			mBorderSelectedPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_MEDIUM);
			mBorderBadMathPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_NORMAL);
			mBorderSelectedBadMathPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_MEDIUM);
		} else {
			mBorderPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_NORMAL);
			mBorderSelectedPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_THICK);
			mBorderBadMathPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_NORMAL);
			mBorderSelectedBadMathPaint
					.setStrokeWidth(BorderPainter.BORDER_STROKE_WIDTH_THICK);
		}
	}

	@Override
	protected void setCellSize(float size) {
		// Text size of cage text is 1/3 of cell size
		int cageTextSize = (int) (size / 3);

		mTextPaint.setTextSize(cageTextSize);
		mTextLeftOffset = 2;
		mTextBottomOffset = cageTextSize;
	}

	/**
	 * Gets the border paint for a selected cage.
	 * 
	 * @return The border paint for a selected cage.
	 */
	public Paint getBorderSelectedPaint() {
		return mBorderSelectedPaint;
	}

	/**
	 * Gets the border paint for cage which is not selected and has bas maths.
	 * 
	 * @return The border paint for cage which is not selected and has bas
	 *         maths.
	 */
	public Paint getBorderBadMathPaint() {
		return mBorderBadMathPaint;
	}

	/**
	 * Gets the border paint for the selected cage which has bas maths.
	 * 
	 * @return The border paint for the selected cage which has bas maths.
	 */
	public Paint getBorderSelectedBadMathPaint() {
		return mBorderSelectedBadMathPaint;
	}

	/**
	 * Gets the text paint for the cage result.
	 * 
	 * @return The text paint for the cage result.
	 */
	public Paint getTextPaint() {
		return mTextPaint;
	}

	/**
	 * Gets the horizontal (left) offset for the cage result inside a cell.
	 * 
	 * @return The horizontal (left) offset for the cage result inside a cell.
	 */
	public float getTextLeftOffset() {
		return mTextLeftOffset;
	}

	/**
	 * Gets the vertical (top) offset for the cage result inside a cell.
	 * 
	 * @return The vertical (top) offset for the cage result inside a cell.
	 */
	public float getTextBottomOffset() {
		return mTextBottomOffset;
	}
}