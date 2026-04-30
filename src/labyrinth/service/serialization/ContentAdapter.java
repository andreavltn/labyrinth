package labyrinth.service.serialization;

import com.google.gson.*;
import labyrinth.model.*;
import java.lang.reflect.Type;

public class ContentAdapter implements JsonSerializer<Content>, JsonDeserializer<Content> {

    @Override // sérialisation Content -> JsonObject
    public JsonElement serialize(Content src, Type typeOfSrc, JsonSerializationContext ctx) {

        // ici on ajoute un champ typeName pour connaître le type
        // au moment de la désérialisation (car on ne le connait
        // pas étant donné que l'interface Content est sealed).

        JsonObject obj;
        String typeName;

        // en fonction du contenu
        switch (src) {

            // None (vide)
            case None n -> {
                obj = new JsonObject(); // nouvel objet (vide)
                typeName = "none"; // de type "none"
            }

            // StartingPosition (position de départ)
            case StartingPosition s -> {
                // sérialise le contenu (utilise un nouveal objet pour éviter la récursion infinie)
                obj = new Gson().toJsonTree(s).getAsJsonObject();
                typeName = "startingPosition"; // de type "startingPosition"
            }

            // Treasure (trésor)
            case Treasure t -> {
                // sérialise le contenu (utilise un nouveal objet pour éviter la récursion infinie)
                obj = new Gson().toJsonTree(t).getAsJsonObject();
                typeName = "treasure"; // de type "treasure"
            }

            // sinon par défaut, erreur
            default -> throw new JsonParseException("Le type : " + src.getClass() + " est inconnu");
        }

        // injecte la propriété typeName dans l'objet sérialisé
        obj.addProperty("type", typeName);
        return obj; // renvoie l'objet sérialisé
    }

    @Override // désérialisation JsonObject -> Content
    public Content deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx){

        // ici étape inverse, on désérialise l'objet sérialisé.
        // dans le cas où l'objet n'a pas de type, on considère
        // que c'est un contenu vide (None) par sécurité

        JsonObject obj = json.getAsJsonObject(); // récupère l'objet sérialisé

        // si l'objet n'a pas de propriété "type"
        if (!obj.has("type")) {
            return new None(); // alors c'est un None
        }

        // convertit le type en chaine
        String typeName = obj.get("type").getAsString();

        Content content;
        // en fonction de la chaine du type, désérialise l'objet
        switch (typeName) {
            case "none" -> content = new None();

            // nouvel objet pour éviter la récursion
            case "startingPosition" -> content = new Gson().fromJson(obj, StartingPosition.class);

            // idem
            case "treasure" -> content = new Gson().fromJson(obj, Treasure.class);

            // sinon par défaut, erreur
            default -> throw new JsonParseException("Le type : " + typeName + " est inconnu");
        };

        return content; // renvoie le contenu désérialisé
    }
}