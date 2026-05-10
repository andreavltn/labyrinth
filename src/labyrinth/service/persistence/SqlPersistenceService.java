package labyrinth.service.persistence;

import labyrinth.service.serialization.SerializationService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

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

            Path dbPath = dir.resolve("labyrinth.db"); // chemin complet vers le fichier de la BDD

            // ouverture de la connexion SQLite
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            // création des tables (si elles n'existent pas déjà)
            initTables();

        } catch (Exception e) {
            throw new SqlException("Erreur ouverture BDD", e);
        }
    }

    // crée les tables de la BDD (si elles n'existent pas)
    private void initTables() {
        // a faire
    }
}
