package uoc.ds.pr.util;

import uoc.ds.pr.SportEvents4Club;

public class LevelHelper {
    private int value;
    private SportEvents4Club.Level level;

    public LevelHelper(int value, SportEvents4Club.Level level) {
        this.value = value;
        this.level = level;
    }

    public static SportEvents4Club.Level getLevel(int value) {
        SportEvents4Club.Level level = null;
        if (value < 2) {
            level = SportEvents4Club.Level.ROOKIE;
        } else if (value < 5) {
            level = SportEvents4Club.Level.PRO;
        } else if (value < 10) {
            level = SportEvents4Club.Level.EXPERT;
        } else if (value < 15) {
            level = SportEvents4Club.Level.MASTER;
        } else {
            level = SportEvents4Club.Level.LEGEND;
        }

        return level;
    }
}
