package com.giggs.heroquest.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.MyActivity;
import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.adapters.HeroesAdapter;
import com.giggs.heroquest.data.characters.HeroFactory;
import com.giggs.heroquest.game.GameConstants;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.utils.ApplicationUtils;
import com.giggs.heroquest.utils.MusicManager;
import com.giggs.heroquest.views.CustomCarousel;

import java.util.List;

public class NewGameActivity extends MyActivity {

    private List<Hero> mLstHeroes;

    /**
     * UI
     */
    private ImageView mStormsBg;
    private Runnable mStormEffect;
    private Dialog mHeroNameDialog;

    /**
     * Callbacks
     */
    private OnClickListener mOnHeroSelectedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            MusicManager.playSound(getApplicationContext(), R.raw.button_sound);
            int position = Integer.parseInt("" + v.getTag(R.string.id));
            Hero selectedHero = mLstHeroes.get(position);
            showNameInputDialog(selectedHero);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        mLstHeroes = HeroFactory.getAll();

        setupUI();
    }

    private void setupUI() {
        mStormsBg = (ImageView) findViewById(R.id.storms);

        // initMap carousel
        CustomCarousel heroesCarousel = (CustomCarousel) findViewById(R.id.heroes);
        HeroesAdapter heroesAdapter = new HeroesAdapter(this, R.layout.hero_item, mLstHeroes, mOnHeroSelectedListener);
        heroesCarousel.setAdapter(heroesAdapter);

        // start message animation
        Animation bigMessageAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.big_label_in_game);
        bigMessageAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.chooseHeroMessage).setAnimation(null);
                findViewById(R.id.chooseHeroMessage).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        findViewById(R.id.chooseHeroMessage).startAnimation(bigMessageAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStormEffect = ApplicationUtils.addStormBackgroundAtmosphere(mStormsBg, GameConstants.BG_ANIMATION_ALPHA_FROM, GameConstants.BG_ANIMATION_ALPHA_TO);
    }

    @Override
    protected int[] getMusicResource() {
        return new int[]{R.raw.main_menu};
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStormsBg.removeCallbacks(mStormEffect);

        if (mHeroNameDialog != null) {
            mHeroNameDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void showNameInputDialog(final Hero selectedHero) {
        mHeroNameDialog = new Dialog(this, R.style.Dialog);
        mHeroNameDialog.setContentView(R.layout.dialog_hero_name_input);

        final EditText heroNameInput = (EditText) mHeroNameDialog.findViewById(R.id.heroNameInput);
        heroNameInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        ApplicationUtils.showKeyboard(this, heroNameInput);
        heroNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                MusicManager.playSound(getApplicationContext(), R.raw.button_sound);
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || event != null
                        && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    validateHeroName(selectedHero, heroNameInput.getEditableText().toString());
                    return true;
                }
                return false;
            }
        });

        mHeroNameDialog.findViewById(R.id.ok_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicManager.playSound(getApplicationContext(), R.raw.button_sound);
                validateHeroName(selectedHero, heroNameInput.getEditableText().toString());
            }
        });

        mHeroNameDialog.setCancelable(true);
        mHeroNameDialog.show();
    }

    private void validateHeroName(Hero hero, String name) {
        if (!name.isEmpty()) {
            name = name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1).toLowerCase() : "");
            mHeroNameDialog.dismiss();
            hero.setHeroName(name);
            launchNewGame(hero);
        }
    }

    private void launchNewGame(Hero hero) {
        Game game = new Game();
        game.setHero(hero);
        Intent intent = new Intent(NewGameActivity.this, AdventureActivity.class);
        intent.putExtra(Game.class.getName(), game);
        startActivity(intent);
        finish();
    }

}
