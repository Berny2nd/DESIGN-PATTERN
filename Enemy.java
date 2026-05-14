import java.util.ArrayList;
import java.util.List;

public abstract class Enemy {

    protected String name;
    protected int hp;
    protected int maxHp;
    protected int minDamage;
    protected int maxDamage;
    protected int speed;
    protected List<StatusEffect> activeEffects = new ArrayList<>();
    protected Character tauntTarget = null;

    public Enemy(String name, int hp, int minDamage, int maxDamage, int speed) {
        this.name      = name;
        this.hp        = hp;
        this.maxHp     = hp;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.speed     = speed;
    }

    public abstract Character chooseTarget(List<Character> players);

    /** Returns the stat-boost multiplier the player earns for killing this enemy. */
    public abstract double getKillMultiplier();

    /** Returns a label used in the boost message, e.g. "Mob Kill", "Sub-Boss Kill". */
    public abstract String getKillLabel();

    public void attack(Character target) {
        int damage = minDamage + (int)(Math.random() * (maxDamage - minDamage + 1));
        System.out.println(name + " attacks " + target.getName() + " for " + damage + " damage!");
        target.takeDamage(damage);
    }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
        System.out.println(name + " takes " + amount + " damage. HP: " + hp + "/" + maxHp);
    }

    public void applyStatusEffect(StatusEffect effect) {
        activeEffects.add(effect);
        System.out.println(name + " is affected by " + effect);
    }

    public void applyTaunt(Character knight) {
        this.tauntTarget = knight;
        System.out.println(name + " is taunted — must attack " + knight.getName() + " for 2 turns.");
    }

    public boolean hasStatusEffect(StatusEffect.Type type) {
        for (StatusEffect e : activeEffects) {
            if (e.getType() == type) return true;
        }
        return false;
    }

    public void tickEffects() {
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect effect : activeEffects) {
            int dot = effect.getTickDamage();
            if (dot > 0) {
                hp = Math.max(0, hp - dot);
                System.out.println(name + " takes " + dot
                        + " damage from " + effect.getType().name().toLowerCase() + ".");
            }
            effect.tick();
            if (effect.isExpired()) expired.add(effect);
        }
        activeEffects.removeAll(expired);
    }

    public Character getTauntTarget() { return tauntTarget; }
    public void clearTaunt()          { this.tauntTarget = null; }

    public boolean isAlive() { return hp > 0; }

    public String getName()  { return name; }
    public int getHp()       { return hp; }
    public int getMaxHp()    { return maxHp; }
    public int getSpeed()    { return speed; }
}
