package labyrinth.service.serialization;

import com.google.gson.*;
import labyrinth.util.collection.ArrayGrid;
import labyrinth.util.collection.Grid;
import java.lang.reflect.Type;

// adaptateur Gson pour Grid<TileDTO>
public class GridAdapter implements JsonSerializer<Grid<TileDTO>>, JsonDeserializer<Grid<TileDTO>> {

    @Override // sérialisation Grid<TileDTO> -> JsonArray
    public JsonElement serialize(Grid<TileDTO> src, Type typeOfSrc, JsonSerializationContext ctx) {

        // ici on va créer une JsonArray 2D (tableau de tableaux)
        // et dans chacun des tableaux on va stocker la sérialisation des tuiles

        JsonArray rows = new JsonArray(); // crée un tableau vide (le grand tableau)
        int height = src.getHeight(); // récupère la hauteur de la grille
        int width = src.getWidth(); // récupère la largeur de la grille

        // pour chaque ligne
        for (int y = 0; y < height; y++) {
            JsonArray row = new JsonArray(); // crée un tableau vide

            // pour chaque tuile de la ligne
            for (int x = 0; x < width; x++) {

                // ajoute la sérialisation de la tuile au tableau
                row.add(ctx.serialize(src.get(x, y), TileDTO.class));
            }

            // ajoute le tableau au grand tableau principal
            rows.add(row);
        }

        return rows; // renvoie le grand tableau (sérialisé)
    }

    @Override // désérialisation JsonArray -> ArrayGrid<TileDTO>
    public Grid<TileDTO> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx){

        // ici étape inverse, on parcourt le grand tableau et on désérialise
        // chaque tuile une par une, qu'on rajoute à une nouvelle grille

        JsonArray rows = json.getAsJsonArray(); // récupère le grand tableau
        int height = rows.size(); // récupère le nombre de lignes

        // récupère la largeur à partir de la taille de la première ligne
        int width;
        if(height>0){ // si il y a bien une première ligne (hauteur>0)
            width = rows.get(0).getAsJsonArray().size(); // récupère la largeur
        } else { // sinon largeur 0
            width = 0;
        }

        // crée une grille vide
        ArrayGrid<TileDTO> grid = new ArrayGrid<>(width, height);

        // pour chaque indice y
        for (int y = 0; y < height; y++) {
            JsonArray row = rows.get(y).getAsJsonArray(); // récupère la ligne d'indice y

            // pour chaque indice x
            for (int x = 0; x < width; x++) {

                // récupère la tuile x de la ligne y, et la désérialise
                TileDTO tile = ctx.deserialize(row.get(x), TileDTO.class);
                grid.set(x, y, tile); // l'ajoute à la grille
            }
        }

        return grid; // renvoie la nouvelle grille désérialisée
    }
}