import java.util.ArrayList;
import java.util.List;

public class EnemyFactory {

    // ── Primary factory method ─────────────────────────────────────
    public List<Enemy> createEnemiesForStage(int stageNumber) {
        List<Enemy> enemies = new ArrayList<>();
        switch (stageNumber) {
            case 1 -> {
                enemies.add(createMob());
                enemies.add(createMob());
                enemies.add(createMob());
            }
            case 2 -> {
                enemies.add(createMob());
                enemies.add(createMob());
                enemies.add(createSubBoss());
            }
            case 3 -> {
                enemies.add(createMob());
                enemies.add(createMob());
                enemies.add(createSubBoss());
                enemies.add(createFinalBoss());
            }
            default -> System.out.println("Unknown stage: " + stageNumber);
        }
        return enemies;
    }

    // ── Individual factory methods (overridable in tests) ──────────

    /** Factory method for a standard Mob. */
    public Mob createMob() {
        return new Mob();
    }

    /** Factory method for a Sub-Boss. */
    public SubBoss createSubBoss() {
        return new SubBoss();
    }

    /** Factory method for the Final Boss. */
    public FinalBoss createFinalBoss() {
        return new FinalBoss();
    }
}
