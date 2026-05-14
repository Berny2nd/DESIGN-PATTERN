import java.util.List;

/**
 * Knight role — refactored with STRATEGY PATTERN.
 *
 * The Knight no longer contains any ability logic. It injects a
 * KnightAbilityStrategy into the parent Character constructor.
 * takeDamage() asks the strategy for block/shield state, keeping
 * all Knight-specific decisions inside the strategy.
 */
public class Knight extends Character {

    private final KnightAbilityStrategy knightStrategy;

    public Knight(String name) {
        this(name, new KnightAbilityStrategy());
    }

    /**
     * Constructor used by CharacterFactory and unit tests.
     * Accepting the strategy as a parameter makes Knight fully testable
     * with a mock or pre-configured strategy.
     */
    public Knight(String name, KnightAbilityStrategy strategy) {
        super(name, 250, 50, 3, 25, strategy);
        this.knightStrategy = strategy;
        // Inventory setup is handled here; factory may override via addItem().
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
    }

    /**
     * Shield Wall block / retaliation logic delegated to strategy state.
     */
    @Override
    public void takeDamage(int amount) {
        double blockChance = knightStrategy.isShieldWallActive() ? 0.90 : 0.30;
        if (Math.random() < blockChance) {
            int retaliate = (int)(baseDamage * 0.50);
            System.out.println(name + " blocks the attack! Retaliates for "
                    + retaliate + " damage.");
            knightStrategy.setShieldWallActive(false);
        } else {
            knightStrategy.setShieldWallActive(false);
            super.takeDamage(amount);
        }
    }

    public KnightAbilityStrategy getKnightStrategy() { return knightStrategy; }
}
