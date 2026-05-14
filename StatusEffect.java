public class StatusEffect {

    public enum Type {
        /**
         * TAUNT    – handled externally in Enemy; forces enemy to attack the taunting Knight.
         * STUNNED  – unit cannot act for the duration (checked in CombatManager / Enemy).
         * BURNT    – deals fire damage each tick (value = damage per tick).
         * SHOCKED  – 30 % chance to skip action each turn (checked in CombatManager / Enemy).
         * SILENCED – prevents casting spells (checked inside Mage spell methods).
         * POISONED – deals poison damage each tick (value = damage per tick).
         */
        TAUNT, STUNNED, BURNT, SHOCKED, SILENCED, POISONED
    }

    private final Type type;
    private int duration;   // turns remaining
    private final int value;  // damage per tick (used by BURNT and POISONED)

    public StatusEffect(Type type, int duration, int value) {
        this.type = type;
        this.duration = duration;
        this.value = value;
    }

    // ---------------------------------------------------------------
    // Core behaviour helpers
    // ---------------------------------------------------------------

    /** Per-tick damage for DoT effects; 0 for non-DoT. */
    public int getTickDamage() {
        return (type == Type.BURNT || type == Type.POISONED) ? value : 0;
    }

    /** STUNNED always prevents action. */
    public boolean preventsAction() {
        return type == Type.STUNNED;
    }

    /** SILENCED blocks spell casting. */
    public boolean blocksCasting() {
        return type == Type.SILENCED;
    }

    /** SHOCKED has a 30 % chance to prevent action (caller rolls the dice). */
    public boolean causesShock() {
        return type == Type.SHOCKED;
    }

    // ---------------------------------------------------------------
    // Tick / expiry
    // ---------------------------------------------------------------

    public void tick() {
        if (duration > 0) duration--;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    // ---------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------

    public Type getType()     { return type; }
    public int  getDuration() { return duration; }
    public int  getValue()    { return value; }

    @Override
    public String toString() {
        if (type == Type.BURNT || type == Type.POISONED) {
            return type.name() + "(" + value + " dmg/turn, " + duration + " turns left)";
        }
        return type.name() + "(" + duration + " turns left)";
    }
}
