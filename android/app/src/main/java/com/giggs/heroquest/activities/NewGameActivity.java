package com.giggs.heroquest.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.MyActivity;
import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.adapters.HeroesAdapter;
import com.giggs.heroquest.data.characters.HeroFactory;
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
    private AlertDialog mHeroNameDialog;

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

        // init carousel
        CustomCarousel heroesCarousel = (CustomCarousel) findViewById(R.id.heroes);
        HeroesAdapter heroesAdapter = new HeroesAdapter(this, R.layout.hero_chooser_item, mLstHeroes, mOnHeroSelectedListener);
        heroesCarousel.setAdapter(heroesAdapter);

        // start message animation
        findViewById(R.id.chooseHeroMessage).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.big_label_in_game));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStormEffect = ApplicationUtils.addStormBackgroundAtmosphere(mStormsBg, 150, 50);
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
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_hero_name_input, null);

        mHeroNameDialog = new AlertDialog.Builder(this, R.style.Dialog).setView(view).create();

        final EditText heroNameInput = (EditText) view.findViewById(R.id.heroNameInput);
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

        view.findViewById(R.id.ok_btn).setOnClickListener(new OnClickListener() {
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
