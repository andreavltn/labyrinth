package labyrinth.util.graph;

import java.util.*;

public class Graph<T> {

    private final Set<T> nodes; // ensemble des noeuds du graphe
    private final Map<T, Set<T>> edges; // map noeud -> successeur(s)

    public Graph() {
        this.nodes = new HashSet<>();
        this.edges = new HashMap<>();
    }

    // ajoute le noeud à l'ensemble des noeuds
    public void addNode(T node) {
        nodes.add(node); // ajoute le noeud a l'ensemble
        if (!edges.containsKey(node)) { // si le noeud n'est pas déjà clé dans la map
            edges.put(node, new HashSet<>()); // l'ajoute (avec aucun successeurs pour l'instant)
        }
    }

    // ajoute une arête de "from" a "to"
    public void addEdge(T from, T to) {
        edges.get(from).add(to);
    }

    // renvoie l'ensemble des noeuds du graphe
    public Set<T> getNodes() {
        return Set.copyOf(nodes);
    }

    // renvoie l'ensemble des successeurs d'un noeud
    public Set<T> getSuccessors(T node) {
        if (edges.containsKey(node)) { // si le noeud appartient au graphe
            return Set.copyOf(edges.get(node)); // renvoie la liste de tous ses successeurs (copie immutable)
        } else {
            return Set.of(); // sinon renvoie un ensemble vide (immutable)
        }
    }
}