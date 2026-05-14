import java.util.List;

public class SubBoss extends Enemy {

    public SubBoss() {
        super("Sub-Boss", 150, 25, 35, 6);
    }

    @Override
    public Character chooseTarget(List<Character> players) {
        Character lowestHp = null;
        for (Character p : players) {
            if (p.isAlive()) {
                if (lowestHp == null || p.getHp() < lowestHp.getHp()) lowestHp = p;
            }
        }
        return lowestHp;
    }

    @Override public double getKillMultiplier() { return 1.5; }
    @Override public String getKillLabel()      { return "Sub-Boss Kill"; }
}
