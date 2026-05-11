package labyrinth.service.persistence;

import labyrinth.service.serialization.SerializationService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import labyrinth.service.serialization.GameDTO;
import java.time.Instant;

public class SqlPersistenceService {

    private final Connection connection; // connexion à la BDD
    private final SerializationService serializationService; // service de sérialisation
    private final List<UserProfileChangedHandler> handlers = new ArrayList<>(); // observateurs

    public SqlPersistenceService(SerializationService serializationService) {
        this.serializationService = serializationService;

        try {
            // récupère le dossier "home" de l'utilisateur
            Path dir = Path.of(System.getProperty("user.home"), ".labyrinth");
            Files.createDirectories(dir); // crée le dossier si absent

            // crée le chemin d'accès vers le fichier de la BDD
            Path dbPath = dir.resolve("labyrinth.db");

            // ouverture de la connexion sql
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            // création des tables (si elles n'existent pas déjà)
            initTables();

        } catch (Exception e) {
            throw new SqlException("Erreur ouverture BDD", e);
        }
    }

    // crée les tables de la BDD (si elles n'existent pas)
    private void initTables() {

        // requête sql a envoyer à la bdd
        try (Statement stmt = connection.createStatement()) {

            // table Players(id,name)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS profiles (
                    id   TEXT PRIMARY KEY,
                    name TEXT NOT NULL
                )
            """);

            // table Games(id,timestamp,data)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS games (
                    id        TEXT PRIMARY KEY,
                    timestamp INTEGER NOT NULL,
                    data      TEXT NOT NULL
                )
            """);

        } catch (SQLException e) {
            throw new SqlException("Erreur initialisation des tables", e);
        }
    }

    // sauvegarde une partie dans la bdd
    public void saveGame(GameDTO gameDTO) {

        String data = serializationService.serialize(gameDTO); // sérialize le DTO
        long timestamp = Instant.now().toEpochMilli(); // récupère l'instant actuel (timestamp)
        String id = gameDTO.getUuid().toString(); // uuid de la partie en chaine

        // requête préparée pour insérer une ligne dans la table Games()
        // les ? sont remplacés par les paramètres juste en dessous
        String sql = "INSERT OR REPLACE INTO games (id, timestamp, data) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id); // id de la partie (id)
            ps.setLong(2, timestamp); // instant (timestamp)
            ps.setString(3, data); // données (data)
            ps.executeUpdate(); // éxécution de la requête

        } catch (SQLException e) {
            throw new SqlException("Erreur sauvegarde de la partie", e);
        }
    }
}
