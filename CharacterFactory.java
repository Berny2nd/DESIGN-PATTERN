public class CharacterFactory {

    public enum Role { KNIGHT, MAGE, ARCHER }

    // ── Primary factory method ─────────────────────────────────────

    public Character create(Role role, String name) {
        return switch (role) {
            case KNIGHT -> createKnight(name);
            case MAGE   -> createMage(name, MageAbilityStrategy.Element.FIRE);
            case ARCHER -> createArcher(name);
        };
    }

    // ── Individual factory methods (overridable in tests) ──────────

    public Knight createKnight(String name) {
        return new Knight(name, new KnightAbilityStrategy());
    }


    public Mage createMage(String name, MageAbilityStrategy.Element affinity) {
        return new Mage(name, new MageAbilityStrategy(affinity));
    }

    public Archer createArcher(String name) {
        return new Archer(name, new ArcherAbilityStrategy());
    }
}
