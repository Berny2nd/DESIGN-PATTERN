import java.util.List;

public class Stage {

    private final int         stageNumber;
    private final List<Enemy> enemies;

    public Stage(int stageNumber, EnemyFactory factory) {
        this.stageNumber = stageNumber;
        this.enemies     = factory.createEnemiesForStage(stageNumber);
    }

    public Stage(int stageNumber) {
        this(stageNumber, new EnemyFactory());
    }

    public boolean isCleared() {
        for (Enemy e : enemies) {
            if (e.isAlive()) return false;
        }
        return true;
    }

    public List<Enemy> getEnemies()  { return enemies; }
    public int         getStageNumber() { return stageNumber; }

    @Override
    public String toString() {
        return "Stage " + stageNumber + " (" + enemies.size() + " enemies)";
    }
}
