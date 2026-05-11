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

        // requêtes sql a envoyer à la bdd
        try (Statement stmt = connection.createStatement()) {

            // création de la table Players(id,name)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS profiles (
                    id   TEXT PRIMARY KEY,
                    name TEXT NOT NULL
                )
            """);

            // création de la table Games(id,timestamp,data)
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

            ps.setString(1, id); // remplace l'id de la partie par le param
            ps.setLong(2, timestamp); // idem pour l'horodatage
            ps.setString(3, data); // idem pour les données
            ps.executeUpdate(); // éxécute la requête d'insertion

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

            ps.setString(1, uuid.toString()); // remplace l'uuid par le param
            ps.setString(2, name); // idem pour le nom
            ps.executeUpdate(); // éxécute la requête d'insertion

        } catch (SQLException e) {
            throw new SqlException("Erreur création du profil", e);
        }

        firePlayerInfoChanged(uuid); // notifie les observateurs pour rafraichir l'affichage
        return uuid; // renvoie l'UUID généré
    }

    // récupère la liste des profils utilisateur depuis la bdd
    public List<UserProfile> getUserProfiles() {

        List<UserProfile> profiles = new ArrayList<>(); // liste vide

        // requête pour récupérer tous les profils
        String sql = "SELECT id, name FROM profiles";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) { // récupère le résultat de la requête

            // tant qu'il y a des lignes dans le résultat
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("id")); // recrée l'UUID depuis la chaine
                String name = rs.getString("name"); // récupère le nom
                profiles.add(new UserProfile(uuid, name)); // ajoute le profil aux profils
            }

        } catch (SQLException e) {
            throw new SqlException("Erreur récupération de tous les profils", e);
        }

        return profiles; // renvoie la liste remplie
    }

    // idem mais avec un uuid choisi
    public UserProfile getUserProfile(UUID userId) {

        // requête préparée pour rechercher le profil correspondant à l'uuid
        String sql = "SELECT id, name FROM profiles WHERE id = ?"; // filtre par UUID

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // remplace l'id par l'uuid en paramètre
            ps.setString(1, userId.toString());

            ResultSet rs = ps.executeQuery(); // récupère le résultat de la requête

            // si un profil existe avec cet uuid (un seul car l'uuid est unique)
            if (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("id")); // récupère l'uuid
                String name = rs.getString("name"); // récupère le nom
                return new UserProfile(uuid, name); // renvoie le profil correspondant
            }

            return null; // sinon aucun profil ne correspond à l'uuid

        } catch (SQLException e) {
            throw new SqlException("Erreur récupération du profil", e);
        }
    }

    // met à jour le nom d'un utilisateur dans la bdd, à partir de son uuid
    public void setUserName(UUID userId, String name) {

        // requête préparée pour mettre à jour le nom à partir d'un uuid
        String sql = "UPDATE profiles SET name = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name); // remplace le nom par le param
            ps.setString(2, userId.toString()); // idem pour l'id
            ps.executeUpdate(); // éxécute la requête de mise à jour

        } catch (SQLException e) {
            throw new SqlException("Erreur mise à jour du profil", e);
        }

        firePlayerInfoChanged(userId); // notifie les observateurs pour rafraichir l'affichage
    }

    // supprime le profil d'un utilisateur de la bdd, à partir de son uuid
    public void deleteUserProfile(UUID userId) {

        // requête préparée pour supprimer l'utilisateur avec l'uuid
        String sql = "DELETE FROM profiles WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userId.toString()); // remplace l'uuid par le param
            ps.executeUpdate(); // éxécute la requête de suppression

        } catch (SQLException e) {
            throw new SqlException("Erreur suppression du profil", e);
        }

        firePlayerInfoChanged(userId); // notifie les observateurs pour rafraichir l'affichage
    }




    // PATTERN OBSERVATEUR

    // enregistre un nouveau handler
    public void addHandler(UserProfileChangedHandler handler) {
        handlers.add(handler);
    }

    // supprime un handler
    public void removeHandler(UserProfileChangedHandler handler) {
        handlers.remove(handler);
    }

    // notifie tous les handlers enregistrés avec l'UUID concerné
    private void firePlayerInfoChanged(UUID playerId) {
        for (UserProfileChangedHandler h : handlers) {
            h.handlePlayerInfoChanged(playerId);
        }
    }
}
