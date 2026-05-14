import java.util.ArrayList;
import java.util.List;

public class Stage {

    private final int stageNumber;
    private final List<Enemy> enemies;

    public Stage(int stageNumber) {
        this.stageNumber = stageNumber;
        this.enemies = new ArrayList<>();
        buildEnemies();
    }

    private void buildEnemies() {
        switch (stageNumber) {
            case 1 -> {
                // Stage 1: 3 Mobs
                enemies.add(new Mob());
                enemies.add(new Mob());
                enemies.add(new Mob());
            }
            case 2 -> {
                // Stage 2: 2 Mobs + 1 Sub-Boss
                enemies.add(new Mob());
                enemies.add(new Mob());
                enemies.add(new SubBoss());
            }
            case 3 -> {
                // Stage 3: 2 Mobs + 1 Sub-Boss + 1 Final Boss
                enemies.add(new Mob());
                enemies.add(new Mob());
                enemies.add(new SubBoss());
                enemies.add(new FinalBoss());
            }
            default -> System.out.println("Unknown stage: " + stageNumber);
        }
    }

    public boolean isCleared() {
        for (Enemy e : enemies) {
            if (e.isAlive()) return false;
        }
        return true;
    }

    public List<Enemy> getEnemies()  { return enemies; }
    public int getStageNumber()      { return stageNumber; }

    @Override
    public String toString() {
        return "Stage " + stageNumber + " (" + enemies.size() + " enemies)";
    }
}
