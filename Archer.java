import java.util.List;

public class Archer extends Character {

    private final ArcherAbilityStrategy archerStrategy;

    public Archer(String name) {
        this(name, new ArcherAbilityStrategy());
    }

    /**
     * Constructor used by CharacterFactory and unit tests.
     */
    public Archer(String name, ArcherAbilityStrategy strategy) {
        super(name, 180, 80, 8, 20, strategy);
        this.archerStrategy = strategy;
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
    }

    /**
     * Basic attack uses Eagle Eye crit logic from the strategy.
     */
    @Override
    public void attack(Enemy target) {
        archerStrategy.resolveAttack(this, target);
    }

    /**
     * Evasion check delegated to strategy.
     */
    @Override
    public void takeDamage(int amount) {
        if (!archerStrategy.tryEvade(this)) {
            super.takeDamage(amount);
        }
    }

    public ArcherAbilityStrategy getArcherStrategy() { return archerStrategy; }
}
