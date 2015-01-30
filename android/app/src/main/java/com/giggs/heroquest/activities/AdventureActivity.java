package com.giggs.heroquest.activities;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.giggs.heroquest.MyActivity;
import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.adapters.QuestsAdapter;
import com.giggs.heroquest.data.BookFactory;
import com.giggs.heroquest.game.GameConstants;
import com.giggs.heroquest.game.gui.GameMenu;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.models.Quest;
import com.giggs.heroquest.providers.MyContentProvider;
import com.giggs.heroquest.utils.ApplicationUtils;
import com.giggs.heroquest.utils.MusicManager;
import com.giggs.heroquest.utils.billing.InAppBillingHelper;
import com.giggs.heroquest.utils.billing.OnBillingServiceConnectedListener;
import com.giggs.heroquest.views.CustomAlertDialog;

import java.util.List;

public class AdventureActivity extends MyActivity implements OnClickListener, OnBillingServiceConnectedListener {

    private static final String TAG = "BookChooserActivity";

    private InAppBillingHelper mInAppBillingHelper;
    private Game mGame;
    private List<Quest> mLstQuests;
    private SharedPreferences mSharedPrefs;
    private QuestsAdapter mQuestsAdapter;

    /**
     * UI
     */
    private ImageView mStormsBg;
    private Runnable mStormEffect;
    private Dialog mGameMenuDialog;
    private CustomAlertDialog mTutorialDialog;

    /**
     * Callbacks
     */
    private AdapterView.OnItemClickListener mOnStorySelectedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Quest selectedBook = mLstQuests.get(position);
            if (position != 0 && mSharedPrefs.getInt(GameConstants.TUTORIAL_DONE, 0) == 0) {
                showTutorialDialog(selectedBook);
            } else {
                onBookSelected(selectedBook);
            }
            mSharedPrefs.edit().putInt(GameConstants.TUTORIAL_DONE, 1).apply();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // retrieve active game
        mGame = (Game) getIntent().getExtras().getSerializable(Game.class.getName());
        mGame.setQuest(null);
        Uri gameUri = getContentResolver().insert(MyContentProvider.URI_GAMES, mGame.toContentValues());
        mGame.setId(ContentUris.parseId(gameUri));

        // retrieve quests
        mLstQuests = BookFactory.getAll();
        int nbQuestsDone = 0;
        for (Quest book : mLstQuests) {
            if (mGame.getBooksDone().get(book.getId()) != null) {
                book.setDone();
                nbQuestsDone++;
            }
        }

        Log.d(TAG, "nb quests done = " + nbQuestsDone);

        // show victory dialog
        if (nbQuestsDone == GameConstants.NB_QUESTS) {
            showVictoryDialog();
        }

        setupUI();

        mInAppBillingHelper = new InAppBillingHelper(this, this);
    }

    private void setupUI() {
        mStormsBg = (ImageView) findViewById(R.id.storms);

        ListView questsListView = (ListView) findViewById(R.id.quests);
        mQuestsAdapter = new QuestsAdapter(getApplicationContext(), R.layout.quest_item, mLstQuests);
        questsListView.setAdapter(mQuestsAdapter);
        questsListView.setOnItemClickListener(mOnStorySelectedListener);

        findViewById(R.id.shop_button).setOnClickListener(this);

        TextView heroNameTV = (TextView) findViewById(R.id.hero_name);
        heroNameTV.setText(mGame.getHero().getHeroName());
        heroNameTV.setCompoundDrawablesWithIntrinsicBounds(mGame.getHero().getImage(getResources()), 0, 0, 0);
        // TODO add characteriscs
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStormEffect = ApplicationUtils.addStormBackgroundAtmosphere(mStormsBg, 150, 50);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInAppBillingHelper.onDestroy();
    }

    @Override
    protected int[] getMusicResource() {
        return new int[]{R.raw.main_menu};
    }


    @Override
    protected void onPause() {
        super.onPause();

        getContentResolver().insert(MyContentProvider.URI_GAMES, mGame.toContentValues());

        mStormsBg.removeCallbacks(mStormEffect);

        if (mGameMenuDialog != null) {
            mGameMenuDialog.dismiss();
        }

        if (mTutorialDialog != null) {
            mTutorialDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        openGameMenu();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.shop_button:
                MusicManager.playSound(this, R.raw.button_sound);
                intent = new Intent(this, ShopActivity.class);
                intent.putExtra(Game.class.getName(), mGame);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onBillingServiceConnected() {
        boolean doIOwnFullGame = mInAppBillingHelper.doIOwn(GameConstants.FULL_GAME_PRODUCT_ID);
        if (doIOwnFullGame) {
            for (Quest quest : mLstQuests) {
                quest.setAvailable(true);
            }
            mQuestsAdapter.notifyDataSetChanged();
        }
    }

    private void showTutorialDialog(final Quest selectedBook) {
        // ask user if he wants to do the tutorial as he is a noob
        mTutorialDialog = new CustomAlertDialog(this, R.style.Dialog, getString(R.string.ask_tutorial), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MusicManager.playSound(getApplicationContext(), R.raw.button_sound);
                if (which == R.id.ok_btn) {
                    // go to tutorial
                    dialog.dismiss();
                    onBookSelected(BookFactory.buildTutorial());
                } else {
                    dialog.dismiss();
                    onBookSelected(selectedBook);
                }
            }
        });
        mTutorialDialog.show();
    }

    private void openGameMenu() {
        mGameMenuDialog = new GameMenu(this, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdventureActivity.this, HomeActivity.class));
                finish();
            }
        });

        mGameMenuDialog.show();
    }

    private void onBookSelected(Quest selectedBook) {
        mGame.setQuest(selectedBook);
        Intent intent = new Intent(this, selectedBook.getActivityClass());
        intent.putExtra(Game.class.getName(), mGame);
        startActivity(intent);
        finish();
    }

    private void showVictoryDialog() {
        if (mSharedPrefs.getString(GameConstants.FINISH_GAME_PREFS, null) == null) {
            Log.d(TAG, "Congratulations ! The game is finished");
            Dialog dialog = new Dialog(this, R.style.Dialog);
            dialog.setContentView(R.layout.dialog_victory);
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText(getString(R.string.congratulations_finish_game, getString(R.string.app_name)));
            title.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star, 0, R.drawable.ic_launcher);
            dialog.show();
            mSharedPrefs.edit().putString(GameConstants.FINISH_GAME_PREFS, "" + GameConstants.NB_QUESTS).apply();
        }
    }

}
