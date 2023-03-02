package match3;

public class Main {
    // https://github.com/nativegamestudio/juicy-match
    public static void main(String[] args) {
        Match3Game game = new Match3Game(8, 8);
        Match3Player player1 = new Match3Player();
        Match3Player player2 = new Match3Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.currentPlayer = 1;
        game.startGame();
    }
}
