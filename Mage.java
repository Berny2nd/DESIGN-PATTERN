import java.util.List;

/**
 * Mage role — refactored with STRATEGY PATTERN.
 *
 * Elemental affinity is passed into MageAbilityStrategy at construction,
 * then the strategy is injected into Character. takeDamage() delegates
 * Mana Shield absorption to the strategy so that logic stays isolated
 * and independently testable.
 */
public class Mage extends Character {

    private final MageAbilityStrategy mageStrategy;

    public Mage(String name, MageAbilityStrategy.Element affinity) {
        this(name, new MageAbilityStrategy(affinity));
    }

    /**
     * Constructor used by CharacterFactory and unit tests.
     */
    public Mage(String name, MageAbilityStrategy strategy) {
        super(name, 150, 200, 5, 15, strategy);
        this.mageStrategy = strategy;
        inventory.add(new Item(Item.Type.HEALTH_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
        inventory.add(new Item(Item.Type.MANA_POTION));
    }

    /**
     * Mana Shield absorption delegated entirely to MageAbilityStrategy.
     * This means the absorption logic is testable without constructing
     * a Mage at all — just instantiate MageAbilityStrategy directly.
     */
    @Override
    public void takeDamage(int amount) {
        int remaining = mageStrategy.absorbWithManaShield(this, amount);
        if (remaining > 0) super.takeDamage(remaining);
    }

    public MageAbilityStrategy getMageStrategy() { return mageStrategy; }
}
