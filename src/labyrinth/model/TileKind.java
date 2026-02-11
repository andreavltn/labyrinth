package labyrinth.model;

import java.util.Set;

public enum TileKind {
    CORRIDOR {
        @Override
        public Set<Direction> getExits() {
            return Set.of(Direction.UP, Direction.DOWN); //Les directions disponibles pour un couloir
        }
    },

    ELBOW {
        @Override
        public Set<Direction> getExits() {
            return Set.of(Direction.UP, Direction.RIGHT); //Les directions disponibles pour un coude
        }
    },

    T_CROSS {
        @Override
        public Set<Direction> getExits() {
            return Set.of(Direction.UP, Direction.LEFT, Direction.RIGHT); //Les directions disponibles pour une croix en T
        }
    };

    public abstract Set<Direction> getExits();
}
