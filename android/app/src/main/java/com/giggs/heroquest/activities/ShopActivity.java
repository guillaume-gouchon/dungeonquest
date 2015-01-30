package com.giggs.heroquest.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.MyActivity;
import com.giggs.heroquest.R;
import com.giggs.heroquest.game.gui.items.ItemInfoInShop;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.utils.ApplicationUtils;
import com.giggs.heroquest.utils.MusicManager;
import com.giggs.heroquest.views.CustomAlertDialog;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends MyActivity implements OnClickListener {

    private static final String TAG = "ShopActivity";

    private Game mGame;
    private List<Item> mItemsOffered = new ArrayList<>();

    /**
     * UI
     */
    private ImageView mStormsBg;
    private TextView mGoldAmount;
    private Runnable mStormEffect;
    private ViewGroup mOffers, mBag;
    private Dialog mItemInfoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        mGame = (Game) getIntent().getExtras().getSerializable(Game.class.getName());

        setupUI();

        updateGoldAmount();
        updateBag();
        updateOffers();
    }

    private void setupUI() {
        mStormsBg = (ImageView) findViewById(R.id.storms);

        mOffers = (ViewGroup) findViewById(R.id.shop_offers);
        mBag = (ViewGroup) findViewById(R.id.bag);
        mGoldAmount = (TextView) findViewById(R.id.gold_amount);

        findViewById(R.id.back_button).setOnClickListener(this);
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

        if (mItemInfoDialog != null) {
            mItemInfoDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        goBackToBookChooser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                MusicManager.playSound(this, R.raw.button_sound);
                goBackToBookChooser();
                break;
        }

        if (v.getTag(R.string.sell_item) != null) {
            showItemInfo((Item) v.getTag(R.string.sell_item), true);
        } else if (v.getTag(R.string.buy_item) != null) {
            showItemInfo((Item) v.getTag(R.string.buy_item), false);
        }
    }

    private void goBackToBookChooser() {
        Intent intent = new Intent(ShopActivity.this, AdventureActivity.class);
        intent.putExtra(Game.class.getName(), mGame);
        startActivity(intent);
        finish();
    }

    private void updateGoldAmount() {
        mGoldAmount.setText("" + mGame.getHero().getGold());
    }

    private void updateOffers() {
        //TODO : get offers
        for (int n = 0; n < 10; n++) {
            updateItemLayout(mOffers.getChildAt(n), mItemsOffered.get(n), false);
        }
    }

    private void updateBag() {
        Hero hero = mGame.getHero();
        Item item;
        for (int n = 5; n < mBag.getChildCount(); n++) {
            item = null;
            if (n - 5 < hero.getItems().size()) {
                item = hero.getItems().get(n - 5);
            }
            updateItemLayout(mBag.getChildAt(n), item, true);
        }
    }

    private void updateItemLayout(View itemView, Item item, boolean isSellingItem) {
        View background = itemView.findViewById(R.id.background);
        ImageView image = (ImageView) itemView.findViewById(R.id.image);

        itemView.setTag(isSellingItem ? R.string.sell_item : R.string.buy_item, item);

        if (item != null) {
            background.setBackgroundColor(getResources().getColor(item.getColor()));
            image.setImageResource(item.getImage(getResources()));
            itemView.setEnabled(true);
            itemView.setOnClickListener(this);
        } else if (itemView.isEnabled()) {
            background.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            image.setImageResource(0);
            itemView.setEnabled(false);
            itemView.setOnClickListener(null);
        }
    }

    public void showItemInfo(final Item item, final boolean isSelling) {
        if (mItemInfoDialog == null || !mItemInfoDialog.isShowing()) {
            mItemInfoDialog = new ItemInfoInShop(this, item, mGame.getHero(), isSelling, new ItemInfoInShop.OnItemActionSelected() {
                @Override
                public void onActionExecuted(ItemInfoInShop.ItemActionsInShop action) {
                    Dialog confirmationDialog = new CustomAlertDialog(ShopActivity.this, R.style.Dialog,
                            getString(R.string.transaction_confirmation, getString(isSelling ? R.string.sell : R.string.buy), getString(item.getName(getResources()))),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (which == R.id.ok_btn) {
                                        if (isSelling) {
                                            sellItem(item);
                                        } else {
                                            buyItem(item);
                                        }
                                        mItemInfoDialog.dismiss();
                                    }
                                }
                            });
                    confirmationDialog.show();
                }
            });

            mItemInfoDialog.show();
        }
    }

    private void buyItem(Item item) {
        mGame.getHero().addGold(-item.getPrice());
        mGame.getHero().getItems().add(item);
        mItemsOffered.set(mItemsOffered.indexOf(item), null);
        updateGoldAmount();
        updateBag();
        updateOffers();
    }

    private void sellItem(Item item) {
        mGame.getHero().addGold(item.getSellPrice());
        mGame.getHero().getItems().remove(item);
        updateGoldAmount();
        updateBag();
    }

}
