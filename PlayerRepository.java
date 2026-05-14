import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class PlayerRepository {

    private static final String DB_URL = "jdbc:sqlite:game.db";

    public PlayerRepository() {
        initTable();
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "role TEXT NOT NULL,"
                + "created_at TEXT NOT NULL"
                + ")";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB init error: " + e.getMessage());
        }
    }

    public void savePlayer(String name, String role) {
        String sql = "INSERT INTO players (name, role, created_at) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, LocalDateTime.now().toString());
            pstmt.executeUpdate();
            System.out.println("Player saved: " + name + " (" + role + ")");
        } catch (SQLException e) {
            System.out.println("DB save error: " + e.getMessage());
        }
    }
}
