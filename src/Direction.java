public enum Direction {
    UP, RIGHT, DOWN, LEFT; // direction ordonnées

    public Direction getOpposite(){
        return values()[(this.ordinal()+2)%4]; // renvoie la direction deux valeurs plus loin
    }

    public Direction getRotated(int nbTimes){
        return values()[(this.ordinal()+nbTimes)%4]; // renvoie la direction "nbTimes" valeurs plus loin
    }

    public Direction getRotated(Direction direction){
        return getRotated(direction.ordinal()); // fait la rotation correspondante à la direction
    }
}