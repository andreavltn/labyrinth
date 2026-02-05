import java.util.Set;

public enum TileKind {
    CORRIDOR, ELBOW, T_CROSS;
}

    public abstract Set<Direction> getExits();
}
