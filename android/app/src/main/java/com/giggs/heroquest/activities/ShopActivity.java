package com.giggs.heroquest.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.giggs.heroquest.MyActivity;
import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.adapters.ItemsAdapter;
import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.game.gui.items.ItemInfoInShop;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.equipments.weapons.Weapon;
import com.giggs.heroquest.utils.MusicManager;
import com.giggs.heroquest.views.CustomAlertDialog;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

public class ShopActivity extends MyActivity implements OnClickListener {

    private static final String TAG = "ShopActivity";

    private Game mGame;
    private List<Item> mItemsOffered = new ArrayList<>();
    private ItemsAdapter mOffersAdapter;
    private ItemsAdapter mBagAdapter;

    /**
     * UI
     */
    private TextView mGoldAmount;
    private Dialog mItemInfoDialog;
    private HListView mOffersListView;

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
        mOffersListView = (HListView) findViewById(R.id.offers);
        mOffersAdapter = new ItemsAdapter(getApplicationContext(), R.layout.item, mItemsOffered);
        mOffersListView.setAdapter(mOffersAdapter);
        mOffersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showItemInfo(mOffersAdapter.getItem(position), false);
            }
        });

        HListView bagItemsListView = (HListView) findViewById(R.id.bag);
        mBagAdapter = new ItemsAdapter(getApplicationContext(), R.layout.item, mGame.getHero().getItems());
        bagItemsListView.setAdapter(mBagAdapter);
        bagItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showItemInfo(mBagAdapter.getItem(position), true);
            }
        });

        mGoldAmount = (TextView) findViewById(R.id.gold_amount);

        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    protected int[] getMusicResource() {
        return new int[]{R.raw.main_menu};
    }

    @Override
    protected void onPause() {
        super.onPause();

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
        mItemsOffered = ItemFactory.getAvailableItemsInShop(mGame.getHero().getHeroType());

        // remove already owned items
        List<Item> copy = new ArrayList<>(mItemsOffered);
        for (Item item : copy) {
            if (item instanceof Weapon && mGame.getHero().hasItem(item)) {
                mItemsOffered.remove(item);
            }
        }

        mOffersAdapter = new ItemsAdapter(getApplicationContext(), R.layout.item, mItemsOffered);
        mOffersListView.setAdapter(mOffersAdapter);
    }

    private void updateBag() {
        mBagAdapter.notifyDataSetChanged();
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
        updateGoldAmount();
        updateBag();
        updateOffers();
    }

    private void sellItem(Item item) {
        mGame.getHero().addGold(item.getSellPrice());
        mGame.getHero().getItems().remove(item);
        updateGoldAmount();
        updateBag();
        updateOffers();
    }

}
