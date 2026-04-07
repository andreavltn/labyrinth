package labyrinth.service.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import labyrinth.model.Content;
import labyrinth.util.collection.Grid;


public class JsonSerializationService implements SerializationService {

    private final Gson gson;

    public JsonSerializationService() {
        this.gson = new GsonBuilder()

                // gestion du polymorphisme de Content (des types de contenus d'une tuile)
                .registerTypeHierarchyAdapter(Content.class, new ContentAdapter())

                // gestion de la généricité de Grid<TileDTO> (problème des {})
                .registerTypeAdapter(
                        new TypeToken<Grid<TileDTO>>() {}.getType(),
                        new GridAdapter()
                )

                // conserver les nulls (slots vides par ex)
                .serializeNulls()

                // création
                .create();
    }

    @Override // sérialisation
    public String serialize(GameDTO game) {
        return gson.toJson(game);
    }

    @Override // désérialisation
    public GameDTO deserialize(String data) {
        return gson.fromJson(data, GameDTO.class);
    }
}