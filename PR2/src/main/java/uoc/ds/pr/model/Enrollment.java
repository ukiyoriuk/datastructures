package uoc.ds.pr.model;

public class Enrollment implements Comparable<Enrollment> {
    private boolean isSubstitute;
    Player player;

    public Enrollment(Player player, boolean isSubstitute) {
        this.player = player;
        this.isSubstitute = isSubstitute;
    }


    public Player getPlayer() {
        return player;
    }

    @Override
    public int compareTo(Enrollment o) {
        return getPlayer().getLevel().compareTo(o.getPlayer().getLevel());
    }
}
