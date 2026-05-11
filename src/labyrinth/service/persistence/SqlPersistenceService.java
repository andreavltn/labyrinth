package labyrinth.service.persistence;

import labyrinth.service.serialization.SerializationService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import labyrinth.service.serialization.GameDTO;
import java.time.Instant;
import java.util.UUID;

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
        long timestamp = Instant.now().toEpochMilli(); // récupère l'horodatage actuel (timestamp)
        String id = gameDTO.getUuid().toString(); // uuid de la partie en chaine

        // requête préparée pour insérer une ligne dans la table Games()
        // les ? sont remplacés par les paramètres juste en dessous
        String sql = "INSERT OR REPLACE INTO games (id, timestamp, data) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id); // id de la partie (id)
            ps.setLong(2, timestamp); // horodatage (timestamp)
            ps.setString(3, data); // données (data)
            ps.executeUpdate(); // éxécution de la requête

        } catch (SQLException e) {
            throw new SqlException("Erreur sauvegarde de la partie", e);
        }
    }

    // charge la dernière partie de la table Games
    public GameDTO loadLatestGame() {

        // requête pour récupérer les données de la première ligne de la table Games
        // (triée par horodatage décroissant)
        String sql = "SELECT data FROM games ORDER BY timestamp DESC LIMIT 1";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) { // éxécute la requête

            // si il y a au moins une ligne de résultat (i.e la table n'est pas vide)
            if (rs.next()) {
                String data = rs.getString("data"); // récupère la colonne "data" de la ligne
                return serializationService.deserialize(data); // désérialize les données
            } else {
                return null; // sinon aucun résultat, pas de partie
            }

        } catch (SQLException e) {
            throw new SqlException("Erreur chargement de la dernière partie", e);
        }
    }

    // crée un nouveau profil utilisateur dans la table Profiles
    public UUID createUserProfile(String name) {

        UUID uuid = UUID.randomUUID(); // génère un UUID aléatoire unique

        // requête préparée pour insérer le profil dans la table Profiles
        String sql = "INSERT INTO profiles (id, name) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, uuid.toString()); // uuid en chaine (uuid)
            ps.setString(2, name); // nom (name)
            ps.executeUpdate(); // éxécute la requête

        } catch (SQLException e) {
            throw new SqlException("Erreur lors de la création du profil", e);
        }

        firePlayerInfoChanged(uuid); // notifie les observateurs pour rafraichir l'affichage
        return uuid; // renvoie l'UUID généré
    }
}
