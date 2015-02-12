package com.giggs.heroquest.models.characters;

import android.util.Log;

import com.giggs.heroquest.data.characters.AllyFactory;
import com.giggs.heroquest.data.characters.HeroFactory;
import com.giggs.heroquest.data.characters.MonsterFactory;
import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.game.base.GameElement;
import com.giggs.heroquest.game.graphics.UnitSprite;
import com.giggs.heroquest.models.FightResult;
import com.giggs.heroquest.models.dungeons.Tile;
import com.giggs.heroquest.models.effects.BuffEffect;
import com.giggs.heroquest.models.effects.CamouflageEffect;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.effects.LifeStealEffect;
import com.giggs.heroquest.models.effects.PermanentEffect;
import com.giggs.heroquest.models.items.Characteristics;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.consumables.Consumable;
import com.giggs.heroquest.models.items.equipments.Equipment;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.Skill;
import com.giggs.heroquest.utils.pathfinding.MathUtils;
import com.giggs.heroquest.utils.pathfinding.MovingElement;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    protected int currentSpirit;
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
        this.currentSpirit = spirit;
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

    public int getCurrentSpirit() {
        return currentSpirit;
    }

    public void setCurrentSpirit(int currentSpirit) {
        this.currentSpirit = currentSpirit;
    }

    public int getAttack() {
        return Math.max(0, getBonusesFromBuffsAndEquipments(Characteristics.ATTACK));
    }

    public int getDefense() {
        return Math.max(0, getBonusesFromBuffsAndEquipments(Characteristics.DEFENSE));
    }

    public int getSpirit() {
        return Math.max(0, getBonusesFromBuffsAndEquipments(Characteristics.SPIRIT));
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

    public int getAttackAgainst(Unit target) {
        int attack = getAttack();

        if (MathUtils.calcManhattanDistance(tilePosition, target.getTilePosition()) > 1 && hasItem(ItemFactory.buildCrossbow())) {
            return 3;
        }

        // spirit blade modifier
        if (hasItem(ItemFactory.buildSpiritBlade()) &&
                (target.getIdentifier().equals(MonsterFactory.buildSkeleton().getIdentifier())
                        || target.getIdentifier().equals(MonsterFactory.buildZombie().getIdentifier())
                        || target.getIdentifier().equals(MonsterFactory.buildMummy().getIdentifier())
                        || target.getIdentifier().equals(MonsterFactory.buildWitchLord().getIdentifier()))) {
            attack++;
        }

        if (identifier.equals(HeroFactory.buildDrow().getIdentifier()) && (target.calculateMovement() < 6 || target.getDefense() > 3)) {
            attack++;
        }

        return attack;
    }

    public float getLifeRatio() {
        return (float) currentHP / (float) hp;
    }

    public FightResult attack(Unit target) {
        FightResult fightResult;

        // remove invisibility
        for (Effect buff : buffs) {
            if (buff instanceof CamouflageEffect) {
                buffs.remove(buff);
                updateSprite();
                break;
            }
        }

        Random random;
        int attackScore = 0;
        for (int n = 0; n < getAttackAgainst(target); n++) {
            random = new Random();
            if (random.nextInt(6) < 3) {
                attackScore++;
            }
        }

        Log.d(TAG, "attack = " + attackScore);

        if (attackScore > 0) {
            // calculate defense score
            int defenseScore = 0;
            int die;
            for (int n = 0; n < target.getDefense(); n++) {
                random = new Random();
                die = random.nextInt(6);
                if (die == 0 || die == 1 && !(target instanceof Monster)) {
                    defenseScore++;
                }
            }

            Log.d(TAG, "defense = " + defenseScore);

            int damage = attackScore - defenseScore;
            if (damage <= 0) {
                fightResult = new FightResult(FightResult.States.BLOCK, attackScore, defenseScore);
            } else {
                fightResult = new FightResult(FightResult.States.DAMAGE, attackScore, defenseScore);
            }
        } else {
            fightResult = new FightResult(FightResult.States.MISS, 0, 0);
        }

        Log.d(TAG, "dealing " + fightResult.getDamage() + " damage");
        target.takeDamage(fightResult.getDamage());

        if (fightResult.getDamage() > 0) {
            // life steal effect
            for (Effect buff : buffs) {
                if (buff instanceof LifeStealEffect) {
                    currentHP += buff.getValue();
                    break;
                }
            }
        }

        return fightResult;
    }

    private static void applyWeaponEffect(Equipment equipment, Unit target) {
        for (Effect effect : equipment.getEffects()) {
            if (!(effect instanceof PermanentEffect)) {
                target.getBuffs().add(effect);
            }
        }
    }

    public void takeDamage(int damage) {
        if (identifier.equals("gnome") && currentSpirit > 1) {
            // shield absorb damage
            int extraDamage = damage - (currentSpirit - 1);
            currentSpirit = Math.max(1, currentSpirit - damage);
            if (extraDamage > 0) {
                currentHP -= extraDamage;
            }
        } else {
            currentHP -= damage;
        }
    }

    public boolean isDead() {
        return currentHP <= 0;
    }

    public List<Effect> getBuffs() {
        return buffs;
    }

    public int calculateMovement() {
        return movement;
    }

    private int getBonusesFromBuffsAndEquipments(Characteristics characteristic) {
        int base = getBaseCharacteristic(characteristic);
        int bonus = 0;

        for (Effect buff : buffs) {
            if (buff.getTarget() == characteristic) {
                bonus += buff.getValue();
            }
        }

        Equipment equipment;
        for (int n = 0; n < items.size(); n++) {
            if (items.get(n) != null && items.get(n) instanceof Equipment) {
                equipment = (Equipment) items.get(n);
                for (Effect buff : equipment.getEffects()) {
                    if (buff.getTarget() == characteristic) {
                        if (buff.getValue() == 1) {
                            if (equipment.getIdentifier().equals(ItemFactory.buildShield().getIdentifier()) && hasItem(ItemFactory.buildBattleAxe())) {
                                // shield and battle axe cannot be worn together
                            } else {
                                bonus++;
                            }
                        } else {
                            if (!equipment.getIdentifier().equals(ItemFactory.buildCrossbow())) {
                                base = Math.max(base, buff.getValue());
                            }
                        }
                    }
                }
            }
        }

        Log.d(TAG, "base = " + base + ", bonus = " + bonus);

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
        int dice = (int) (Math.random() * 6);
        Log.d(TAG, "characteristic dice result = " + dice);
        return dice == 0 || dice < getCharacteristic(target) - value;
    }

    public int getBaseCharacteristic(Characteristics target) {
        switch (target) {
            case SPIRIT:
                return spirit;
            case ATTACK:
                return attack;
            case DEFENSE:
                return defense;

        }
        return 0;
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
