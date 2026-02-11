package labyrinth.model;

public enum Direction {
    UP, RIGHT, DOWN, LEFT; // direction ordonnées

    public Direction getOpposite(){
        return values()[(this.ordinal()+2)%4]; // renvoie la direction deux valeurs plus loin
    }

    public Direction getRotated(int nbTimes){
        int index = (this.ordinal()+nbTimes)%4;
        if (index<0){ // si index<0 (nbTimes négatif)
            index+=4; // on lui ajoute 4 pour faire la rotation complète
        }
        return values()[index]; // renvoie la direction obtenue après nbTimes rotations (positif ou négatif)
    }

    public Direction getRotated(Direction direction){
        return getRotated(direction.ordinal()); // fait la rotation correspondante à la direction
    }
}