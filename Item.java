public class Item {

    public enum Type {
        HEALTH_POTION, MANA_POTION
    }

    private Type type;
    private int healAmount;

    public Item(Type type) {
        this.type = type;
        if (type == Type.HEALTH_POTION) {
            this.healAmount = 60;
        } else {
            this.healAmount = 50;
        }
    }

    public void use(Character target) {
        if (type == Type.HEALTH_POTION) {
            int newHp = Math.min(target.getHp() + healAmount, target.getMaxHp());
            target.setHp(newHp);
            System.out.println(target.getName() + " used a Health Potion and restored "
                    + healAmount + " HP.");
        } else {
            int newMana = Math.min(target.getMana() + healAmount, target.getMaxMana());
            target.setMana(newMana);
            System.out.println(target.getName() + " used a Mana Potion and restored "
                    + healAmount + " Mana.");
        }
    }

    public Type getType() { return type; }
    public int getHealAmount() { return healAmount; }

    @Override
    public String toString() {
        return type == Type.HEALTH_POTION ? "Health Potion" : "Mana Potion";
    }
}
