package match3;

public class Match3Cell {
    public GemType type;
    public SpecialGem specialGem;

    public enum GemType {
        RED,
        YELLOW,
        BLUE,
        GREEN,
        PURPLE
    }

    public enum SpecialGem {
        NONE,
        VERTICAL,
        HORIZONTAL,
        BOMB,
        THUNDER
    }
}
