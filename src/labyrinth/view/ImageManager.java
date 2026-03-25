package labyrinth.view;

import javafx.scene.image.Image;
import labyrinth.model.Direction;
import labyrinth.model.TileKind;

import java.util.HashMap;
import java.util.Map;

public class ImageManager {

    // instance unique dans toute l'appli
    private static final ImageManager instance = new ImageManager();

    // map chemin -> image (c'est le cache)
    private final Map<String, Image> cache = new HashMap<>();

    // constructeur privé
    private ImageManager() {}

    // accès à l'instance
    public static ImageManager getInstance() {
        return instance;
    }

    // retourne l'image depuis le cache
    public Image getImage(String path) {
        if (!cache.containsKey(path)) { // si le cache ne contient pas déjà le chemin
            cache.put(path, new Image(path)); // le stocke
        }
        return cache.get(path); // renvoie l'image associée au chemin
    }

    // nom du fichier = type-direction.png (même méthode que précédemment dans TileView)
    public Image getTileImage(TileKind kind, Direction direction) {

        // nom de la tuile en minuscules (pour le chemin d'accès)
        String kindName = switch (kind) {
            case CORRIDOR -> "corridor";
            case ELBOW -> "elbow";
            case T_CROSS -> "tcross";
        };

        // angle de rotation pour l'affichage de la bonne image
        int degrees = switch (direction) {
            case UP -> 0;
            case RIGHT -> 90;
            case DOWN -> 180;
            case LEFT -> 270;
        };

        // la tuile corridor est symétrique, donc n'a que 2 "états" (et 2 images aussi)
        // donc angle modulo 180 pour celle-ci
        if (kind == TileKind.CORRIDOR) {
            degrees = degrees % 180;
        }

        // création du chemin d'accès
        String path = "/img/tiles/" + kindName + "-" + degrees + ".png";
        return getImage(path);
    }
}