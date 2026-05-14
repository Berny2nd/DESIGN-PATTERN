import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("   Welcome to the RPG Game!");
        System.out.println("=================================");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            name = "Hero";
        }

        System.out.println("\nHello, " + name + "! Choose your role:");
        System.out.println("1. Knight  (HP: 250 | Mana:  50 | Speed: 3)");
        System.out.println("2. Mage    (HP: 150 | Mana: 200 | Speed: 5)");
        System.out.println("3. Archer  (HP: 180 | Mana:  80 | Speed: 8)");

        int roleChoice = 0;
        while (roleChoice < 1 || roleChoice > 3) {
            System.out.print("Enter 1, 2, or 3: ");
            try {
                roleChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }

        Character player;
        switch (roleChoice) {
            case 1 -> player = new Knight(name);
            case 2 -> {
                System.out.println("\nChoose your elemental affinity:");
                System.out.println("1. Fire  2. Ice  3. Lightning");
                int elem = 0;
                while (elem < 1 || elem > 3) {
                    System.out.print("Enter 1, 2, or 3: ");
                    try {
                        elem = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a number.");
                    }
                }
                Mage.Element affinity = switch (elem) {
                    case 1 -> Mage.Element.FIRE;
                    case 2 -> Mage.Element.ICE;
                    default -> Mage.Element.LIGHTNING;
                };
                player = new Mage(name, affinity);
            }
            default -> player = new Archer(name);
        }

        PlayerRepository repo = new PlayerRepository();
        repo.savePlayer(name, player.getClass().getSimpleName().toUpperCase());

        System.out.println("\nYou have chosen: " + player.getClass().getSimpleName());
        System.out.println(player);
        System.out.println("\nPrepare for battle!\n");

        Game game = new Game(player, scanner);
        game.start();

        scanner.close();
    }
}
