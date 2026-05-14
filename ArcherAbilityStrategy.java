import java.util.List;

public class ArcherAbilityStrategy implements AbilityStrategy {

    private boolean eagleEyeReady      = false;
    private boolean silentTakedownUsed = false;
    private int     poisonStacks       = 0;

    // ── AbilityStrategy ────────────────────────────────────────────

    @Override
    public List<String> getAbilityMenuLines() {
        return List.of(
            "1. Poison Arrow    (15 dmg + poison stack, stacks up to 5)",
            "2. Area Volley     (AoE 70% dmg to all enemies, 20 mana)",
            "3. Eagle Eye       (next attack: +accuracy, +30% crit chance)",
            "4. Silent Takedown (300% crit dmg, 30 mana, once per combat)"
        );
    }

    @Override
    public void execute(int abilityIndex, Character user,
                        List<Enemy> enemies, List<Character> allies) {
        switch (abilityIndex) {
            case 1 -> poisonArrow(user, enemies);
            case 2 -> areaVolley(user, enemies);
            case 3 -> eagleEye(user);
            case 4 -> silentTakedown(user, enemies);
            default -> System.out.println("Invalid ability choice.");
        }
    }

    @Override
    public void resetCombat() {
        eagleEyeReady      = false;
        silentTakedownUsed = false;
        poisonStacks       = 0;
    }

    // ── Ability implementations ────────────────────────────────────

    public void poisonArrow(Character user, List<Enemy> enemies) {
        Enemy target = firstAlive(enemies);
        if (target == null) return;
        poisonStacks = Math.min(poisonStacks + 1, 5);
        target.takeDamage(15);
        target.applyStatusEffect(
                new StatusEffect(StatusEffect.Type.POISONED, 3, 8 * poisonStacks));
        System.out.println(user.getName() + " fires a Poison Arrow at "
                + target.getName() + "! Poison stacks: " + poisonStacks);
    }

    public void areaVolley(Character user, List<Enemy> enemies) {
        if (!spendMana(user, 20)) return;
        System.out.println(user.getName() + " fires an Area Volley!");
        int damage = (int)(user.getBaseDamage() * 0.70);
        for (Enemy e : enemies) {
            if (e.isAlive()) e.takeDamage(damage);
        }
    }

    public void eagleEye(Character user) {
        eagleEyeReady = true;
        System.out.println(user.getName()
                + " takes aim with Eagle Eye. Next attack: +25% accuracy, 30% crit chance.");
    }

    public void silentTakedown(Character user, List<Enemy> enemies) {
        if (silentTakedownUsed) {
            System.out.println("Silent Takedown has already been used this combat.");
            return;
        }
        if (!spendMana(user, 30)) return;
        Enemy target = firstAlive(enemies);
        if (target == null) return;
        int damage = user.getBaseDamage() * 3;
        System.out.println(user.getName() + " uses Silent Takedown on "
                + target.getName() + " for " + damage + " damage! (300% crit)");
        target.takeDamage(damage);
        silentTakedownUsed = true;
    }

    /**
     * Resolves an Archer basic attack with Eagle Eye and evasion logic.
     * Separated from execute() so CombatManager can call it on basic attack,
     * and unit tests can verify crit/evasion behaviour independently.
     */
    public void resolveAttack(Character user, Enemy target) {
        if (Math.random() < 0.20) {
            System.out.println(user.getName() + " misses the shot!");
            return;
        }
        boolean crit = eagleEyeReady && Math.random() < 0.30;
        int damage   = crit ? (int)(user.getBaseDamage() * 1.5) : user.getBaseDamage();
        System.out.println(user.getName() + " shoots " + target.getName()
                + " for " + damage + (crit ? " (CRITICAL HIT!)" : "") + " damage!");
        target.takeDamage(damage);
        eagleEyeReady = false;
    }

    /**
     * Evasion check — called from Archer.takeDamage().
     * Returns true if the hit was evaded (caller should skip damage).
     */
    public boolean tryEvade(Character user) {
        if (Math.random() < 0.20) {
            System.out.println(user.getName() + " evades the attack!");
            return true;
        }
        return false;
    }

    // ── Private helpers ────────────────────────────────────────────

    private boolean spendMana(Character user, int cost) {
        if (user.getMana() < cost) {
            System.out.println(user.getName() + " does not have enough mana! ("
                    + user.getMana() + "/" + cost + ")");
            return false;
        }
        user.setMana(user.getMana() - cost);
        return true;
    }

    private Enemy firstAlive(List<Enemy> enemies) {
        for (Enemy e : enemies) if (e.isAlive()) return e;
        return null;
    }

    // ── State accessors (useful for unit tests) ────────────────────

    public boolean isEagleEyeReady()      { return eagleEyeReady;      }
    public boolean isSilentTakedownUsed() { return silentTakedownUsed; }
    public int     getPoisonStacks()      { return poisonStacks;        }

    /** Package-visible setters so tests can pre-configure state. */
    void setEagleEyeReady(boolean ready)       { this.eagleEyeReady      = ready; }
    void setSilentTakedownUsed(boolean used)   { this.silentTakedownUsed = used;  }
    void setPoisonStacks(int stacks)           { this.poisonStacks        = stacks; }
}
