import java.util.List;

public class FinalBoss extends Enemy {

    private int targetIndex = 0;

    public FinalBoss() {
        super("Final Boss", 300, 40, 55, 7);
    }

    @Override
    public Character chooseTarget(List<Character> players) {
        List<Character> alive = players.stream().filter(Character::isAlive).toList();
        if (alive.isEmpty()) return null;
        Character target = alive.get(targetIndex % alive.size());
        targetIndex++;
        return target;
    }

    @Override public double getKillMultiplier() { return 1.7; }
    @Override public String getKillLabel()      { return "Final Boss Kill"; }
}
