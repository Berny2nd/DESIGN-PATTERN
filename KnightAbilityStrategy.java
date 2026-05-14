import java.util.List;

/**
 * STRATEGY PATTERN — Concrete Strategy for Knight abilities.
 *
 * All Knight-specific combat logic lives here, isolated from CombatManager
 * and from other role strategies. This class can be instantiated and tested
 * completely independently: no Scanner, no Stage, no Game needed.
 */
public class KnightAbilityStrategy implements AbilityStrategy {

    private boolean layOnHandsUsed  = false;
    private boolean shieldWallActive = false;

    // ── AbilityStrategy ────────────────────────────────────────────

    @Override
    public List<String> getAbilityMenuLines() {
        return List.of(
            "1. Taunt        (force enemies to target you, 2 turns)",
            "2. Shield Wall  (block chance 90%, no attack this turn)",
            "3. Lay on Hands (heal lowest-HP ally 30% maxHP, once per game)",
            "4. Whirlwind    (AoE 80% dmg to all enemies, 20 mana)"
        );
    }

    @Override
    public void execute(int abilityIndex, Character user,
                        List<Enemy> enemies, List<Character> allies) {
        switch (abilityIndex) {
            case 1 -> taunt(user, enemies);
            case 2 -> shieldWall(user);
            case 3 -> layOnHands(user, allies);
            case 4 -> whirlwind(user, enemies);
            default -> System.out.println("Invalid ability choice.");
        }
    }

    @Override
    public void resetCombat() {
        shieldWallActive = false;
        // layOnHandsUsed is per-game, intentionally NOT reset here
    }

    // ── Ability implementations ────────────────────────────────────

    public void taunt(Character user, List<Enemy> enemies) {
        System.out.println(user.getName()
                + " uses Taunt! All enemies must target "
                + user.getName() + " for 2 turns.");
        for (Enemy e : enemies) {
            if (e.isAlive()) e.applyTaunt(user);
        }
    }

    public void shieldWall(Character user) {
        shieldWallActive = true;
        System.out.println(user.getName()
                + " raises Shield Wall! Block chance → 90% this turn.");
    }

    public void layOnHands(Character user, List<Character> allies) {
        if (layOnHandsUsed) {
            System.out.println("Lay on Hands has already been used this game.");
            return;
        }
        Character lowestHp = null;
        for (Character ally : allies) {
            if (ally.isAlive()) {
                if (lowestHp == null || ally.getHp() < lowestHp.getHp()) {
                    lowestHp = ally;
                }
            }
        }
        if (lowestHp != null) {
            int heal = (int)(lowestHp.getMaxHp() * 0.30);
            lowestHp.restoreHp(heal);
            System.out.println(user.getName() + " uses Lay on Hands on "
                    + lowestHp.getName() + ", restoring " + heal + " HP.");
            layOnHandsUsed = true;
        }
    }

    public void whirlwind(Character user, List<Enemy> enemies) {
        if (user.getMana() < 20) {
            System.out.println(user.getName()
                    + " does not have enough mana for Whirlwind! ("
                    + user.getMana() + "/20)");
            return;
        }
        user.setMana(user.getMana() - 20);
        int damage = (int)(user.getBaseDamage() * 0.80);
        System.out.println(user.getName()
                + " unleashes Whirlwind! (" + damage + " dmg to all enemies)");
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                System.out.println("  ↳ hitting " + e.getName() + "...");
                e.takeDamage(damage);
            }
        }
    }

    // ── State accessors (useful for unit tests) ────────────────────

    public boolean isShieldWallActive() { return shieldWallActive; }
    public boolean isLayOnHandsUsed()   { return layOnHandsUsed;   }

    /** Package-visible setter so tests can pre-configure state. */
    void setShieldWallActive(boolean active) { this.shieldWallActive = active; }
    void setLayOnHandsUsed(boolean used)     { this.layOnHandsUsed   = used;   }
}
