import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * CombatManager — refactored with STRATEGY PATTERN.
 *
 * Key change: showAbilityMenu() no longer contains
 *
 *   if (player instanceof Knight) { ... }
 *   else if (player instanceof Mage) { ... }
 *   else if (player instanceof Archer) { ... }
 *
 * Instead it asks the player's AbilityStrategy for its menu lines,
 * then delegates execution back through player.useAbility().
 * CombatManager has zero knowledge of Knight, Mage, or Archer.
 *
 * Testability benefit:
 *   - CombatManager can be unit-tested with any Character that holds
 *     a mock/stub AbilityStrategy — no real role class needed.
 *   - Adding a new role (e.g. Cleric) requires zero changes here.
 */
public class CombatManager {

    private final Character   player;
    private final List<Enemy> enemies;
    private final Scanner     scanner;

    private final Set<Enemy> boostedKills = new HashSet<>();

    public CombatManager(Character player, List<Enemy> enemies, Scanner scanner) {
        this.player  = player;
        this.enemies = enemies;
        this.scanner = scanner;
    }

    // ── Main combat loop ───────────────────────────────────────────

    public boolean runCombat() {
        System.out.println("\n--- Combat Start ---");
        printCombatants();

        while (player.isAlive() && hasAliveEnemies()) {
            playerTurn();
            checkAndApplyKillBoosts();
            if (!hasAliveEnemies()) break;
            enemyTurn();
            tickAllEffects();
            printStatus();
        }

        if (!player.isAlive()) {
            System.out.println("\n" + player.getName() + " has been defeated...");
            return false;
        }
        System.out.println("\nAll enemies defeated!");
        return true;
    }

    // ── Kill boost ─────────────────────────────────────────────────

    private void checkAndApplyKillBoosts() {
        for (Enemy e : enemies) {
            if (!e.isAlive() && !boostedKills.contains(e)) {
                boostedKills.add(e);
                player.applyStatBoost(e.getKillMultiplier(), e.getKillLabel());
            }
        }
    }

    // ── Player turn ────────────────────────────────────────────────

    private void playerTurn() {
        for (StatusEffect e : player.getActiveEffects()) {
            if (e.preventsAction()) {
                System.out.println(player.getName() + " is stunned and skips their turn!");
                return;
            }
        }
        for (StatusEffect e : player.getActiveEffects()) {
            if (e.causesShock() && Math.random() < 0.30) {
                System.out.println(player.getName() + " is shocked and cannot act!");
                return;
            }
        }

        System.out.println("\n--- Your Turn ---");
        System.out.println(player);
        printInventory();
        showActionMenu();

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                Enemy target = pickEnemyTarget();
                if (target != null) player.attack(target);
            }
            case 2 -> showAbilityMenu();   // ← no instanceof here
            case 3 -> showItemMenu();
            case 4 -> player.defend();
            default -> System.out.println("Invalid choice. Turn skipped.");
        }
    }

    private void showActionMenu() {
        System.out.println("1. Basic Attack");
        System.out.println("2. Use Ability");
        System.out.println("3. Use Item"
                + (player.getInventory().isEmpty() ? " (empty)" : ""));
        System.out.println("4. Defend (halve incoming damage)");
        System.out.print("Choose action: ");
    }

    /**
     * STRATEGY PATTERN in action:
     * Asks the strategy for display lines, then delegates execution.
     * No knowledge of Knight / Mage / Archer required.
     */
    private void showAbilityMenu() {
        AbilityStrategy strategy = player.getAbilityStrategy();
        for (String line : strategy.getAbilityMenuLines()) {
            System.out.println(line);
        }
        System.out.print("Choose ability: ");
        int abilityChoice = readInt();
        player.useAbility(abilityChoice, enemies, List.of(player));
    }

    private void showItemMenu() {
        List<Item> inventory = player.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("No items in inventory.");
            return;
        }
        for (int i = 0; i < inventory.size(); i++) {
            System.out.println((i + 1) + ". " + inventory.get(i));
        }
        System.out.print("Choose item (0 to cancel): ");
        int itemChoice = readInt() - 1;
        if (itemChoice >= 0 && itemChoice < inventory.size()) {
            player.useItem(itemChoice);
        }
    }

    // ── Enemy turn ─────────────────────────────────────────────────

    private void enemyTurn() {
        System.out.println("\n--- Enemy Turn ---");
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            if (enemy.hasStatusEffect(StatusEffect.Type.STUNNED)) {
                System.out.println(enemy.getName()
                        + " is stunned and skips their turn!");
                continue;
            }
            if (enemy.hasStatusEffect(StatusEffect.Type.SHOCKED)
                    && Math.random() < 0.30) {
                System.out.println(enemy.getName()
                        + " is shocked and cannot act!");
                continue;
            }

            Character target;
            if (enemy.getTauntTarget() != null
                    && enemy.getTauntTarget().isAlive()) {
                target = enemy.getTauntTarget();
            } else {
                enemy.clearTaunt();
                target = enemy.chooseTarget(List.of(player));
            }

            if (target != null && target.isAlive()) {
                enemy.attack(target);
            }
        }
    }

    // ── Tick effects ───────────────────────────────────────────────

    private void tickAllEffects() {
        player.tickEffects();
        for (Enemy e : enemies) {
            if (e.isAlive()) e.tickEffects();
        }
    }

    // ── Helpers ────────────────────────────────────────────────────

    private Enemy pickEnemyTarget() {
        List<Enemy> alive = enemies.stream().filter(Enemy::isAlive).toList();
        if (alive.isEmpty()) return null;
        if (alive.size() == 1) return alive.get(0);

        System.out.println("Choose target:");
        for (int i = 0; i < alive.size(); i++) {
            System.out.println((i + 1) + ". " + alive.get(i).getName()
                    + " (HP: " + alive.get(i).getHp()
                    + "/" + alive.get(i).getMaxHp() + ")");
        }
        System.out.print("Target: ");
        int t = readInt() - 1;
        if (t >= 0 && t < alive.size()) return alive.get(t);
        return alive.get(0);
    }

    private boolean hasAliveEnemies() {
        return enemies.stream().anyMatch(Enemy::isAlive);
    }

    private void printCombatants() {
        System.out.println("Player: " + player);
        System.out.println("Enemies:");
        for (Enemy e : enemies) {
            System.out.println("  - " + e.getName()
                    + " (HP: " + e.getHp() + "/" + e.getMaxHp() + ")");
        }
    }

    private void printStatus() {
        System.out.println("\n[Status] " + player);
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                System.out.println("[Status] " + e.getName()
                        + " HP: " + e.getHp() + "/" + e.getMaxHp());
            }
        }
    }

    private void printInventory() {
        List<Item> inv = player.getInventory();
        if (!inv.isEmpty()) {
            System.out.print("Inventory: ");
            for (Item item : inv) System.out.print("[" + item + "] ");
            System.out.println();
        }
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
