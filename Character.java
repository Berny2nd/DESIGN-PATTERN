import java.util.ArrayList;
import java.util.List;

public abstract class Character {

    protected String name;
    protected int hp;
    protected int maxHp;
    protected int mana;
    protected int maxMana;
    protected int speed;
    protected int baseDamage;
    protected List<Item> inventory;
    protected List<StatusEffect> activeEffects;

    public Character(String name, int maxHp, int maxMana, int speed, int baseDamage) {
        this.name       = name;
        this.hp         = maxHp;
        this.maxHp      = maxHp;
        this.mana       = maxMana;
        this.maxMana    = maxMana;
        this.speed      = speed;
        this.baseDamage = baseDamage;
        this.inventory  = new ArrayList<>();
        this.activeEffects = new ArrayList<>();
    }

    public abstract void useAbility(int abilityIndex, List<Enemy> enemies, List<Character> allies);

    public void attack(Enemy target) {
        System.out.println(name + " attacks " + target.getName() + " for " + baseDamage + " damage!");
        target.takeDamage(baseDamage);
    }

    public void defend() {
        System.out.println(name + " takes a defensive stance — incoming damage halved this turn.");
    }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
        System.out.println(name + " takes " + amount + " damage. HP: " + hp + "/" + maxHp);
    }

    public void useItem(int index) {
        if (index < 0 || index >= inventory.size()) {
            System.out.println("No item at that slot.");
            return;
        }
        Item item = inventory.remove(index);
        item.use(this);
    }

    public void applyStatusEffect(StatusEffect effect) {
        activeEffects.add(effect);
        System.out.println(name + " is affected by " + effect);
    }

    public void tickEffects() {
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect effect : activeEffects) {
            int dot = effect.getTickDamage();
            if (dot > 0) {
                takeDamage(dot);
                System.out.println(name + " suffers " + dot
                        + " damage from " + effect.getType().name().toLowerCase() + ".");
            }
            effect.tick();
            if (effect.isExpired()) expired.add(effect);
        }
        activeEffects.removeAll(expired);
    }

    public boolean hasEffect(StatusEffect.Type type) {
        for (StatusEffect e : activeEffects) {
            if (e.getType() == type) return true;
        }
        return false;
    }

    public void restoreHp(int amount) {
        hp = Math.min(hp + amount, maxHp);
    }

    /**
     * Scale up all player stats by the given multiplier.
     * Called after defeating an enemy (Mob x1.2, SubBoss x1.5, FinalBoss x1.7).
     * Current HP is scaled proportionally so the player doesn't instantly heal or drop dead.
     */
    public void applyStatBoost(double multiplier, String reason) {
        double hpRatio = (double) hp / maxHp; // preserve current HP %
        maxHp      = (int)(maxHp      * multiplier);
        maxMana    = (int)(maxMana    * multiplier);
        baseDamage = (int)(baseDamage * multiplier);
        speed      = (int)(speed      * multiplier);
        hp         = (int)(maxHp * hpRatio); // restore proportional HP
        mana       = Math.min(mana, maxMana);
        System.out.println("\n[STAT BOOST x" + multiplier + " — " + reason + "]");
        System.out.println(name + " grows stronger! " + this);
    }

    public boolean isAlive() { return hp > 0; }

    public String getName()      { return name; }
    public int getHp()           { return hp; }
    public int getMaxHp()        { return maxHp; }
    public int getMana()         { return mana; }
    public int getMaxMana()      { return maxMana; }
    public int getSpeed()        { return speed; }
    public int getBaseDamage()   { return baseDamage; }
    public List<Item> getInventory()             { return inventory; }
    public List<StatusEffect> getActiveEffects() { return activeEffects; }

    public void setHp(int hp)     { this.hp   = Math.min(Math.max(hp, 0), maxHp); }
    public void setMana(int mana) { this.mana  = Math.min(Math.max(mana, 0), maxMana); }

    @Override
    public String toString() {
        return name + " | HP: " + hp + "/" + maxHp + " | Mana: " + mana + "/" + maxMana
                + " | DMG: " + baseDamage + " | SPD: " + speed;
    }
}
