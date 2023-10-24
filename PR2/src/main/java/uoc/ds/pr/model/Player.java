package uoc.ds.pr.model;

import java.time.LocalDate;
import java.util.Objects;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.SportEvents4Club;

public class Player {
    private String id;
    private String name;
    private String surname;
    private LocalDate birthday;
    private List<SportEvent> events;
    private int numRatings;
    private SportEvents4Club.Level level;
    private int numFollowers;
    private int numFollowings;
    private List<Post> posts;

	public Player(String idUser, String name, String surname, LocalDate birthday) {
        this.setId(idUser);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
        this.events = new LinkedList<>();
        numRatings = 0;
        this.setLevel(numRatings);
        this.numFollowers = 0;
        this.numFollowings = 0;
        this.posts = new LinkedList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setLevel(int numRatings) {
        if (numRatings < 2) {
            this.level = SportEvents4Club.Level.ROOKIE;
        } else if (numRatings < 5) {
            this.level = SportEvents4Club.Level.PRO;
        } else if (numRatings < 10) {
            this.level = SportEvents4Club.Level.EXPERT;
        } else if (numRatings < 15) {
            this.level = SportEvents4Club.Level.MASTER;
        } else {
            this.level = SportEvents4Club.Level.LEGEND;
        }
    }

    public void incNumRatings() {
        this.numRatings++;
        setLevel(numRatings);
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public SportEvents4Club.Level getLevel() {
        return level;
    }

    public boolean is(String playerID) {
        return id.equals(playerID);
    }

    public void addEvent(SportEvent sportEvent) {
        events.insertEnd(sportEvent);
    }

    public int numEvents() {
        return events.size();
    }

    public boolean isInSportEvent(String eventId) {
        boolean found = false;
        SportEvent sportEvent = null;
        Iterator<SportEvent> it = getEvents();
        while (it.hasNext() && !found) {
            sportEvent = it.next();
            found = sportEvent.is(eventId);
        }
        return found;
    }

    public int numSportEvents() {
        return events.size();
    }

    public Iterator<SportEvent> getEvents() {
        return events.values();
    }

    public boolean hasEvents() {
        return this.events.size()>0;
    }

    public int numFollowers() {
        return this.numFollowers;
    }

    public int numFollowings() {
        return this.numFollowings;
    }

    public void incNumFollowers() {
        this.numFollowers++;
    }

    public void incNumFollowings() {
        this.numFollowings++;
    }

    /*
    public String getMessage() {
        String message = null;
        boolean found = false;

        if (this.hasEvents()) {
            while(getEvents().hasNext()) {
                SportEvent se = getEvents().next();
                System.out.println(se.getEventId());

                if (this.isInSportEvent(se.getEventId())) {
                    message += "{'player': '" + this.getId() + "', 'sportEvent': '"+se.getEventId()+"', ";
                    //System.out.println(message);
                    if(se.hasRatings()) {
                        while(se.ratings().hasNext() && !found) {
                            Rating r = se.ratings().next();

                            if (Objects.equals(r.getPlayer().getId(), this.getId())) {
                                message += "'rating': '"+r.rating()+"', 'action': 'rating'}";
                                found = true;
                            }
                        }

                        if (!found) {
                            message += " 'action': 'signup'}";
                        }
                    }
                }
                found = false;
            }
        }
        return message;
    }*/
}
