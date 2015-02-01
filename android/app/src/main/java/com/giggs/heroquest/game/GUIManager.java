package com.giggs.heroquest.game;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.AdventureActivity;
import com.giggs.heroquest.activities.HomeActivity;
import com.giggs.heroquest.activities.adapters.ItemsAdapter;
import com.giggs.heroquest.activities.fragments.StoryFragment;
import com.giggs.heroquest.game.base.MyBaseGameActivity;
import com.giggs.heroquest.game.gui.GameMenu;
import com.giggs.heroquest.game.gui.Loading;
import com.giggs.heroquest.game.gui.RewardDialog;
import com.giggs.heroquest.game.gui.items.ItemInfoInGame;
import com.giggs.heroquest.game.gui.skills.UseSkill;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.models.Quest;
import com.giggs.heroquest.models.Reward;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.characters.Unit;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.PassiveSkill;
import com.giggs.heroquest.models.skills.Skill;
import com.giggs.heroquest.utils.ApplicationUtils;
import com.giggs.heroquest.views.CustomAlertDialog;
import com.giggs.heroquest.views.LifeBar;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

public class GUIManager {

    private static final String TAG = "GUIManager";

    private MyBaseGameActivity mGameActivity;
    private Resources mResources;
    private Hero mHero;

    private Dialog mLoadingScreen, mGameMenuDialog, mRewardDialog, mBagDialog, mItemInfoDialog;
    private TextView mBigLabel, mSpiritTV;
    private Animation mBigLabelAnimation;
    private ViewGroup mQueueLayout, mLifeLayout;
    private ViewGroup mSkillButtonsLayout;
    private CustomAlertDialog mConfirmDialog;

    public GUIManager(MyBaseGameActivity activity) {
        mGameActivity = activity;
        mResources = mGameActivity.getResources();
    }

    public void initGUI() {
        mQueueLayout = (ViewGroup) mGameActivity.findViewById(R.id.queue);

        mGameActivity.findViewById(R.id.bag).setOnClickListener(mGameActivity);

        mBigLabelAnimation = AnimationUtils.loadAnimation(mGameActivity, R.anim.big_label_in_game);
        mBigLabel = (TextView) mGameActivity.findViewById(R.id.bigLabel);

        mSkillButtonsLayout = (ViewGroup) mGameActivity.findViewById(R.id.skillButtonsLayout);

        mLifeLayout = (ViewGroup) mGameActivity.findViewById(R.id.life);

        mSpiritTV = (TextView) mGameActivity.findViewById(R.id.spirit);
    }

    public void setData(Hero hero) {
        mHero = hero;
    }

    public void displayBigLabel(final String text, final int color) {
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBigLabel.setVisibility(View.VISIBLE);
                mBigLabel.setText("" + text);
                mBigLabel.setTextColor(mResources.getColor(color));
                mBigLabel.startAnimation(mBigLabelAnimation);
            }
        });
    }

    public void hideLoadingScreen() {
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingScreen.dismiss();
            }
        });
    }

    public void openGameMenu() {
        mGameMenuDialog = new GameMenu(mGameActivity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaveQuestConfirmDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameActivity.startActivity(new Intent(mGameActivity, HomeActivity.class));
                mGameActivity.finish();
            }
        });

        mGameMenuDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mGameActivity.resumeGame();
            }
        });

        mGameMenuDialog.show();
    }

    public void showLeaveQuestConfirmDialog() {
        mConfirmDialog = new CustomAlertDialog(mGameActivity, R.style.Dialog, mGameActivity.getString(R.string.confirm_leave_quest),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == R.id.ok_btn) {
                            Intent intent = new Intent(mGameActivity, AdventureActivity.class);
                            mGameActivity.getGame().setHero(mHero);
                            intent.putExtra(Game.class.getName(), mGameActivity.getGame());
                            mGameActivity.startActivity(intent);
                            mGameActivity.finish();
                        }
                        dialog.dismiss();
                    }
                });
        mConfirmDialog.show();
    }

    public void showFinishQuestConfirmDialog() {
        mConfirmDialog = new CustomAlertDialog(mGameActivity, R.style.Dialog, mGameActivity.getString(R.string.confirm_finish_quest),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == R.id.ok_btn) {
                            finishQuest();
                        }
                        dialog.dismiss();
                    }
                });
        mConfirmDialog.show();
    }

    private void finishQuest() {
        Game game = mGameActivity.getGame();
        Quest currentQuest = game.getQuest();
        game.setHero(mHero);
        game.setQuest(null);

        // quest is finished go to book chooser activity
        game.finishQuest();

        if (currentQuest.getOutroText(mResources) > 0) {
            // show outro text
            Bundle args = new Bundle();
            args.putInt(StoryFragment.ARGUMENT_STORY, currentQuest.getOutroText(mResources));
            args.putBoolean(StoryFragment.ARGUMENT_IS_OUTRO, true);
            ApplicationUtils.openDialogFragment(mGameActivity, new StoryFragment(), args);
        } else {
            // go directly to the book chooser
            Intent intent = new Intent(mGameActivity, AdventureActivity.class);
            intent.putExtra(Game.class.getName(), game);
            mGameActivity.startActivity(intent);
            mGameActivity.finish();
        }
    }

    public void onPause() {
        if (mGameMenuDialog != null) {
            mGameMenuDialog.dismiss();
        }
        if (mLoadingScreen != null) {
            mLoadingScreen.dismiss();
        }
        if (mRewardDialog != null) {
            mRewardDialog.dismiss();
        }
        if (mBagDialog != null) {
            mBagDialog.dismiss();
        }
        if (mItemInfoDialog != null) {
            mItemInfoDialog.dismiss();
        }
        if (mConfirmDialog != null) {
            mConfirmDialog.dismiss();
        }
    }

    public void showLoadingScreen() {
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingScreen = new Loading(mGameActivity);
                mLoadingScreen.show();
            }
        });
    }

    public void updateHeroLayout() {
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpiritTV.setText("" + mHero.getSpirit());
                mLifeLayout.removeAllViews();
                ImageView view = new ImageView(mGameActivity.getApplicationContext());
                view.setImageResource(R.drawable.ic_health);
                for (int n = 0; n < mHero.getHp(); n++) {
                    if (mHero.getCurrentHP() < n) {
                        view.setColorFilter(Color.argb(100, 0, 0, 0));
                    }
                    mLifeLayout.addView(view);
                }
            }
        });
    }

    public void updateQueue(final Unit activeCharacter, final Quest quest) {
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (quest.isSafe()) {
                    mQueueLayout.setVisibility(View.GONE);
                } else {
                    mQueueLayout.setVisibility(View.VISIBLE);
                    updateQueueCharacter((ViewGroup) mQueueLayout.findViewById(R.id.activeCharacter), activeCharacter);
                    if (quest.getQueue().size() > 1) {
                        updateQueueCharacter((ViewGroup) mQueueLayout.findViewById(R.id.nextCharacter), quest.getQueue().get(0));
                    } else {
                        mQueueLayout.findViewById(R.id.nextCharacter).setVisibility(View.GONE);
                    }
                    if (quest.getQueue().size() > 2) {
                        updateQueueCharacter((ViewGroup) mQueueLayout.findViewById(R.id.nextnextCharacter), quest.getQueue().get(1));
                    } else {
                        mQueueLayout.findViewById(R.id.nextnextCharacter).setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void updateQueueCharacter(ViewGroup view, Unit unit) {
        view.setVisibility(View.VISIBLE);
        ((ImageView) view.findViewById(R.id.image)).setImageResource(unit.getImage(mResources));
        ((LifeBar) view.findViewById(R.id.life)).updateLife(unit.getLifeRatio());
    }

    public void showReward(final Reward reward, final OnDismissListener onDismissListener) {
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRewardDialog == null || !mRewardDialog.isShowing()) {
                    mRewardDialog = new RewardDialog(mGameActivity, reward);
                    mRewardDialog.setOnDismissListener(onDismissListener);
                    mRewardDialog.show();
                }
            }
        });
    }

    public void showBag() {
        mBagDialog = new Dialog(mGameActivity, R.style.Dialog);
        mBagDialog.setContentView(R.layout.in_game_bag);
        mBagDialog.setCancelable(true);

        HListView bagItemsListView = (HListView) mBagDialog.findViewById(R.id.bag);
        final ItemsAdapter bagAdapter = new ItemsAdapter(mGameActivity.getApplicationContext(), R.layout.item, mHero.getItems());
        bagItemsListView.setAdapter(bagAdapter);
        bagItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setTag(R.string.item, bagAdapter.getItem(position));
                mGameActivity.onClick(view);
            }
        });

        mBagDialog.show();
    }

    public void showItemInfo(Item item, ItemInfoInGame.OnItemActionSelected onItemActionCallback) {
        if (mItemInfoDialog == null || !mItemInfoDialog.isShowing()) {
            mItemInfoDialog = new ItemInfoInGame(mGameActivity, item, mHero, onItemActionCallback);
            mItemInfoDialog.show();
        }
    }

    public void updateSkillButtons() {
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = mGameActivity.getLayoutInflater();
                mSkillButtonsLayout.removeAllViews();
                View skillButton;
                for (final Skill skill : mHero.getSkills()) {
                    if (skill.getLevel() > 0) {
                        skillButton = inflater.inflate(R.layout.in_game_skill_button, null);
                        skillButton.setTag(R.string.show_skill, skill);
                        ((ImageView) skillButton.findViewById(R.id.image)).setImageResource(skill.getImage(mResources));
                        if (skill instanceof PassiveSkill || skill instanceof ActiveSkill && ((ActiveSkill) skill).isUsed()) {
                            skillButton.findViewById(R.id.image).setEnabled(false);
                            skillButton.setAlpha(0.5f);
                        }

                        skillButton.setOnClickListener(mGameActivity);

                        mSkillButtonsLayout.addView(skillButton);
                    }
                }
            }
        });
    }

    public void showUseSkillInfo(Skill skill) {
        if (mItemInfoDialog == null || !mItemInfoDialog.isShowing()) {
            mItemInfoDialog = new UseSkill(mGameActivity, skill, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGameActivity.onClick(v);
                }
            });

            mItemInfoDialog.show();
        }
    }

    public void hideBag() {
        if (mBagDialog != null) {
            mBagDialog.dismiss();
        }
    }

}
