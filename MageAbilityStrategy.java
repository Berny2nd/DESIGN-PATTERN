import java.util.List;

/**
 * STRATEGY PATTERN — Concrete Strategy for Mage abilities.
 *
 * Encapsulates all Mage spell logic. The elemental affinity is injected
 * at construction time, making it easy to test each affinity path without
 * changing anything in CombatManager or Character.
 */
public class MageAbilityStrategy implements AbilityStrategy {

    public enum Element { FIRE, ICE, LIGHTNING }

    private final Element affinity;
    private boolean manaShieldActive = false;

    public MageAbilityStrategy(Element affinity) {
        this.affinity = affinity;
    }

    // ── AbilityStrategy ────────────────────────────────────────────

    @Override
    public List<String> getAbilityMenuLines() {
        return List.of(
            "1. Fireball         (45 dmg + Burnt 3 turns, 30 mana)",
            "2. Ice Lance        (35 dmg + Stun  1 turn,  25 mana)",
            "3. Lightning Strike (AoE 20 dmg + Shock 2 turns, 20 mana)",
            "4. Mana Shield      (toggle: damage hits mana first)"
        );
    }

    @Override
    public void execute(int abilityIndex, Character user,
                        List<Enemy> enemies, List<Character> allies) {
        switch (abilityIndex) {
            case 1 -> fireball(user, enemies);
            case 2 -> iceLance(user, enemies);
            case 3 -> lightningStrike(user, enemies);
            case 4 -> manaShield(user);
            default -> System.out.println("Invalid ability choice.");
        }
    }

    @Override
    public void resetCombat() {
        manaShieldActive = false;
    }

    // ── Ability implementations ────────────────────────────────────

    public void fireball(Character user, List<Enemy> enemies) {
        if (!spendMana(user, 30)) return;
        if (isSilenced(user)) return;
        Enemy target = firstAlive(enemies);
        if (target == null) return;
        int damage = applyAffinity(45, Element.FIRE);
        System.out.println(user.getName() + " casts Fireball on "
                + target.getName() + " for " + damage + " damage!");
        target.takeDamage(damage);
        target.applyStatusEffect(new StatusEffect(StatusEffect.Type.BURNT, 3, 10));
    }

    public void iceLance(Character user, List<Enemy> enemies) {
        if (!spendMana(user, 25)) return;
        if (isSilenced(user)) return;
        Enemy target = firstAlive(enemies);
        if (target == null) return;
        int damage = applyAffinity(35, Element.ICE);
        System.out.println(user.getName() + " casts Ice Lance on "
                + target.getName() + " for " + damage + " damage!");
        target.takeDamage(damage);
        target.applyStatusEffect(new StatusEffect(StatusEffect.Type.STUNNED, 1, 0));
    }

    public void lightningStrike(Character user, List<Enemy> enemies) {
        if (!spendMana(user, 20)) return;
        if (isSilenced(user)) return;
        System.out.println(user.getName() + " casts Lightning Strike on all enemies!");
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                int damage = applyAffinity(20, Element.LIGHTNING);
                e.takeDamage(damage);
                e.applyStatusEffect(new StatusEffect(StatusEffect.Type.SHOCKED, 2, 0));
            }
        }
    }

    public void manaShield(Character user) {
        manaShieldActive = !manaShieldActive;
        System.out.println(user.getName() + " Mana Shield "
                + (manaShieldActive ? "activated." : "deactivated."));
    }

    // ── Mana shield damage interception ───────────────────────────

    /**
     * Called by Mage.takeDamage() to redirect damage through mana first.
     * Exposed as a public method so it can be unit-tested directly.
     *
     * @return remaining HP damage after mana absorption
     */
    public int absorbWithManaShield(Character user, int incomingDamage) {
        if (!manaShieldActive || user.getMana() <= 0) return incomingDamage;
        int manaAbsorb = incomingDamage / 3;
        int absorbed   = Math.min(manaAbsorb, user.getMana());
        user.setMana(user.getMana() - absorbed);
        System.out.println(user.getName()
                + "'s Mana Shield absorbs damage. Mana: " + user.getMana());
        return incomingDamage - (absorbed * 3);
    }

    // ── Private helpers ────────────────────────────────────────────

    private int applyAffinity(int base, Element spellElement) {
        return (spellElement == affinity) ? (int)(base * 1.15) : base;
    }

    private boolean spendMana(Character user, int cost) {
        if (user.getMana() < cost) {
            System.out.println(user.getName() + " does not have enough mana! ("
                    + user.getMana() + "/" + cost + ")");
            return false;
        }
        user.setMana(user.getMana() - cost);
        return true;
    }

    private boolean isSilenced(Character user) {
        if (user.hasEffect(StatusEffect.Type.SILENCED)) {
            System.out.println(user.getName() + " is silenced and cannot cast spells!");
            return true;
        }
        return false;
    }

    private Enemy firstAlive(List<Enemy> enemies) {
        for (Enemy e : enemies) if (e.isAlive()) return e;
        return null;
    }

    // ── State accessors (useful for unit tests) ────────────────────

    public boolean isManaShieldActive() { return manaShieldActive; }
    public Element getAffinity()        { return affinity; }
}
