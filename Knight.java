import java.util.List;

public class Knight extends Character {

    private boolean layOnHandsUsed = false;
    private boolean shieldWallActive = false;

    public Knight(String name) {
        super(name, 250, 50, 3, 25);
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
    }

    @Override
    public void useAbility(int abilityIndex, List<Enemy> enemies, List<Character> allies) {
        switch (abilityIndex) {
            case 1 -> taunt(enemies);
            case 2 -> shieldWall();
            case 3 -> layOnHands(allies);
            case 4 -> whirlwind(enemies);
            default -> System.out.println("Invalid ability.");
        }
    }

    // ── Abilities ──────────────────────────────────────────────────

    public void taunt(List<Enemy> enemies) {
        System.out.println(name + " uses Taunt! Enemies are forced to target " + name + " for 2 turns.");
        for (Enemy e : enemies) {
            if (e.isAlive()) e.applyTaunt(this);
        }
    }

    public void shieldWall() {
        shieldWallActive = true;
        System.out.println(name + " raises Shield Wall! Block chance → 90% this turn. No attack.");
    }

    public void layOnHands(List<Character> allies) {
        if (layOnHandsUsed) {
            System.out.println("Lay on Hands has already been used this game.");
            return;
        }
        Character lowestHp = null;
        for (Character ally : allies) {
            if (ally.isAlive()) {
                if (lowestHp == null || ally.getHp() < lowestHp.getHp()) lowestHp = ally;
            }
        }
        if (lowestHp != null) {
            int healAmount = (int)(lowestHp.getMaxHp() * 0.30);
            lowestHp.restoreHp(healAmount);
            System.out.println(name + " uses Lay on Hands on " + lowestHp.getName()
                    + ", restoring " + healAmount + " HP.");
            layOnHandsUsed = true;
        }
    }

    /**
     * Whirlwind – AoE melee attack hitting all living enemies for 80 % of base damage.
     * Costs 20 mana.
     */
    public void whirlwind(List<Enemy> enemies) {
        if (mana < 20) {
            System.out.println(name + " does not have enough mana to use Whirlwind! ("
                    + mana + "/20)");
            return;
        }
        mana -= 20;
        int damage = (int)(baseDamage * 0.80);
        System.out.println(name + " unleashes Whirlwind! (" + damage + " dmg to all enemies)");
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                System.out.println("  ↳ hitting " + e.getName() + "...");
                e.takeDamage(damage);
            }
        }
    }

    @Override
    public void takeDamage(int amount) {
        double blockChance = shieldWallActive ? 0.90 : 0.30;
        if (Math.random() < blockChance) {
            int retaliateDamage = (int)(baseDamage * 0.50);
            System.out.println(name + " blocks the attack! Retaliates for " + retaliateDamage + " damage.");
            shieldWallActive = false;
        } else {
            shieldWallActive = false;
            super.takeDamage(amount);
        }
    }

    public boolean isShieldWallActive() { return shieldWallActive; }
}
