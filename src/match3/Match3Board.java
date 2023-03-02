package match3;

public class Match3Board {
    public int width;
    public int height;
    public Match3Cell[][] grid;

    public Match3Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Match3Cell[width][height];
    }
}
