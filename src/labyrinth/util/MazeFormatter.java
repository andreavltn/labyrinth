package labyrinth.util;

import labyrinth.model.*;
import labyrinth.util.collection.Grid;

public class MazeFormatter {

    // caractères spéciaux pour dessiner les tuiles
    private static final char H  = '\u2500'; // ─ (horizontal)
    private static final char V  = '\u2502'; // │ (vertical)
    private static final char TL = '\u250C'; // ┌ (top-left -> coin en haut à gauche)
    private static final char TR = '\u2510'; // ┐ (top-right -> coin en haut à droite)
    private static final char BL = '\u2514'; // └ (bottom-left -> coin en bas à gauche)
    private static final char BR = '\u2518'; // ┘ (bottom-right -> coin en bas à droite)

    // méthode qui renvoie la chaine associée à la représentation d'une tuile
    public String format(Tile tile) {

        // récupérer les sorties de la tuile
        boolean up = tile.getExits().contains(Direction.UP);
        boolean down = tile.getExits().contains(Direction.DOWN);
        boolean left = tile.getExits().contains(Direction.LEFT);
        boolean right= tile.getExits().contains(Direction.RIGHT);

        // récupérer le contenu de la tuile
        String center = getContentChar(tile.getContent());

        // création de la chaine (5 lignes)
        StringBuilder sb = new StringBuilder();

        // 1è ligne
        sb.append(TL); // coin haut gauche
        sb.append(H).append(H).append(H); // traits
        sb.append(up ? ' ' : H); // trou si sortie en haut, sinon trait
        sb.append(H).append(H).append(H); // traits
        sb.append(TR); // coin haut droite
        sb.append('\n');

        // 2è ligne
        sb.append(V);
        sb.append("       "); // vide
        sb.append(V);
        sb.append('\n');

        // 3è ligne
        sb.append(left ? ' ' : V); // trou si sortie à gauche, sinon trait
        sb.append("   ").append(center).append("   "); // contenu au centre
        sb.append(right ? ' ' : V); // trou si sortie à droite, sinon trait
        sb.append('\n');

        // 4è ligne (idem que ligne 2)
        sb.append(V);
        sb.append("       ");
        sb.append(V);
        sb.append('\n');

        // 5è ligne
        sb.append(BL); // coin bas gauche
        sb.append(H).append(H).append(H); // traits
        sb.append(down ? ' ' : H); // trou si sortie en bas, sinon trait
        sb.append(H).append(H).append(H); // traits
        sb.append(BR); // coin bas droite

        return sb.toString();
    }

    // méthode qui renvoie le contenu d'une tuile sous forme d'un caractère
    private String getContentChar(Content content) {

        // en fonction du contenu de la tuile
        return switch (content) {
            case None n -> " "; // rien -> vide
            case StartingPosition s -> String.valueOf(s.index());  // case de départ -> 0,1,2,3
            case Treasure t -> formatTreasure(t); // trésor -> A,B,C,...
        };
    }

    // méthode qui renvoie la lettre associée à l'id d'un trésor
    private String formatTreasure(Treasure t) {
        char val = (char)('A' + t.id()); // décalage avec l'id depuis la valeur associée à 'A'
        return String.valueOf(val); // conversion en String
    }

    // méthode qui renvoie la chaine associée à la représentation complète du labyrinthe
    public String format(Maze maze) {
        return null;
    }
}
