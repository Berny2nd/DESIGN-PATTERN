import java.util.List;

public class Archer extends Character {

    private boolean eagleEyeReady = false;
    private boolean silentTakedownUsed = false;
    private int poisonStacks = 0;

    public Archer(String name) {
        super(name, 180, 80, 8, 20);
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
    }

    @Override
    public void useAbility(int abilityIndex, List<Enemy> enemies, List<Character> allies) {
        switch (abilityIndex) {
            case 1 -> poisonArrow(enemies);
            case 2 -> areaVolley(enemies);
            case 3 -> eagleEye();
            case 4 -> silentTakedown(enemies);
            default -> System.out.println("Invalid ability.");
        }
    }

    @Override
    public void attack(Enemy target) {
        if (Math.random() < 0.20) {
            System.out.println(name + " misses the shot!");
            return;
        }
        boolean crit = eagleEyeReady && Math.random() < 0.30;
        int damage = crit ? (int)(baseDamage * 1.5) : baseDamage;
        System.out.println(name + " shoots " + target.getName() + " for " + damage
                + (crit ? " (CRITICAL HIT!)" : "") + " damage!");
        target.takeDamage(damage);
        eagleEyeReady = false;
    }

    @Override
    public void takeDamage(int amount) {
        if (Math.random() < 0.20) {
            System.out.println(name + " evades the attack!");
            return;
        }
        super.takeDamage(amount);
    }

    public void poisonArrow(List<Enemy> enemies) {
        Enemy target = pickFirstAlive(enemies);
        if (target == null) return;
        int stacks = Math.min(poisonStacks + 1, 5);
        poisonStacks = stacks;
        target.takeDamage(15);
        target.applyStatusEffect(new StatusEffect(StatusEffect.Type.POISONED, 3, 8 * stacks));
        System.out.println(name + " fires a Poison Arrow at " + target.getName()
                + "! Poison stacks: " + stacks);
    }

    public void areaVolley(List<Enemy> enemies) {
        if (!spendMana(20)) return;
        System.out.println(name + " fires an Area Volley!");
        int volleyDamage = (int)(baseDamage * 0.70);
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.takeDamage(volleyDamage);
            }
        }
    }

    public void eagleEye() {
        eagleEyeReady = true;
        System.out.println(name + " takes aim with Eagle Eye. Next attack: +25% accuracy, 30% crit chance.");
    }

    public void silentTakedown(List<Enemy> enemies) {
        if (silentTakedownUsed) {
            System.out.println("Silent Takedown has already been used this combat.");
            return;
        }
        if (!spendMana(30)) return;
        Enemy target = pickFirstAlive(enemies);
        if (target == null) return;
        int damage = baseDamage * 3;
        System.out.println(name + " uses Silent Takedown on " + target.getName()
                + " for " + damage + " damage! (300% crit)");
        target.takeDamage(damage);
        silentTakedownUsed = true;
    }

    public void resetCombat() {
        silentTakedownUsed = false;
        eagleEyeReady = false;
        poisonStacks = 0;
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
}
