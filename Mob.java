import java.util.List;

public class Mob extends Enemy {

    public Mob() {
        super("Mob", 60, 10, 15, 4);
    }

    @Override
    public Character chooseTarget(List<Character> players) {
        List<Character> alive = players.stream().filter(Character::isAlive).toList();
        if (alive.isEmpty()) return null;
        return alive.get((int)(Math.random() * alive.size()));
    }

    @Override public double getKillMultiplier() { return 1.2; }
    @Override public String getKillLabel()      { return "Mob Kill"; }
}
