package com.giggs.heroquest.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.giggs.heroquest.MyActivity;
import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.fragments.LoadGameFragment;
import com.giggs.heroquest.game.GameConstants;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.providers.MyContentProvider;
import com.giggs.heroquest.utils.ApplicationUtils;
import com.giggs.heroquest.utils.MusicManager;

public class HomeActivity extends MyActivity implements OnClickListener, LoadGameFragment.FragmentCallbacks {

    private SharedPreferences mSharedPrefs;

    /**
     * UI
     */
    private ScreenState mScreenState = ScreenState.HOME;
    private Animation mMainButtonAnimationRightIn, mMainButtonAnimationRightOut, mMainButtonAnimationLeftIn, mMainButtonAnimationLeftOut;
    private Animation mFadeOutAnimation, mFadeInAnimation;
    private Button mNewGameButton, mSettingsButton, mLoadGameButton;
    private ViewGroup mSettingsLayout;
    private View mBackButton, mAppNameView;
    private Dialog mAboutDialog = null;
    private ImageView mStormsBg;
    private Runnable mStormEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setupUI();

        ApplicationUtils.showRateDialogIfNeeded(this);
        ApplicationUtils.showAdvertisementIfNeeded(this);

        showMainHomeButtons();
    }

    private void retrieveLoadGames() {
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... params) {
                return getContentResolver().query(MyContentProvider.URI_GAMES, new String[]{Game.COLUMN_ID}, null, null, Game.COLUMN_ID + " LIMIT 1");
            }

            @Override
            protected void onPostExecute(Cursor games) {
                super.onPostExecute(games);
                mLoadGameButton.setEnabled(games.getCount() > 0);
            }
        }.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStormsBg.removeCallbacks(mStormEffect);

        if (mAboutDialog != null) {
            mAboutDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.isShown()) {
            switch (v.getId()) {
                case R.id.newGameButton:
                    startActivity(new Intent(this, NewGameActivity.class));
                    finish();
                    break;
                case R.id.loadGameButton:
                    ApplicationUtils.openDialogFragment(this, new LoadGameFragment(), null);
                    hideMainHomeButtons();
                    mBackButton.setVisibility(View.GONE);
                    mBackButton.setAnimation(null);
                    break;
                case R.id.settingsButton:
                    showSettings();
                    break;
                case R.id.backButton:
                    onBackPressed();
                    break;
                case R.id.aboutButton:
                    openAboutDialog();
                    break;
                case R.id.rateButton:
                    ApplicationUtils.rateTheApp(this);
                    break;
                case R.id.shareButton:
                    ApplicationUtils.startSharing(this, getString(R.string.share_subject, getString(R.string.app_name)),
                            getString(R.string.share_message, getPackageName()), 0);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        switch (mScreenState) {
            case HOME:
                super.onBackPressed();
                break;
            case SETTINGS:
                showMainHomeButtons();
                hideSettings();
                break;
            default:
                break;
        }
    }

    private void setupUI() {
        mStormsBg = (ImageView) findViewById(R.id.storms);

        mMainButtonAnimationRightIn = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        mMainButtonAnimationLeftIn = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        mMainButtonAnimationRightOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mMainButtonAnimationLeftOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        mSettingsLayout = (ViewGroup) findViewById(R.id.settingsLayout);

        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        mNewGameButton = (Button) findViewById(R.id.newGameButton);
        mNewGameButton.setOnClickListener(this);

        mLoadGameButton = (Button) findViewById(R.id.loadGameButton);
        mLoadGameButton.setOnClickListener(this);

        mSettingsButton = (Button) findViewById(R.id.settingsButton);
        mSettingsButton.setOnClickListener(this);

        mBackButton = findViewById(R.id.backButton);
        mBackButton.setOnClickListener(this);

        mAppNameView = findViewById(R.id.appName);

        // volume radio buttons
        RadioGroup radioMusicVolume = (RadioGroup) findViewById(R.id.musicVolume);
        // update radio buttons states according to the music preference
        int musicVolume = mSharedPrefs.getInt(GameConstants.GAME_PREFS_KEY_MUSIC_VOLUME, GameConstants.MusicStates.ON.ordinal());
        ((RadioButton) radioMusicVolume.getChildAt(musicVolume)).setChecked(true);
        radioMusicVolume.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // enable / disable sound in preferences
                GameConstants.MusicStates newMusicState;
                switch (checkedId) {
                    case R.id.musicOn:
                        newMusicState = GameConstants.MusicStates.ON;
                        break;
                    default:
                        newMusicState = GameConstants.MusicStates.OFF;
                        break;
                }
                mSharedPrefs.edit().putInt(GameConstants.GAME_PREFS_KEY_MUSIC_VOLUME, newMusicState.ordinal()).apply();
                if (newMusicState == GameConstants.MusicStates.ON) {
                    MusicManager.playMusic(HomeActivity.this, getMusicResource());
                } else {
                    MusicManager.release();
                }
            }
        });

        // settings buttons
        findViewById(R.id.aboutButton).setOnClickListener(this);
        findViewById(R.id.shareButton).setOnClickListener(this);
        findViewById(R.id.rateButton).setOnClickListener(this);
    }

    private void openAboutDialog() {
        mAboutDialog = new Dialog(this, R.style.Dialog);
        mAboutDialog.setCancelable(true);
        mAboutDialog.setContentView(R.layout.dialog_about);
        // activate the dialog links
        TextView creditsTV = (TextView) mAboutDialog.findViewById(R.id.aboutCredits);
        creditsTV.setMovementMethod(LinkMovementMethod.getInstance());
        TextView contactTV = (TextView) mAboutDialog.findViewById(R.id.aboutContact);
        contactTV.setMovementMethod(LinkMovementMethod.getInstance());
        TextView sourcesTV = (TextView) mAboutDialog.findViewById(R.id.aboutSources);
        sourcesTV.setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) mAboutDialog.findViewById(R.id.version)).setText(getString(R.string.about_version, ApplicationUtils.getAppVersion(getApplicationContext())));
        mAboutDialog.show();
    }

    private void showButton(View view, boolean fromRight) {
        if (fromRight) {
            view.startAnimation(mMainButtonAnimationRightIn);
        } else {
            view.startAnimation(mMainButtonAnimationLeftIn);
        }
        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);
    }

    private void hideButton(final View view, boolean toRight) {
        if (toRight) {
            view.startAnimation(mMainButtonAnimationRightOut);
        } else {
            mMainButtonAnimationLeftOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(mMainButtonAnimationLeftOut);
        }
        view.setVisibility(View.GONE);
        view.setEnabled(false);
    }

    private void showMainHomeButtons() {
        mScreenState = ScreenState.HOME;
        mBackButton.startAnimation(mFadeOutAnimation);
        mBackButton.setVisibility(View.GONE);
        mAppNameView.startAnimation(mFadeInAnimation);
        mAppNameView.setVisibility(View.VISIBLE);
        showButton(mNewGameButton, true);
        showButton(mLoadGameButton, false);
        retrieveLoadGames();

        showButton(mSettingsButton, true);
    }

    private void hideMainHomeButtons() {
        mAppNameView.startAnimation(mFadeOutAnimation);
        mAppNameView.setVisibility(View.GONE);
        mBackButton.setVisibility(View.VISIBLE);
        mBackButton.startAnimation(mFadeInAnimation);
        hideButton(mNewGameButton, true);
        hideButton(mLoadGameButton, false);
        hideButton(mSettingsButton, true);
    }

    private void showSettings() {
        mScreenState = ScreenState.SETTINGS;
        mSettingsLayout.setVisibility(View.VISIBLE);
        mSettingsLayout.startAnimation(mFadeInAnimation);
        hideMainHomeButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // init storm effect
        mStormEffect = ApplicationUtils.addStormBackgroundAtmosphere(mStormsBg, 200, 50);
    }

    @Override
    protected int[] getMusicResource() {
        return new int[]{R.raw.main_menu};
    }

    private void hideSettings() {
        mSettingsLayout.setVisibility(View.GONE);
        mSettingsLayout.startAnimation(mFadeOutAnimation);
    }

    @Override
    public void OnFragmentClosed() {
        showMainHomeButtons();
        mBackButton.setVisibility(View.GONE);
        mBackButton.setAnimation(null);
    }

    private static enum ScreenState {
        HOME, SOLO, SETTINGS
    }

}
