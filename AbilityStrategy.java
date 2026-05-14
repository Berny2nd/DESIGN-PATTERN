import java.util.List;

/**
 * STRATEGY PATTERN — defines the contract for all role ability sets.
 *
 * Each concrete strategy (KnightAbilityStrategy, MageAbilityStrategy,
 * ArcherAbilityStrategy) encapsulates one role's full ability behaviour.
 * CombatManager depends only on this interface, never on concrete role classes.
 *
 * Testability benefit:
 *   - Each strategy is tested in isolation with no CombatManager involvement.
 *   - CombatManager can be tested with a mock/stub AbilityStrategy.
 *   - Adding a new role requires zero changes to CombatManager.
 */
public interface AbilityStrategy {

    /**
     * Returns the human-readable ability menu lines shown to the player.
     * Keeping display logic inside the strategy means CombatManager needs
     * no knowledge of which role is active.
     */
    List<String> getAbilityMenuLines();

    /**
     * Executes the ability at the given 1-based index.
     *
     * @param abilityIndex 1–4 chosen by the player
     * @param user         the Character executing the ability
     * @param enemies      live enemy list
     * @param allies       live ally list (may be solo)
     */
    void execute(int abilityIndex, Character user,
                 List<Enemy> enemies, List<Character> allies);

    /**
     * Resets any per-combat state (cooldowns, one-use flags, stacks).
     * Called at the start of every new combat encounter.
     */
    void resetCombat();
}
