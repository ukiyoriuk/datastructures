package uoc.ds.pr.model;

public class Post {

    private String message;
    private Player player;

    public Post(Player player, String message) {
        message = null;
        this.player = null;
    }

    public String message() {
        return message;
    }
}
