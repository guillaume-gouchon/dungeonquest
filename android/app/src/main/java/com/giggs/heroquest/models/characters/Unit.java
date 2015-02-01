package com.giggs.heroquest.models.characters;

import android.util.Log;

import com.giggs.heroquest.data.characters.AllyFactory;
import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.game.base.GameElement;
import com.giggs.heroquest.game.graphics.UnitSprite;
import com.giggs.heroquest.models.FightResult;
import com.giggs.heroquest.models.dungeons.Tile;
import com.giggs.heroquest.models.effects.BuffEffect;
import com.giggs.heroquest.models.effects.CamouflageEffect;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.effects.PermanentEffect;
import com.giggs.heroquest.models.items.Characteristics;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.consumables.Consumable;
import com.giggs.heroquest.models.items.equipments.Equipment;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.Skill;
import com.giggs.heroquest.utils.pathfinding.MovingElement;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by guillaume ON 10/2/14.
 */
public abstract class Unit extends GameElement implements MovingElement<Tile> {

    private static final long serialVersionUID = 9185541600463971808L;
    private static final String TAG = "Unit";

    // Skills
    protected final List<Skill> skills = new ArrayList<>();
    protected final List<Item> items = new ArrayList<>();
    protected List<Effect> buffs = new ArrayList<>();

    // Possessions
    protected int gold;

    // Characteristics
    protected int hp;
    protected int currentHP;
    protected int spirit;
    protected int attack;
    protected int defense;
    protected int movement;

    public Unit(String identifier, Ranks rank, int hp, int currentHP, int attack, int defense, int spirit, int movement) {
        super(identifier, rank, 266, 468, UnitSprite.SPRITE_ANIM_X, UnitSprite.SPRITE_ANIM_Y);
        this.hp = hp;
        this.currentHP = currentHP;
        this.attack = attack;
        this.defense = defense;
        this.spirit = spirit;
        this.movement = movement;
        this.gold = 0;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public int getAttack() {
        return Math.max(0, attack + getBonusesFromBuffsAndEquipments(Characteristics.ATTACK));
    }

    public int getDefense() {
        return Math.max(0, defense + getBonusesFromBuffsAndEquipments(Characteristics.DEFENSE));
    }

    public int getSpirit() {
        return Math.max(0, spirit + getBonusesFromBuffsAndEquipments(Characteristics.SPIRIT));
    }

    public int getGold() {
        return gold;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    @Override
    public boolean canMoveIn(Tile tile) {
        return tile.getGround() != null && tile.getContent() == null;
    }

    @Override
    public void createSprite(VertexBufferObjectManager vertexBufferObjectManager) {
        sprite = new UnitSprite(this, vertexBufferObjectManager);
    }

    public float getLifeRatio() {
        return (float) currentHP / (float) hp;
    }

    public FightResult attack(Unit target) {
        FightResult fightResult;

        // remove invisibility
        boolean isInvisible = false;
        for (Effect buff : buffs) {
            if (buff instanceof CamouflageEffect) {
                buffs.remove(buff);
                updateSprite();
                isInvisible = true;
                break;
            }
        }

        //TODO
//
//        int dice = (int) (Math.random() * 100);
//
//        if (isRangeAttack()) {
//            if (isNextTo(target.getTilePosition())) {
//                // malus for range weapon in close combat
//                dice = Math.min(99, dice + 20);
//            } else {
//                // range attack bonus or malus depending on dexterity
//                dice = Math.max(0, dice - (dexterity - 12) * 3);
//            }
//        }
//
//        Log.d(TAG, "attack dice = " + dice);
//
//        int critical = calculateCritical();
//
//        if (isInvisible || dice < critical) {
//            int criticalDamage = Math.max(0, calculateDamageNaturalBonus() + getBonusesFromBuffsAndEquipments(Characteristics.DAMAGE)
//                    + (equipments[0] != null ? calculateCriticalDamage((Weapon) equipments[0]) : 0) + (equipments[1] != null ? calculateCriticalDamage((Weapon) equipments[1]) / 2 : 0));
//            fightResult = new FightResult(FightResult.States.CRITICAL, (int) Math.max(0, criticalDamage * 1.3f - target.calculateProtection()));
//        } else if (dice >= 95) {
//            fightResult = new FightResult(FightResult.States.MISS, 0);
//        } else if (dice > 95 - target.calculateBlock()) {
//            fightResult = new FightResult(FightResult.States.BLOCK, 0);
//        } else {
//            int dodgeDice = (int) (Math.random() * 100);
//            Log.d(TAG, "dodge dice = " + dodgeDice);
//            if (dodgeDice < target.calculateDodge()) {
//                fightResult = new FightResult(FightResult.States.DODGE, 0);
//            } else {
//                int damage = Math.max(0, calculateDamageNaturalBonus() + getBonusesFromBuffsAndEquipments(Characteristics.DAMAGE)
//                        + (equipments[0] != null ? calculateDamage((Weapon) equipments[0]) : 0) + (equipments[1] != null ? calculateDamage((Weapon) equipments[1]) / 2 : 0));
//                fightResult = new FightResult(FightResult.States.DAMAGE, Math.max(0, damage - target.calculateProtection()));
//            }
//        }
//
//        Log.d(TAG, "dealing " + fightResult.getDamage() + " damage");
//        target.takeDamage(fightResult.getDamage());
//
//        // add weapons special effects
//        if (fightResult.getDamage() > 0) {
//            if (equipments[0] != null) {
//                applyWeaponEffect(equipments[0], target);
//            }
//            if (equipments[1] != null) {
//                applyWeaponEffect(equipments[1], target);
//            }
//        }

        return null;
    }

    private static void applyWeaponEffect(Equipment equipment, Unit target) {
        for (Effect effect : equipment.getEffects()) {
            if (!(effect instanceof PermanentEffect)) {
                target.getBuffs().add(effect);
            }
        }
    }

    public void takeDamage(int damage) {
        currentHP -= damage;
    }

    public boolean isDead() {
        return currentHP <= 0;
    }

    public List<Effect> getBuffs() {
        return buffs;
    }

    public int calculateMovement() {
        return Math.max(1, movement + getBonusesFromBuffsAndEquipments(Characteristics.MOVEMENT));
    }

    private int getBonusesFromBuffsAndEquipments(Characteristics characteristic) {
        int bonus = 0;
        for (Effect buff : buffs) {
            if (buff.getTarget() == characteristic) {
                bonus += buff.getValue();
            }
        }
//TODO
        Equipment equipment;
        int base = getCharacteristic(characteristic);
        for (int n = 0; n < items.size(); n++) {
            if (items.get(n) != null && items.get(n) instanceof Equipment) {
                equipment = (Equipment) items.get(n);
                for (Effect buff : equipment.getEffects()) {
//                    if (buff.getTarget() == characteristic) {
//                        if (buff.getValue() == 1) {
//                            bonus++;
//                        } else {
//                            base = Math.max(base, buff.getValue());
//                        }
//                    }
                }
            }
        }

        return base + bonus;
    }

    public void initNewTurn() {
        // consume and remove ended buffs
        List<Effect> copy = new ArrayList<>(buffs);
        for (Effect buff : copy) {
            boolean isOver = buff.consume();
            if (isOver) {
                Log.d(TAG, "buff is over =" + buff.getTarget());
                buffs.remove(buff);
            }
        }
        updateSprite();
    }

    public void updateSprite() {
        Log.d(TAG, "update sprite with buffs");
        boolean isInvisible = false;
        boolean isColored = false;
        for (Effect effect : buffs) {
            if (effect instanceof CamouflageEffect) {
                isInvisible = true;
            } else if (!isColored && effect instanceof BuffEffect && ((BuffEffect) effect).getBuffColor() != null) {
                isColored = true;
                sprite.setColor(((BuffEffect) effect).getBuffColor());
            }
        }

        sprite.setAlpha(isInvisible ? 0.3f : 1);
        if (!isColored) {
            sprite.setColor(new Color(1, 1, 1, sprite.getAlpha()));
        }
    }


    public boolean addItem(Item item) {
        items.add(item);
        return true;
    }

    public void use(Consumable consumable) {
        items.remove(consumable);
    }

    public boolean testCharacteristic(Characteristics target, int value) {
        int dice = (int) (Math.random() * 20);
        Log.d(TAG, "characteristic dice result = " + dice);
        return dice == 1 || dice < getCharacteristic(target) - value;
    }

    public int getCharacteristic(Characteristics target) {
        switch (target) {
            case SPIRIT:
                return getSpirit();
        }
        return 0;
    }

    public ActiveSkill getAvailableSkill() {
        Collections.shuffle(skills);
        for (Skill skill : skills) {
            if (skill instanceof ActiveSkill && !((ActiveSkill) skill).isUsed()) {
                return (ActiveSkill) skill;
            }
        }
        return null;
    }

    public boolean hasItem(Item item) {
        for (Item myItem : items) {
            if (myItem.getIdentifier().equals(item.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    public boolean isRangeAttack() {
        return hasItem(ItemFactory.buildCrossbow()) || identifier.equals(AllyFactory.buildCrossbowman().getIdentifier());
    }

}
