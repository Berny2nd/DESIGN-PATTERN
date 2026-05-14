import java.util.Scanner;

public class Game {

    private Character player;
    private Scanner scanner;

    public Game(Character player, Scanner scanner) {
        this.player = player;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("=============================");
        System.out.println("       Game Start!");
        System.out.println("=============================");

        for (int stageNumber = 1; stageNumber <= 3; stageNumber++) {
            Stage stage = new Stage(stageNumber);
            System.out.println("\n===== " + stage + " =====");

            CombatManager combat = new CombatManager(player, stage.getEnemies(), scanner);
            boolean survived = combat.runCombat();

            if (!survived) {
                gameOver();
                return;
            }

            if (stageNumber < 3) {
                fullRestoreBetweenStages();
                System.out.println("\nPrepare for Stage " + (stageNumber + 1) + "...");
                pause();
            }
        }

        victory();
    }

    /** Fully restores HP and Mana between stages. */
    private void fullRestoreBetweenStages() {
        player.setHp(player.getMaxHp());
        player.setMana(player.getMaxMana());
        System.out.println("\nStage cleared! " + player.getName()
                + " has been fully restored to max HP and Mana.");
        System.out.println(player);
    }

    private void gameOver() {
        System.out.println("\n=============================");
        System.out.println("         GAME OVER");
        System.out.println("=============================");
        System.out.println(player.getName() + " has fallen in battle.");
        System.out.print("\nPlay again? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("y")) {
            System.out.println("Restarting... please run the program again.");
        } else {
            System.out.println("Thanks for playing!");
        }
    }

    private void victory() {
        System.out.println("\n=============================");
        System.out.println("         VICTORY!");
        System.out.println("=============================");
        System.out.println(player.getName() + " has defeated all enemies!");
        System.out.println("Final stats: " + player);
        System.out.println("Thanks for playing!");
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
