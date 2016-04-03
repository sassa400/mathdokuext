package com.klokisoft.mathdokuext.grid.ui;

import com.klokisoft.mathdokuext.Preferences;
import com.klokisoft.mathdokuext.grid.CellChange;
import com.klokisoft.mathdokuext.grid.Grid;
import com.klokisoft.mathdokuext.grid.GridCell;
import com.klokisoft.mathdokuext.statistics.GridStatistics.StatisticsCounterType;
import com.klokisoft.mathdokuext.tip.TipBadCageMath;
import com.klokisoft.mathdokuext.tip.TipDuplicateValue;
import com.klokisoft.mathdokuext.ui.PuzzleFragmentActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;

public class GridPlayerView extends GridViewerView {
	@SuppressWarnings("unused")
	private static final String TAG = "MathDokuExt.GridPlayerView";

	// Listeners
	private OnInputModeChangedListener mOnInputModeChangedListener;

	public interface OnInputModeChangedListener {
		void onInputModeChanged(GridInputMode inputMode);
	}

    GestureDetector gestureDetector;

    private GridInputMode mInputMode;

	public GridPlayerView(Context context) {
		super(context);
        if(!isInEditMode()){
		initGridView(context);
        gestureDetector = new GestureDetector(context, new GestureListener());
        }
	}

	public GridPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
            if(!isInEditMode()){
		initGridView(context);
        gestureDetector = new GestureDetector(context, new GestureListener());
            }
	}

	public GridPlayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
                if(!isInEditMode()){
		initGridView(context);
        gestureDetector = new GestureDetector(context, new GestureListener());
        }
	}

	private void initGridView(Context context) {
		// Set listeners
//		this.setOnTouchListener(this);
		mOnInputModeChangedListener = null;
	}

    // delegate the event to the gesture detector
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return gestureDetector.onTouchEvent(e);
	}

// @Override
//	public boolean onTouch(View arg0, MotionEvent event) {
//		if (mGrid == null || !mGrid.isActive())
//			return false;
//
//		// On down event select the cell but no further processing until we are
//		// sure the long press event has been caught.
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			setTouchDownEvent(event);
//
//			// Do not allow other view to respond to this action, for example by
//			// handling the long press, which would result in malfunction of the
//			// swipe motion.
//			return true;
//		case MotionEvent.ACTION_UP:
//			if (this.mTouchedListener != null) {
//				this.playSoundEffect(SoundEffectConstants.CLICK);
//
//				// Inform listener of puzzle fragment about the release action
//				mTouchedListener.gridTouched(mGrid.getSelectedCell());
//
//				// Update to remove the swipe line
//				invalidate();
//			}
//			return true;
//		default:
//			break;
//		}
//
//		return false;
//	}

	public GridCell getSelectedCell() {
		return mGrid.getSelectedCell();
	}

	public void digitSelected(int newValue, boolean isPutting) {
		GridCell selectedCell = mGrid.getSelectedCell();
		if (selectedCell == null) {
			// It should not be possible to select a digit without having
			// selected a cell first. But better safe then sorry.
			return;
		}

		// Save undo information
		CellChange orginalUserMove = selectedCell.saveUndoInformation(null);

		// Get old value of selected cell
		int oldValue = selectedCell.getUserValue();

		if (newValue == 0) { // Clear Button
			if (!selectedCell.isEmpty()) {
				selectedCell.clearPossibles();
				selectedCell.setUserValue(0);
				mGrid.getGridStatistics().increaseCounter(
						StatisticsCounterType.ACTION_CLEAR_CELL);

				// In case the last user value has been cleared in the grid, the
				// check progress should no longer be available.
				if (mGrid.isEmpty(false)) {
					((PuzzleFragmentActivity) mContext).invalidateOptionsMenu();
				}
			}
		} else {
			//SASSA on long touch behave like input mode is NORMAL
			if (mInputMode == GridInputMode.MAYBE && !isPutting) {
				if (selectedCell.isUserValueSet()) {
					selectedCell.clear();
				}
				if (selectedCell.hasPossible(newValue)) {
					selectedCell.removePossible(newValue);
				} else {
					selectedCell.addPossible(newValue);
					mGrid.increaseCounter(StatisticsCounterType.POSSIBLES);
				}
			} else {
				if (newValue != oldValue) {
					// User value of cell has actually changed.
					selectedCell.setUserValue(newValue);
					selectedCell.clearPossibles();
					if (oldValue == 0) {
						// In case a user value has been entered, the
						// check progress should be made available.
						((PuzzleFragmentActivity) mContext)
								.invalidateOptionsMenu();
					}
//SASSA--
//					if (newValue != selectedCell.getCorrectValue()
//							&& TipIncorrectValue.toBeDisplayed(mPreferences)) {
//						new TipIncorrectValue(mContext).show();
//					}
				}
				//SASSA always remove possibles even if value is the same
				if (mPreferences.isPuzzleSettingClearMaybesEnabled()) {
					// Update possible values for other cells in this row
					// and
					// column.
					mGrid.clearRedundantPossiblesInSameRowOrColumn(orginalUserMove);
				}
			}
		}

		// Each cell in the same column or row as the given cell has to be
		// checked for duplicate values.
		int targetRow = selectedCell.getRow();
		int targetColumn = selectedCell.getColumn();
		for (GridCell checkedCell : mGrid.mCells) {
			if (checkedCell.getRow() == targetRow
					|| checkedCell.getColumn() == targetColumn) {
				if (mGrid.markDuplicateValuesInRowAndColumn(checkedCell)) {
					if (checkedCell == selectedCell
							&& TipDuplicateValue.toBeDisplayed(mPreferences)) {
						new TipDuplicateValue(mContext).show();
					}
				}
			}
		}

		// Check the cage math
		if (!selectedCell.getCage().checkCageMathsCorrect(false)) {
			if (TipBadCageMath.toBeDisplayed(mPreferences)) {
				new TipBadCageMath(mContext).show();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mGrid == null) {
			// As long as no grid has been attached to the grid view, it can not
			// be drawn.
			return;
		}

		synchronized (mGrid.mLock) {
			onDrawLocked(canvas);
		}
	}

	/**
	 * Get the current grid input mode.
	 * 
	 * @return The current grid input mode.
	 */
	@Override
	public GridInputMode getGridInputMode() {
		return mInputMode;
	}

	@Override
	public void loadNewGrid(Grid grid) {
		super.loadNewGrid(grid);

		if (mPreferences.isDigitsLongTouchEnabled())
		{
			mInputMode = GridInputMode.MAYBE;
		}
		else // Set default input mode to normal
		{
			mInputMode = GridInputMode.NORMAL;
		}
			
		invalidate();
	}

	/**
	 * Highlight those cells where the user has made a mistake.
	 * 
	 * @return The number of cells which have been marked as invalid. Cells
	 *         which were already marked as invalid will not be counted again.
	 */
	public int markInvalidChoices() {
		int countNewInvalids = 0;
		for (GridCell cell : mGrid.mCells) {
			// Check all cells having a value and not (yet) marked as invalid.
			if (cell.isUserValueSet() && !cell.hasInvalidUserValueHighlight()) {
				if (cell.getUserValue() != cell.getCorrectValue()) {
					cell.setInvalidHighlight();
					mGrid.increaseCounter(StatisticsCounterType.CHECK_PROGRESS_INVALIDS_CELLS_FOUND);
					countNewInvalids++;
				}
			}
		}

		if (countNewInvalids > 0) {
			invalidate();
		}

		return countNewInvalids;
	}

	/**
	 * Toggle the input mode to the other mode
	 */
	public void toggleInputMode() {

		if (!mPreferences.isDigitsLongTouchEnabled())
		{
			mInputMode = (mInputMode == GridInputMode.NORMAL ? GridInputMode.MAYBE
					: GridInputMode.NORMAL);
			invalidate();

			// Inform listeners about change in input mode
			if (mOnInputModeChangedListener != null) {
				mOnInputModeChangedListener.onInputModeChanged(mInputMode);
			}
		}
	}

	/**
	 * Set input mode to the other mode
	 */
	public void setInputMode(GridInputMode newmode)
	{
		if (newmode != mInputMode)
		{
			mInputMode = newmode;
			invalidate();

			// Inform listeners about change in input mode
			if (mOnInputModeChangedListener != null) {
				mOnInputModeChangedListener.onInputModeChanged(mInputMode);
			}
		}
	}

	/**
	 * Register the listener to call in case the ticker tape for the hints has
	 * to be set.
	 * 
	 * @param onInputChangedModeListener
	 *            The listener to call in case a hint has to be set.
	 */
	public void setOnInputModeChangedListener(
			OnInputModeChangedListener onInputChangedModeListener) {
		mOnInputModeChangedListener = onInputChangedModeListener;
	}
	
	/**
	 * Register the touch down event.
	 * 
	 * @param event
	 *            The event which is registered as the touch down event of the
	 *            swipe motion.
	 */
	protected void setTouchDownEvent(MotionEvent event) 
	{
		Grid grid = getGrid();
		int gridSize = (grid == null ? 1 : grid.getGridSize());

		// Convert x-position to a column number. -1 means left of grid,
		// mGridSize means right of grid.
		final float xPos = (event.getX() - mBorderWidth) / mGridCellSize;
		final float yPos = (event.getY() - mBorderWidth) / mGridCellSize;
		if (xPos >= 0 && xPos <= gridSize && yPos >=0 && yPos <= gridSize)
		{
			// Select the touch down cell.
			mGrid.setSelectedCell((int) yPos, (int) xPos);
		}
	}

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    	 @Override
    	 public void onLongPress(MotionEvent e) {
            if (mGrid.getSelectedCell().countPossibles() == 1)
            {
            	performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            	final Preferences mMathDokuPreferences = Preferences.getInstance();
            	digitSelected(mGrid.getSelectedCell().getFirstPossible(), 
            			     mMathDokuPreferences.isDigitsLongTouchEnabled());
            	invalidate();
            }
        }

        @Override
		public boolean onSingleTapConfirmed(MotionEvent event)
		{
				playSoundEffect(SoundEffectConstants.CLICK);

				// Update to remove the swipe line
				invalidate();
			return super.onSingleTapConfirmed(event);
		}
		@Override
        public boolean onDown(MotionEvent event) {
			setTouchDownEvent(event);
			invalidate();
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
			toggleInputMode();
            return true;
        }
    }

}