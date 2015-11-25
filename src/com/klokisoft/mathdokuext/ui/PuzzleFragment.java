package com.klokisoft.mathdokuext.ui;

import java.util.ArrayList;

import com.klokisoft.mathdokuext.Cheat;
import com.klokisoft.mathdokuext.GameTimer;
import com.klokisoft.mathdokuext.Preferences;
import com.klokisoft.mathdokuext.Cheat.CheatType;
import com.klokisoft.mathdokuext.grid.CellChange;
import com.klokisoft.mathdokuext.grid.Grid;
import com.klokisoft.mathdokuext.grid.GridCage;
import com.klokisoft.mathdokuext.grid.GridCell;
import com.klokisoft.mathdokuext.grid.ui.GridInputMode;
import com.klokisoft.mathdokuext.grid.ui.GridPlayerView;
import com.klokisoft.mathdokuext.painter.Painter;
import com.klokisoft.mathdokuext.statistics.GridStatistics.StatisticsCounterType;
import com.klokisoft.mathdokuext.tip.TipCheat;
import com.klokisoft.mathdokuext.tip.TipDialog;
import com.klokisoft.mathdokuext.util.Util;
import com.klokisoft.mathdokuext.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Paul
 * 
 */
public class PuzzleFragment extends android.support.v4.app.Fragment implements
		OnSharedPreferenceChangeListener, OnCreateContextMenuListener,
		GridPlayerView.OnInputModeChangedListener {
	public final static String TAG = "MathDokuExt.PuzzleFragment";

	public static final String BUNDLE_KEY_SOLVING_ATTEMPT_ID = "PuzzleFragment.solvingAttemptId";

	// Call type of KKC
	private enum KKCCallType {
		SINGLE_CALL_NO_IMMED, SINGLE_CALL_IMMED, MULTI_CALL_FIRST, MULTI_CALL_REST
	}

	// The grid and the view which will display the grid.
	public Grid mGrid;
	public GridPlayerView mGridPlayerView;

	// A global painter object to paint the grid in different themes.
	public Painter mPainter;

	private GameTimer mTimerTask;

	private RelativeLayout mPuzzleGridLayout;
	private TextView mTimerText;
	private ImageView mInputModeButtonView;

	private TableLayout mButtonsTableLayout;
	private Button mDigit[] = new Button[9];

    private View[] mSoundEffectViews;

	public Preferences mMathDokuPreferences;

	private Context mContext;

	private View mRootView;

	private OnGridFinishedListener mOnGridFinishedListener;

	private BroadcastReceiver mDreamingBroadcastReceiver;

	private boolean kkcExists;
	// Container Activity must implement these interfaces
	public interface OnGridFinishedListener {
		public void onGridFinishedListener(int solvingAttemptId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mOnGridFinishedListener = (OnGridFinishedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGridFinishedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater
				.inflate(R.layout.puzzle_fragment, container, false);

		mContext = getActivity();

		mPainter = Painter.getInstance();
		mMathDokuPreferences = Preferences.getInstance();
		mMathDokuPreferences.mSharedPreferences
				.registerOnSharedPreferenceChangeListener(this);

		mPuzzleGridLayout = (RelativeLayout) mRootView
				.findViewById(R.id.puzzleGrid);
		mGridPlayerView = (GridPlayerView) mRootView
				.findViewById(R.id.grid_player_view);
		mTimerText = (TextView) mRootView.findViewById(R.id.timerText);

		mButtonsTableLayout = (TableLayout)mRootView.findViewById(R.id.digitButtons);
		mDigit[0] = (Button)mRootView.findViewById(R.id.digit1);
		mDigit[1] = (Button)mRootView.findViewById(R.id.digit2);
		mDigit[2] = (Button)mRootView.findViewById(R.id.digit3);
		mDigit[3] = (Button)mRootView.findViewById(R.id.digit4);
		mDigit[4] = (Button)mRootView.findViewById(R.id.digit5);
		mDigit[5] = (Button)mRootView.findViewById(R.id.digit6);
		mDigit[6] = (Button)mRootView.findViewById(R.id.digit7);
		mDigit[7] = (Button)mRootView.findViewById(R.id.digit8);
		mDigit[8] = (Button)mRootView.findViewById(R.id.digit9);
//		for (int i = 0; i < mDigit.length; i++)
//		{
//			mDigit[i].setBackgroundColor(mPainter.getButtonBackgroundColor());			
//		}
//SASSA
//		mDigitC = (Button)mRootView.findViewById(R.id.digitC);
//		mDigitC.setBackgroundColor(mPainter.getButtonBackgroundColor());

        ImageButton mClearButton = (ImageButton) mRootView.findViewById(R.id.clearButton);
//		mClearButton.setBackgroundColor(mPainter.getButtonBackgroundColor());

        ImageButton mUndoButton = (ImageButton) mRootView.findViewById(R.id.undoButton);
//		mUndoButton.setBackgroundColor(mPainter.getButtonBackgroundColor());

        Button mKKC = (Button) mRootView.findViewById(R.id.kkcButton);
//		mKKC.setBackgroundColor(mPainter.getButtonBackgroundColor());

        ImageButton mCheckButton = (ImageButton) mRootView.findViewById(R.id.checkButton);

        mSoundEffectViews = new View[] { mGridPlayerView, mClearButton,
                mUndoButton, mKKC, mCheckButton};

		// Hide all controls until sure a grid view can be displayed.
		setNoGridLoaded();

		mClearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGridPlayerView != null) {
                    mGridPlayerView.digitSelected(0, false); //second parameter doesn't matter

//					setClearAndUndoButtonVisibility(mGrid.getSelectedCell());
                    mGridPlayerView.invalidate();
                }
            }
        });
		mUndoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGrid.undoLastMove()) {
                    // Successful undo
//					setClearAndUndoButtonVisibility(mGrid.getSelectedCell());
                    mGridPlayerView.invalidate();

                    // Undo can toggle the visibility of the check progress
                    // button in the action bar
                    ((FragmentActivity) mContext).invalidateOptionsMenu();
                }
            }
        });
		mKKC.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                runKenKenCalc(KKCCallType.SINGLE_CALL_NO_IMMED);
            }
        });
		mKKC.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				runKenKenCalc(KKCCallType.SINGLE_CALL_IMMED);
				return true;
			}
		});
		mCheckButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	checkProgress();
            }
        });
		
		for (int i = 0; i < mDigit.length; i++)
		{
			mDigit[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Convert text of button (number) to Integer
					int d = Integer.parseInt(((Button) v).getText().toString());
					setDigitSelected(d, false);
				}
			});
			mDigit[i].setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v)
				{
					// if long touch is not enabled, treat as short click
					int d = Integer.parseInt(((Button) v).getText().toString());
					setDigitSelected(d, mMathDokuPreferences.isDigitsLongTouchEnabled());
					return true;
				}
			});
		}

//SASSA
//		mDigitC.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (mGridPlayerView != null) {
//					mGridPlayerView.digitSelected(0, false); //second parameter doesn't matter
//					mGridPlayerView.invalidate();
//				}
//			}
//		});
		mGridPlayerView.setFocusable(true);
		mGridPlayerView.setFocusableInTouchMode(true);

		// Initialize the input mode
		mInputModeButtonView = (ImageView) mRootView
				.findViewById(R.id.input_mode_image);
		mInputModeButtonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mGridPlayerView != null && mGrid != null) {
					// Toggle input mode
					mGridPlayerView.toggleInputMode();
				}
			}
		});
		mGridPlayerView.setOnInputModeChangedListener(this);

		// Force display of normal mode text.
		this.onInputModeChanged(GridInputMode.NORMAL);
		setInputModeButtonVisibility();
		
		registerForContextMenu(mGridPlayerView);

		// In case a solving attempt id has been passed, this attempt has to be
		// loaded.
		Bundle args = getArguments();
		if (args != null) {
			int solvingAttemptId = args.getInt(BUNDLE_KEY_SOLVING_ATTEMPT_ID);

			mGrid = new Grid();
			if (mGrid.load(solvingAttemptId)) {
				setNewGrid(mGrid);
			}
		}

		return mRootView;
	}

	@Override
	public void onPause() {
		// Store preference counters as they are kept in internal memory for
		// performance reasons.
		if (mMathDokuPreferences != null) {
			mMathDokuPreferences.commitCounters();
		}

		// Unregister the receiver of the day dreaming intent.
		if (mContext != null && mDreamingBroadcastReceiver != null) {
			mContext.unregisterReceiver(mDreamingBroadcastReceiver);
		}

		pause();

		super.onPause();
	}

	public void setTheme() {
		mPainter.setTheme(mMathDokuPreferences.getTheme());

		// Invalidate the grid view and the input mode button in order to used
		// the new theme setting
		if (mGridPlayerView != null) {
			mGridPlayerView.invalidate();
			if (mInputModeButtonView != null) {
				setInputModeButtonVisibility();
				setInputModeImage(mGridPlayerView.getGridInputMode());
				mInputModeButtonView.invalidate();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mMathDokuPreferences != null
				&& mMathDokuPreferences.mSharedPreferences != null) {
			mMathDokuPreferences.mSharedPreferences
					.unregisterOnSharedPreferenceChangeListener(this);
		}
		super.onDestroy();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onResume() {
		// Register a broadcast receiver on the intents related to day dreaming.
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// Create broad cast receiver
			if (mDreamingBroadcastReceiver == null) {
				mDreamingBroadcastReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						if (intent.getAction().equals(
								Intent.ACTION_DREAMING_STARTED)) {
							// Pause the fragment on start of dreaming
							pause();
						} else if (intent.getAction().equals(
								Intent.ACTION_DREAMING_STOPPED)) {
							// Resume the fragment as soon as the dreaming has
							// stopped
							resume();
						}
					}
				};
			}

			// Create intent filter for day dreaming
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_DREAMING_STARTED);
			intentFilter.addAction(Intent.ACTION_DREAMING_STOPPED);

			// Register receiver
			mContext.registerReceiver(mDreamingBroadcastReceiver, intentFilter,
					null, null);
		}

		// Resume the fragment
		resume();

		super.onResume();
	}

	public void setSoundEffectsEnabled(boolean enabled) {
		for (View v : this.mSoundEffectViews)
			v.setSoundEffectsEnabled(enabled);
	}

	/**
	 * Checks whether the clear grid menu item is available.
	 * 
	 * @return True in case the clear grid menu item is available. False
	 *         otherwise.
	 */
	protected boolean showClearGrid() {
		return (mGrid != null && mGrid.isActive() && mGrid.isEmpty(true) == false);
	}

	/**
	 * Handles clearing of the entire grid. The grid will only be cleared after
	 * the user has confirmed clearing.
	 */
	protected void clearGrid() {
		this.mGrid.clearCells(false);
		this.mGridPlayerView.invalidate();
	}

	/**
	 * Sets the handler on the grid which will be called as soon as the grid has
	 * been solved.
	 */
	public void setOnSolvedHandler() {
		if (mGrid == null) {
			return;
		}

		mGrid.setSolvedHandler(mGrid.new OnSolvedListener() {
			@Override
			public void puzzleSolved() {
				// Stop the time and unselect the current cell and cage. Finally
				// save the grid.
				stopTimer();
				mGrid.deselectSelectedCell();
				mGrid.save();

				// Notify the containing fragment activity about the finishing
				// of the grid. In case the puzzle has been solved manually, a
				// animation is played first.
				if (!mGrid.isActive() || mGrid.isSolutionRevealed()
						|| mGrid.countMoves() == 0) {
					mOnGridFinishedListener.onGridFinishedListener(mGrid
							.getSolvingAttemptId());
				} else {
					// Hide controls while showing the animation.
					setInactiveGridLoaded();

					// Set the text view which will be animated
					final TextView textView = (TextView) mRootView
							.findViewById(R.id.solvedText);
					textView.setText(R.string.main_ui_solved_messsage);
					textView.setTextColor(Painter.getInstance()
							.getSolvedTextPainter().getTextColor());
					textView.setBackgroundColor(Painter.getInstance()
							.getSolvedTextPainter().getBackgroundColor());
					textView.setTypeface(Painter.getInstance().getTypeface());
					textView.setVisibility(View.VISIBLE);

					// Build the animation
					Animation animation = AnimationUtils.loadAnimation(
							mContext, R.anim.solved);
					animation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation animation) {
							textView.setVisibility(View.GONE);
							mOnGridFinishedListener
									.onGridFinishedListener(mGrid
											.getSolvingAttemptId());
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
					});

					// Start animation of the text view.
					textView.startAnimation(animation);
				}

			}
		});
	}

	/**
	 * Load the new grid and set control visibility.
	 * 
	 * @param grid
	 *            The grid to display.
	 */
	public void setNewGrid(Grid grid) {
		if (grid != null) {
			mGrid = grid;
			mGridPlayerView.loadNewGrid(grid);

			// Show the grid of the loaded puzzle.
			if (mGrid.isActive()) {
				// Set visibility of grid layout
				if (mPuzzleGridLayout != null) {
					mPuzzleGridLayout.setVisibility(View.VISIBLE);
					mPuzzleGridLayout.invalidate();
				}

				// Set timer
				if (mMathDokuPreferences.isTimerVisible() && mTimerText != null) {
					mTimerText.setVisibility(View.VISIBLE);
					mTimerText.invalidate();
				}
				startTimer();

//				setClearAndUndoButtonVisibility((mGrid == null ? null : mGrid
//						.getSelectedCell()));
				
				setDigitButtons();

				// Handler for solved game
				setOnSolvedHandler();
			} else {
				setInactiveGridLoaded();
				stopTimer();
			}

			// Check actionbar and menu options
			((Activity) mContext).invalidateOptionsMenu();

			mRootView.invalidate();
		} else {
			// No grid available.
			setNoGridLoaded();
		}
	}
	
	/**
	 * Set the correct visibility of the digit select buttons.
	 */
	private void setDigitButtons() {
		for (int i = 4; i < mDigit.length; i++)
		{
			mDigit[i].setVisibility(View.INVISIBLE);				
		}

		switch (mGrid.getGridSize()) {
		case 9:
			mDigit[8].setVisibility(View.VISIBLE);
		case 8:
			mDigit[7].setVisibility(View.VISIBLE);
		case 7:
			mDigit[6].setVisibility(View.VISIBLE);
		case 6:
			mDigit[5].setVisibility(View.VISIBLE);
		case 5:
			mDigit[4].setVisibility(View.VISIBLE);				
		}
		setDigitButtonsMode();
		mButtonsTableLayout.setVisibility(View.VISIBLE);
		for (int i = 0; i < mDigit.length; i++)
		{
			mDigit[i].invalidate();				
		}
//SASSA
//			mDigitC.invalidate();
		mButtonsTableLayout.invalidate();
	}
	
	/**
	 * Set the digit buttons colours base on input mode.
	 */
	private void setDigitButtonsMode() {
		for (int i = 0; i < mDigit.length; i++)
		{
			if (mGridPlayerView.getGridInputMode() == GridInputMode.NORMAL) {
				mDigit[i].setTextColor(mPainter.getDigitFgColor());
			} else {
				mDigit[i].setTextColor(mPainter.getDigitFgMaybeColor());
			}
		}
	}

	/**
	 * Start a new timer (only in case the grid is active).
	 */
	protected void startTimer() {
		// Stop old timer
		stopTimer();

		if (mGrid != null && mGrid.isActive()) {
			mTimerTask = new GameTimer(this);
			mTimerTask.mElapsedTime = mGrid.getElapsedTime();
			mTimerTask.mCheatPenaltyTime = mGrid.getCheatPenaltyTime();
			if (mMathDokuPreferences.isTimerVisible()) {
				mTimerText.setVisibility(View.VISIBLE);
			} else {
				mTimerText.setVisibility(View.INVISIBLE);
			}
			mTimerTask.execute();
		}
	}

	/**
	 * Stop the current timer.
	 */
	public void stopTimer() {
		// Stop timer if running
		if (mTimerTask != null && !mTimerTask.isCancelled()) {
			if (mGrid != null) {
				this.mGrid.setElapsedTime(mTimerTask.mElapsedTime,
						mTimerTask.mCheatPenaltyTime);
			}
			mTimerTask.cancel(true);
		}
	}

	/**
	 * Sets the timer text with the actual elapsed time.
	 * 
	 * @param elapsedTime
	 *            The elapsed time (in mili seconds) while playing the game.
	 */
	@SuppressLint("DefaultLocale")
	public void setElapsedTime(long elapsedTime) {
		if (mTimerText != null) {
			mTimerText.setText(Util.durationTimeToString(elapsedTime));
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Preferences.PUZZLE_SETTING_THEME)) {
			setTheme();
		}
		if (key.equals(Preferences.PUZZLE_SETTING_DIGITS_LONG_TOUCH)) {
			setInputModeButtonVisibility(); 
		}
	}

	/**
	 * Checks whether the reveal cell menu item is available.
	 * 
	 * @return
	 */
	protected boolean showRevealCell() {
		return (mGrid != null && mGrid.isActive() && mGrid.getSelectedCell() != null);
	}

	/**
	 * Handles revealing of user value in the given cell.
	 * 
	 * @param selectedCell
	 *            The cell for which the user value has to be revealed.
	 */
	protected void revealCell() {
		if (mGrid == null) {
			return;
		}

		GridCell selectedCell = mGrid.getSelectedCell();
		if (selectedCell == null) {
			return;
		}

		// Save old cell info
		CellChange orginalUserMove = selectedCell.saveUndoInformation(null);

		// Reveal the user value
		selectedCell.setRevealed();
		selectedCell.setUserValue(selectedCell.getCorrectValue());
		if (mMathDokuPreferences.isPuzzleSettingClearMaybesEnabled()) {
			// Update possible values for other cells in this row and
			// column.
			mGrid.clearRedundantPossiblesInSameRowOrColumn(orginalUserMove);
		}
//		setClearAndUndoButtonVisibility(selectedCell);

		mGrid.increaseCounter(StatisticsCounterType.ACTION_REVEAL_CELL);
		Cheat cheat = getNewCheat(CheatType.CELL_REVEALED);

		// Display tip
		if (TipCheat.toBeDisplayed(mMathDokuPreferences, cheat)) {
			new TipCheat(mContext, cheat).show();
		}

		this.mGridPlayerView.invalidate();
	}

	/**
	 * Checks whether the reveal operator menu item is available.
	 * 
	 * @return True in case the reveal operator menu item is available. False
	 *         otherwise.
	 */
	protected boolean showRevealOperator() {
		if (mGrid == null || mGrid.isActive() == false) {
			return false;
		}

		// Determine current selected cage.
		GridCage selectedGridCage = mGrid.getCageForSelectedCell();
		return (selectedGridCage != null && selectedGridCage.isOperatorHidden());
	}

	/**
	 * Handles revealing of the operator of the given cage.
	 */
	protected void revealOperator() {
		if (mGrid == null || mGrid.isActive() == false) {
			return;
		}

		// Determine current selected cage.
		GridCage selectedGridCage = mGrid.getCageForSelectedCell();

		if (selectedGridCage == null) {
			return;
		}

		selectedGridCage.revealOperator();

		mGrid.increaseCounter(StatisticsCounterType.ACTION_REVEAL_OPERATOR);
		Cheat cheat = getNewCheat(CheatType.OPERATOR_REVEALED);

		// Display tip
		if (TipCheat.toBeDisplayed(mMathDokuPreferences, cheat)) {
			new TipCheat(mContext, cheat).show();
		}

		mGridPlayerView.invalidate();
	}

	/**
	 * Handles fill all possibles in the grid.
	 * 
	 * @param none
	 */
	protected void fillPossibles() {
		if (mGrid == null) {
			return;
		}

		for (GridCell cell : mGrid.mCells)
		{
			if (cell != null && cell.isEmpty())
			{
				for (int i = 1; i <= mGrid.getGridSize(); i++)
				{
					cell.addPossible(i);
					mGrid.increaseCounter(StatisticsCounterType.POSSIBLES);										
				}
			}
		}

		this.mGridPlayerView.invalidate();
	}

	/**
	 * Handles fill all possibles in the grid.
	 * 
	 * @param none
	 */
	protected void fillDigitcalc() {
		if (mGrid == null) {
			return;
		}

		Toast.makeText(mContext, R.string.pleasewait, Toast.LENGTH_SHORT).show();

		KKCCallType callMode = KKCCallType.MULTI_CALL_FIRST; 
		for (GridCage cage : mGrid.mCages)
		{
			for (GridCell cell : cage.mCells)
			{
				//for first non-empty cell call KKC
				if (cell != null && cell.isEmpty() && cell.getCage().mCells.size() > 1)
				{
					cell.select();
					runKenKenCalc(callMode);
					callMode = KKCCallType.MULTI_CALL_REST;
					break;
				}
			}
		}

		this.mGridPlayerView.invalidate();
	}

	/**
	 * Create a cheat of the given type.
	 * 
	 * @param cheatType
	 *            The type of cheat to be processed.
	 */
	private Cheat getNewCheat(CheatType cheatType) {
		// Create new cheat
		Cheat cheat = new Cheat(mContext, cheatType);

		// Add penalty time
		if (mTimerTask != null) {
			mTimerTask.addCheatPenaltyTime(cheat);
		}

		return cheat;
	}

	/**
	 * Checks whether the check progress menu item is available.
	 * 
	 * @return
	 */
//	protected boolean showCheckProgress() {
//		return (mGrid != null && mGrid.isActive() && !mGrid.isEmpty(false));
//	}

	/**
	 * Checks the progress of solving the current grid
	 */
	protected void checkProgress() {
		if (mGrid == null || mGridPlayerView == null) {
			return;
		}

		boolean allUserValuesValid = mGrid.isSolutionValidSoFar();
		int countNewInvalidChoices = (allUserValuesValid ? 0 : mGridPlayerView
				.markInvalidChoices());

		// Create new cheat
//SASSA--
//		Cheat cheat = new Cheat(this.getActivity(),
//				CheatType.CHECK_PROGRESS_USED, countNewInvalidChoices);

		// Register cheat in statistics
		mGrid.getGridStatistics().increaseCounter(
				StatisticsCounterType.ACTION_CHECK_PROGRESS);
		mGrid.getGridStatistics().increaseCounter(
				StatisticsCounterType.CHECK_PROGRESS_INVALIDS_CELLS_FOUND,
				countNewInvalidChoices);

		// Add penalty time
		//SASSA--
//		if (mTimerTask != null) {
//			mTimerTask.addCheatPenaltyTime(cheat);
//		}

		// Display tip or toast
//SASSA--
//		if (TipCheat.toBeDisplayed(mMathDokuPreferences, cheat)) {
//			new TipCheat(mContext, cheat).show();
//		} 
		if (allUserValuesValid) {
			Toast.makeText(mContext, R.string.ProgressOK, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(mContext, R.string.ProgressBad, Toast.LENGTH_SHORT)
					.show();
		}

		// Never show the tip about incorrect values and reference to function
		// Check Progress again.
//SASSA--
//		TipIncorrectValue.doNotDisplayAgain(mMathDokuPreferences);
	}

	/**
	 * Checks whether the clear and undo buttons should be visible in case the
	 * given cell is selected.
	 * 
	 * @param cell
	 *            The cell to be used to check whether the clear and undo button
	 *            should be visible. Use null in case no cell is selected.
	 */
//	private void setClearAndUndoButtonVisibility(GridCell cell) {
//		if (mClearButton != null) {
//			mClearButton
//					.setVisibility((cell == null || cell.isEmpty()) ? View.INVISIBLE
//							: View.VISIBLE);
//			mClearButton.invalidate();
//		}
//		if (mUndoButton != null) {
//			mUndoButton
//					.setVisibility((mGrid == null || mGrid.countMoves() == 0 || mGrid
//							.isActive() == false) ? View.INVISIBLE
//							: View.VISIBLE);
//			mUndoButton.invalidate();
//		}
//	}

	public boolean isActive() {
		return (mGrid != null && mGrid.isActive());
	}

	public void prepareLoadNewGame() {
		// Cancel old timer if running.
		stopTimer();

		// Save the game.
		if (mGrid != null) {
			mGrid.save();
		}
	}

	/**
	 * Checks whether the reveal solution menu item is available.
	 * 
	 * @return True in case the reveal solution menu item is available. False
	 *         otherwise.
	 */
	protected boolean showRevealSolution() {
		return (mGrid != null && mGrid.isActive());
	}

	/**
	 * Handles revealing of the solution of the grid.
	 */
	protected void revealSolution() {
		// Disable the solved listener before revealing
		// the solution.
		mGrid.setSolvedHandler(null);

		// Reveal the solution
		mGrid.revealSolution();

		// Create the cheat. This also updates the cheat
		// penalty in the timer.
		Cheat cheat = getNewCheat(CheatType.SOLUTION_REVEALED);

		// Stop the timer and unselect the current cell
		// and cage. Finally save the grid.
		stopTimer();
		mGrid.deselectSelectedCell();
		mGrid.save();

		// Check if tip has to be displayed before
		// informing the listener about finishing the
		// grid.
		if (cheat != null
				&& TipCheat.toBeDisplayed(mMathDokuPreferences, cheat)) {
			new TipCheat(mContext, cheat).setOnClickCloseListener(
					new TipDialog.OnClickCloseListener() {
						@Override
						public void onTipDialogClose() {
							// Notify the containing fragment activity about the
							// finishing of the grid. In case the puzzle has
							// been solved manually, an animation is played first.
							if (mGrid != null
									&& mOnGridFinishedListener != null) {
								mOnGridFinishedListener
								.onGridFinishedListener(mGrid.getSolvingAttemptId());
							}
						};
					}).show();
		} else {
			if (mGrid != null
					&& mOnGridFinishedListener != null) {
				mOnGridFinishedListener.onGridFinishedListener(mGrid.getSolvingAttemptId());
			}
		}
	}

	/**
	 * Set visibility of controls for the case no grid could be loaded.
	 */
	private void setNoGridLoaded() {
		if (mPuzzleGridLayout != null) {
			mPuzzleGridLayout.setVisibility(View.GONE);
		}
		if (mTimerText != null) {
			mTimerText.setVisibility(View.GONE);
		}
//		if (mClearButton != null) {
//			mClearButton.setVisibility(View.GONE);
//		}
//		if (mUndoButton != null) {
//			mUndoButton.setVisibility(View.GONE);
//		}
		if (mButtonsTableLayout != null) {
			mButtonsTableLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * Set visibility of controls for an inactive grid.
	 */
	private void setInactiveGridLoaded() {
		if (mPuzzleGridLayout != null) {
			mPuzzleGridLayout.setVisibility(View.VISIBLE);
		}
		if (mGrid == null || (mGrid != null && mGrid.isSolutionRevealed())) {
			// Hide time in case the puzzle was solved by
			// requesting to show the solution.
			if (mTimerText != null) {
				mTimerText.setVisibility(View.INVISIBLE);
				mTimerText.invalidate();
			}
		} else {
			// Show time
			if (mTimerText != null) {
				mTimerText.setVisibility(View.VISIBLE);
				mTimerText.invalidate();
				setElapsedTime(mGrid.getElapsedTime());
			}
		}
		if (mInputModeButtonView != null) {
			mInputModeButtonView.setVisibility(View.GONE);
			setInputModeButtonVisibility();
			mInputModeButtonView.invalidate();
		}
//		if (mClearButton != null) {
//			mClearButton.setVisibility(View.GONE);
//			mClearButton.invalidate();
//		}
//		if (mUndoButton != null) {
//			mUndoButton.setVisibility(View.GONE);
//			mUndoButton.invalidate();
//		}
		if (mButtonsTableLayout != null) {
			mButtonsTableLayout.setVisibility(View.GONE);
			mButtonsTableLayout.invalidate();
		}
	}

	/**
	 * Get the solving attempt id which is being showed in this archive
	 * fragment.
	 * 
	 * @return The solving attempt id which is being showed in this archive
	 *         fragment.
	 */
	public int getSolvingAttemptId() {
		return mGrid.getSolvingAttemptId();
	}

	/**
	 * Checks whether the normal input mode menu item is visible.
	 * 
	 * @return
	 */
	protected boolean showInputModeNormal() {
		return (mGrid != null && mGrid.isActive() && mGridPlayerView != null && mGridPlayerView
				.getGridInputMode() == GridInputMode.MAYBE);
	}

	/**
	 * Checks whether the maybe input mode menu item is visible.
	 * 
	 * @return
	 */
	protected boolean showInputModeMaybe() {
		return (mGrid != null && mGrid.isActive() && mGridPlayerView != null && mGridPlayerView
				.getGridInputMode() == GridInputMode.NORMAL);
	}

	@Override
	public void onInputModeChanged(GridInputMode inputMode) {
		setInputModeImage(inputMode);
		setDigitButtonsMode();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  GridCage currCage = null;
	  
	  if (resultCode == Activity.RESULT_OK) 
      {
    	  for (GridCage cage : mGrid.mCages)
    	  {
    		  if (cage.mId == requestCode)
    		  {
    			  currCage=cage;
    		  }
    	  }
		      if (resultCode == Activity.RESULT_OK) 
		      {
		    	  int version = data.getIntExtra("version", -1);
		    	  if (version < 110)
		    	  {
				        Toast.makeText(mContext, getString(R.string.newer_version_kkc_exists), Toast.LENGTH_SHORT).show();
		    	  }
		    	  if (mGrid != null && mGrid.isActive() && mMathDokuPreferences.isFillKenkencalcEnabled()) 
		    	  {
		    		  // Determine current selected cage.
		    		  if (currCage != null) 
		    		  {
		    			  int[] maybes = new int[10];
		    			  ArrayList<String> listResults = data.getStringArrayListExtra("possibleValues");
		    			  for (String string : listResults)
		    			  {
		    				  for (int i = 0; i < string.length(); i++)
							{
								Character j = string.charAt(i);
								int b = Character.digit(j, 10);
								maybes[b] = 1;
							}
		    			  }
		    			  for (GridCell cell : currCage.mCells)
		    			  {
		    				  if (cell.isEmpty())
		    				  {
		    					  for (int i = 1; i < maybes.length; i++)
								{
									if (maybes[i] == 1)
									{
				    					  cell.addPossible(i);
				    					  mGrid.increaseCounter(StatisticsCounterType.POSSIBLES);										
									}
								}
		    				  }
		    			  }		    			   
		    		  }
		    	  }
		      } 
      }
      else if (kkcExists) 
      {
			kkcDialog(R.string.dialog_kkc_upgrade_confirmation_title,
					  R.string.dialog_kkc_upgrade_confirmation_message);
      }
      else
      {
			kkcDialog(R.string.dialog_kkc_download_confirmation_title,
					  R.string.dialog_kkc_download_confirmation_message);
      }
	}
	/**
	 * Set the visibility of the input mode text to invisible or gone.
	 */
	public void setInputModeButtonVisibility() {
		if (mMathDokuPreferences.isDigitsLongTouchEnabled())
		{
			mGridPlayerView.setInputMode(GridInputMode.MAYBE);
			mInputModeButtonView.setVisibility(View.GONE);
		}

		else
		{
			mInputModeButtonView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Set the input mode image for the given input mode.
	 * 
	 * @param inputMode
	 *            The input mode for which the input mode button has to be set.
	 */
	private void setInputModeImage(GridInputMode inputMode) {
		// Set the input mode image to the new value of the input mode
		if (mInputModeButtonView != null && mPainter != null) {
			mInputModeButtonView
					.setImageResource((inputMode == GridInputMode.NORMAL ? mPainter
							.getNormalInputModeButton() : mPainter
							.getMaybeInputModeButton()));
		}
	}
	
	/**
	 * Called when the selected digit button has been pressed.
	 * @param isLong 
	 */
	private void setDigitSelected(int digit, boolean isLong) {
		mGridPlayerView.digitSelected(digit, isLong);
		mGridPlayerView.invalidate();
	}

	/**
	 * Resumes the fragment.
	 */
	private void resume() {
		setTheme();

		// Propagate current preferences to the grid.
		if (mGrid != null) {
			mGrid.setPreferences();
		}

		this.setSoundEffectsEnabled(mMathDokuPreferences
				.isPlaySoundEffectEnabled());

		if (mTimerTask == null
				|| (mTimerTask != null && mTimerTask.isCancelled())) {
			startTimer();
		}
	}

	/**
	 * Paused the fragment.
	 */
	private void pause() {
		stopTimer();
		if (mGrid != null) {
			mGrid.save();
		}
	}
	
	/**
	 * Start intent KenKenCalc.
	 */
	private void runKenKenCalc(KKCCallType callMode)
	{
		GridCage selectedGridCage = mGrid.getCageForSelectedCell();
		if (selectedGridCage == null)
		{
			Toast.makeText(mContext, R.string.no_cell_selected , Toast.LENGTH_SHORT).show();
			return;
		}
		int factors = selectedGridCage.mCells.size();
		
		if (factors > 1)
		{
			final Intent intent = new Intent();
			intent.setClassName("com.klokisoft.kenkencalc", "com.klokisoft.kenkencalc.MainActivity");
			String operation;
			switch (selectedGridCage.mAction) {
			case GridCage.ACTION_ADD:
				operation="+";
				break;
			case GridCage.ACTION_MULTIPLY:
				operation="*";
				break;
			case GridCage.ACTION_DIVIDE:
				operation="/";
				break;
			case GridCage.ACTION_SUBTRACT:
				operation="-";
				break;
			default:
				operation="";
				break;
			}
			
			final ArrayList<Integer> filled = new ArrayList<Integer>();	
		    for (int i = 0; i < 9; i++)
			{
		    	filled.add(mGrid.getGridSize() < i + 1 ? -1 : 0);
			}
			for (GridCell cell : selectedGridCage.mCells) {
				int value = cell.getUserValue(); 
				if ( value > 0)
				{
					filled.set(value-1, 1);
				}
			}

			intent.putExtra("number", selectedGridCage.mResult);
			intent.putExtra("factors", factors);
			intent.putExtra("operation", operation);
			boolean inLine = selectedGridCage.isInLine();
			intent.putExtra("duplicates", !inLine);
			intent.putIntegerArrayListExtra("filled", filled);
			intent.putExtra("returnImmed", (callMode != KKCCallType.SINGLE_CALL_NO_IMMED)); //just for compatibility
			intent.putExtra("callMode", callMode.ordinal());
			
			try
			{
				startActivityForResult(intent, selectedGridCage.mId);
				kkcExists = true;
			}
			catch (ActivityNotFoundException e)
			{
				kkcExists = false;
			}

		}

	}

	/**
	 * Dialog to confirm download KKC
	 */
	protected void kkcDialog(int idTitle, int idMessage) 
	{
		new AlertDialog.Builder(this.getActivity())
				.setTitle(idTitle)
				.setMessage(idMessage)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setNegativeButton(
						R.string.dialog_kkc_download_confirmation_negative_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								//
							}
						})
				.setPositiveButton(
						R.string.dialog_kkc_download_confirmation_positive_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							    Uri uri = Uri.parse("market://details?id=com.klokisoft.kenkencalc");
							    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
							    try 
							    {
							        mContext.startActivity(myAppLinkToMarket);
							    } 
							    catch (ActivityNotFoundException e) 
							    {
							        Toast.makeText(mContext, getString(R.string.not_able_to_open), Toast.LENGTH_SHORT).show();
							    }
							}
						}).show();
	}
}