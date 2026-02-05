import java.util.Set;

public enum TileKind {
    CORRIDOR {
        @Override
        public Set<Direction> getExits() {
            return Set.of();
        }
    }, ELBOW {
        @Override
        public Set<Direction> getExits() {
            return Set.of();
        }
    }, T_CROSS {
        @Override
        public Set<Direction> getExits() {
            return Set.of();
        }
    };

    public abstract Set<Direction> getExits();
}
