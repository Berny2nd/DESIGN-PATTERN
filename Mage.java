import java.util.List;

public class Mage extends Character {

    public enum Element { FIRE, ICE, LIGHTNING }

    private Element affinity;
    private boolean manaShieldActive = false;

    public Mage(String name, Element affinity) {
        super(name, 150, 200, 5, 15);
        this.affinity = affinity;
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
    }

    @Override
    public void useAbility(int abilityIndex, List<Enemy> enemies, List<Character> allies) {
        switch (abilityIndex) {
            case 1 -> fireball(enemies);
            case 2 -> iceLance(enemies);
            case 3 -> lightningStrike(enemies);
            case 4 -> manaShield();
            default -> System.out.println("Invalid ability.");
        }
    }

    public void fireball(List<Enemy> enemies) {
        if (!spendMana(30)) return;
        if (hasEffect(StatusEffect.Type.SILENCED)) {
            System.out.println(name + " is silenced and cannot cast spells!");
            return;
        }
        Enemy target = pickFirstAlive(enemies);
        if (target == null) return;
        int damage = applyAffinity(45, Element.FIRE);
        System.out.println(name + " casts Fireball on " + target.getName() + " for " + damage + " damage!");
        target.takeDamage(damage);
        target.applyStatusEffect(new StatusEffect(StatusEffect.Type.BURNT, 3, 10));
    }

    public void iceLance(List<Enemy> enemies) {
        if (!spendMana(25)) return;
        if (hasEffect(StatusEffect.Type.SILENCED)) {
            System.out.println(name + " is silenced and cannot cast spells!");
            return;
        }
        Enemy target = pickFirstAlive(enemies);
        if (target == null) return;
        int damage = applyAffinity(35, Element.ICE);
        System.out.println(name + " casts Ice Lance on " + target.getName() + " for " + damage + " damage!");
        target.takeDamage(damage);
        target.applyStatusEffect(new StatusEffect(StatusEffect.Type.STUNNED, 1, 0));
    }

    public void lightningStrike(List<Enemy> enemies) {
        if (!spendMana(20)) return;
        if (hasEffect(StatusEffect.Type.SILENCED)) {
            System.out.println(name + " is silenced and cannot cast spells!");
            return;
        }
        System.out.println(name + " casts Lightning Strike on all enemies!");
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                int damage = applyAffinity(20, Element.LIGHTNING);
                e.takeDamage(damage);
                e.applyStatusEffect(new StatusEffect(StatusEffect.Type.SHOCKED, 2, 0));
            }
        }
    }

    public void manaShield() {
        manaShieldActive = !manaShieldActive;
        System.out.println(name + " Mana Shield " + (manaShieldActive ? "activated." : "deactivated."));
    }

    @Override
    public void takeDamage(int amount) {
        if (manaShieldActive && mana > 0) {
            int manaAbsorb = amount / 3;
            int absorbed = Math.min(manaAbsorb, mana);
            mana -= absorbed;
            int remaining = amount - (absorbed * 3);
            System.out.println(name + "'s Mana Shield absorbs damage. Mana: " + mana);
            if (remaining > 0) super.takeDamage(remaining);
        } else {
            super.takeDamage(amount);
        }
    }

    private int applyAffinity(int base, Element spellElement) {
        if (spellElement == affinity) return (int)(base * 1.15);
        return base;
    }

    private boolean spendMana(int cost) {
        if (mana < cost) {
            System.out.println(name + " does not have enough mana! (" + mana + "/" + cost + ")");
            return false;
        }
        mana -= cost;
        return true;
    }

    private Enemy pickFirstAlive(List<Enemy> enemies) {
        for (Enemy e : enemies) {
            if (e.isAlive()) return e;
        }
        return null;
    }

    public Element getAffinity() { return affinity; }
    public boolean isManaShieldActive() { return manaShieldActive; }
}
