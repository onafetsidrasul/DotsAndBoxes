public class Player {
    private String name;
    private Color color;
    private int score;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    public int getScore(Player player) {
        return player.score;
    }

    public void increaseScore(Player player) {
        player.score++;
    }

}
