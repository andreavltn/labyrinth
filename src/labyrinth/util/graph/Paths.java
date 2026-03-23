package labyrinth.util.graph;

import java.util.*;

public class Paths<T> {

    private final Map<T, T> predecessors; // map noeud -> prédecesseur (null pour la racine)

    public Paths(Graph<T> graph, T from) {

        this.predecessors = new HashMap<>(); // nouvelle map noeud -> prédécesseur
        predecessors.put(from, null); // la racine n'a pas de prédécesseur

        // parcours en BFS vvvvv
        // même principe qu'en cours algo et graphes
        // blanc = jamais visité
        // gris = visité une fois

        Queue<T> gris = new LinkedList<>(); // pile des gris
        gris.add(from); // empile la racine

        // tant que la pile n'est pas vide
        while (!gris.isEmpty()) {
            T node = gris.poll(); // dépile le sommet des gris

            // pour chaque successeur blanc
            for (T successor : graph.getSuccessors(node)) {
                if (!predecessors.containsKey(successor)) {
                    predecessors.put(successor, node); // lien noeud -> prédécesseur dans la map
                    gris.add(successor); // ajoute aux gris
                }
            }
        }
    }

    // renvoie true si le noeud est accessible depuis la racine
    public boolean isAccessible(T node) {
        return predecessors.containsKey(node);
    }

    // reconstruit le chemin complet depuis la source jusqu'au noeud
    public List<T> getPath(T node) {
        if (!isAccessible(node)) { // si le noeud n'est pas accessible
            return null; // pas de chemin
        }

        List<T> path = new ArrayList<>(); // nouveau chemin
        T current = node; // noeud courant = noeud auquel on veut accéder

        while (current != null) { // tant que le courant n'est pas nul (i.e on n'est pas a la racine)
            path.add(current); // ajoute le noeud au chemin
            current = predecessors.get(current); // passe le courant au prédécesseur
        }

        // en arrivant ici on a le chemin de la forme (noeud, pred(noeud), pred(pred(noeud)), ..., racine)
        Collections.reverse(path); // on inverse pour avoir l'ordre (racine, ... , noeud)
        return path; // renvoie le chemin
    }
}