package uoc.ds.pr;

import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;

import java.time.LocalDate;



public interface SportEvents4Club {



    enum Status {
        PENDING,
        ENABLED,
        DISABLED
    }

    enum Type {
        MICRO,
        SMALL,
        MEDIUM,
        LARGE,
        XLARGE,
    }
    //
    // Resources
    //
    public static final byte FLAG_PRIVATE_SECURITY = 1;    // 0001
    public static final byte FLAG_PUBLIC_SECURITY = 2;    // 0010
    public static final byte FLAG_BASIC_LIFE_SUPPORT = 4;  // 0100
    public static final byte FLAG_VOLUNTEERS = 8;          // 1000
    public static final byte FLAG_ALL_OPTS  = 15; // 1111

    //
    // E : Sport Events
    //
    public static final int MAX_NUM_SPORT_EVENTS = 250;
    public static final int MAX_NUM_ENROLLMENT= 150;

    public static final int MAX_NUM_PLAYER = 120;
    public static final int MAX_NUM_ORGANIZING_ENTITIES = 20;


    enum Rating {
        ONE (1),
        TWO (2),
        THREE  (3),
        FOUR (4),
        FIVE (5);

        private final int value;

        private Rating(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ///////
    // PR2
    ///////

    enum Level {
        LEGEND(5),
        MASTER(4),
        EXPERT(3),
        PRO(2),
        ROOKIE(1);
        private final int value;

        private Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        }

    public static final int MAX_ROLES = 10;

    public static final int MAX_ORGANIZING_ENTITIES_WITH_MORE_ATTENDERS = 5;

    /**
     * Add a new player to the system. We know of each player his identifier, their name and surnames and their date of birth.
     * If there is already a player with that identifier, we update their data.
     *
     * @pre true.
     * @post if the player code is new, the players will be the same plus a new player.
     * If not, the player data will have been updated.
     *
     * @param id identifier
     * @param name the name
     * @param surname the surname
     * @param dateOfBirth the date of birth
     */
    public void addPlayer(String id, String name, String surname, LocalDate dateOfBirth);

    /**
     * Add an organizing entity in the system. Of each organizing entity we know
     * its identifier, its name and description. If there is already an
     * organizing entity with the same identifier, we update its data.
     *
     * @pre true.
     * @post if the organizing entity code is new, the organizing entities
     * will be the same plus a new one.
     * If not, the data of the organizing entity will have been updated.
     *
     * @param id the identifier
     * @param name the name
     * @param description the description
     */
    public void addOrganizingEntity (String id, String name, String description);

    /**
     * Create a file about a new sporting event
     *
     * @param id the identifier
     * @param eventId event identifier
     * @param orgId the organizing Entity
     * @param description the description
     * @param type the type according to its magnitude
     * @param resources resources needed (security, basic life support, volunteers
     * @param max the maximum number of entries allowed
     * @param startDate start date
     * @param endDate end Date
     *
     * @pre the event and the file do not exist.
     * @post the files will be the same plus a new one.
     * If the organizing entity does not exist, the error will be reported
     *
     * @see uoc.ds.pr.util.ResourceUtil
     * @throws OrganizingEntityNotFoundException
     */
    public void addFile(String id, String eventId, String orgId, String description,
                        Type type, byte resources, int max, LocalDate startDate, LocalDate endDate) throws OrganizingEntityNotFoundException;

    /**
     * Approve a file. In both cases, the date on which it was carried out is recorded
     * @param status
     * @param date
     * @param description
     * @return
     * @throws NoFilesException
     */
    public File updateFile(Status status, LocalDate date, String description) throws NoFilesException;

    /**
     *
     * Enrolment in a sporting event by a player. If the player or
     * sporting event does not exist, an error will be shown. The
     * players will be registered in order of arrival. If the
     * maximum number of participants has been exceeded, an error
     * will be indicated, but the participants will also be stored as
     * substitutes, who will enter to compete in the event of a casualty.
     * Of course, the participation of a substitute player due to withdrawal
     * will also be done in order of arrival
     *
     * @param playerId the player
     * @param eventId sport Event
     * @throws PlayerNotFoundException if player not exist
     * @throws SportEventNotFoundException if sport event not exist
     * @throws LimitExceededException If the maximum number of participants has
     * been exceeded
     *
     * @pre true.
     * @post the number of registrations for an event will be the same plus one.
     * If the player or event does not exist, the error will be reported.
     * If the maximum number of registrations has been exceeded, the error will be reported.
     *
     */
    public void signUpEvent(String playerId, String eventId) throws PlayerNotFoundException, SportEventNotFoundException, LimitExceededException;

    /**
     * Check the percentage of rejected files
     * @return the percentage of rejected files
     *
     * @pre true.
     * @post returns a real number with the percentage of files that have not been approved.
     */
    public double getRejectedFiles();


    /**
     * Consult the sporting events of an organizing entity.
     *
     * @pre the organizing entity exists.
     * @post returns an iterator to iterate through the events of an organizing entity.
     * If there are no events, the error will be reported.
     *
     * @param organizationId the organizing Entity
     * @return iterator to iterate through the events of an organizing entity
     * @throws NoSportEventsException if there are no sports event
     */
    public Iterator<SportEvent> getSportEventsByOrganizingEntity(String organizationId) throws NoSportEventsException;

    /**
     * Consult all the sporting events that are in the system. If there is none, an error will be shown.
     *
     * @pre true.
     * @post returns an iterator to iterate through all the events.
     * If there are no events, the error will be reported.
     *
     * @return iterator to iterate all the events.
     *
     */
    public Iterator<SportEvent> getAllEvents() throws NoSportEventsException;

    /**
     *
     * Consult the sporting events in which a player has participated.
     * It is known in advance that the player
     * exists. If there are no sporting events, an error will be shown..
     * @pre the player exists.
     * @post returns an iterator to loop through all events for a player.
     * If there are no events, the error will be reported.
     *
     * @param playerId the player
     * @return returns an iterator to loop through all events for a
     * player. If there are no events, the error will be reported.
     * @throws NoSportEventsException If there are no sporting events
     */
    public Iterator<SportEvent> getEventsByPlayer(String playerId) throws NoSportEventsException ;

    /**
     *Addition of a rating in numerical format (1-10) and a comment of
     * a sporting event by a player. If the player or event does not exist, an error will be shown. If the player has not participated in the event, an error will be shown.
     * @param playerId the player
     * @param eventId the sport Event
     * @param rating the rating
     * @param message the message
     * @pre true.
     * @post the ratings will be the same plus one.
     * If the player or event does not exist, the error will be reported.
     * If the player has not participated in the event, the error will be reported.
     * @throws SportEventNotFoundException if the sport event does not exist.
     * @throws PlayerNotFoundException If the player or event does not exist
     * @throws PlayerNotInSportEventException If the player has not participated in the event
     */
    public void addRating(String playerId, String eventId, Rating rating, String message)
            throws SportEventNotFoundException, PlayerNotFoundException, PlayerNotInSportEventException;

    /**
     * @pre true.
     * @post returns an iterator to iterate through the ratings of an event.
     * If the event does not exist, the error will be reported.
     * If there are no ratings, the error will be reported.
     * @param eventId the sport event
     * @return  an iterator to iterate through the ratings of an event
     * @throws SportEventNotFoundException if the sport event does not exist
     * @throws NoRatingsException if there are no ratings
     */
    public Iterator<uoc.ds.pr.model.Rating> getRatingsByEvent(String eventId) throws SportEventNotFoundException, NoRatingsException;

    /**
     * Check the most active player. The player who has participated in the most
     * sporting events is returned. If there is a tie, the first who has registered
     * the most is provided. If none exists, an error will be shown.
     * @pre true.
     * @post returns the player with the most appearances in sporting events (most active).
     * If there is a tie, the first who has registered the most times is provided.
     * If none exists, the error will be reported.
     */
    public Player mostActivePlayer() throws PlayerNotFoundException;

    /**
     * Consult the sporting event best rated by the players. If it does
     * not exist, an error will be shown.
     * @pre true.
     * @post returns the highest rated event.
     * If no event exists, the error will be reported.
     *
     * @return the best sport event
     * @throws SportEventNotFoundException
     */
    public SportEvent bestSportEvent()  throws SportEventNotFoundException ;


    ///////////////////////////////////////////////////////////////////
    // PR2
    ///////////////////////////////////////////////////////////////////

    /**
     * Add a role
     * @pre true.
     * @post if the role code is new, the roles will be the same plus a new role
     * with the indicated data. If not, the role data will have been updated with the new ones.
     * @param roleId identifier
     * @param description the description
     */
    public void addRole(String roleId, String description);

    /**
     * Add a worker to the sports club
     * @param dni identifier
     * @param name the name
     * @param surname the surname
     * @param birthDay the birthday
     * @param roleId role identifier
     *
     * @pre the role exists
     * @post if the worker's ID is new, the workers will be the same plus one new worker,
     * the number of workers will be the same plus a new one and the number of workers in
     * a role will be the same plus a new one. If not, the worker's data will have been
     * updated with the new ones.
     */
    public void addWorker(String dni, String name, String surname, LocalDate birthDay, String roleId);

    /**
     * Assign a worker to a sporting event
     * @param dni identifier
     * @param eventId sport event
     *
     * @pre true.
     * @post if the worker is not assigned to the event, the event
     * workers will be the same plus a new one. If the worker is already
     * assigned to the event, an error will be displayed. If the worker or
     * event does not exist, the error will be reported.
     *
     * @throws WorkerNotFoundException if worker not found
     * @throws WorkerAlreadyAssignedException if worker already assigned
     * @throws SportEventNotFoundException if sport event not found
     */
    public void assignWorker(String dni, String eventId) throws WorkerNotFoundException, WorkerAlreadyAssignedException, SportEventNotFoundException;

    /**
     * Consult the workers assigned to a sporting event
     *
     * @pre true.
     * @post returns an iterator to loop through all the workers assigned to a
     * sporting event. If there are no workers or the event does
     * not exist, an error must be indicated
     *
     * @param eventId sport event
     * @return the workers assigned
     * @throws SportEventNotFoundException if sport event not found
     * @throws NoWorkersException if there are not workers
     */
    public Iterator<Worker> getWorkersBySportEvent(String eventId) throws SportEventNotFoundException, NoWorkersException;

    /**
     * Retrieves all the workers of the sports club with a certain role
     * @post returns an iterator to loop through all the workers
     * in a role. If there are no workers with that role, an error must be indicated
     *
     * @param roleId identifier
     * @return the workers
     * @throws NoWorkersException if there are no workers
     */
    public Iterator<Worker> getWorkersByRole(String roleId) throws NoWorkersException;

    /**
     * Consult the level of a player
     *
     * @param playerId identifier
     * @return the level
     * @throws PlayerNotFoundException if player not found
     * @pre true.
     * @post returns the level associated with the player. If the player
     * does not exist, an error must be indicated
     */
    public Level getLevel(String playerId) throws PlayerNotFoundException;

    /**
     * retrieves substitute players for a sporting event.
     * @pre true.
     * @post returns an iterator to loop through all substitute players
     * in an event. If the event does not exist or there are no substitute
     * players, an error should be indicated.
     *
     * @param eventId identifier
     * @return the substitutes
     * @throws SportEventNotFoundException if sport event not found
     * @throws NoSubstitutesException if there are no substitutes
     */
    public Iterator<Enrollment> getSubstitutes(String eventId) throws SportEventNotFoundException, NoSubstitutesException;

    /**
     * Add an attendee to a sporting event.
     *
     * @pre true.
     * @post if the number of attendees is less than the capacity of the
     * event, the number of attendees of the sporting event will be the
     * same plus one. In case the event capacity is full, an error will be
     * reported. If an attendee with the same phone number already exists or
     * the event does not exist, an error will be displayed.
     *
     * @param phone the phone
     * @param name the name
     * @param eventId sport event identifier
     * @throws AttenderAlreadyExistsException if attendee already exist
     * @throws SportEventNotFoundException if sport event not found
     * @throws LimitExceededException if exceed the number of people
     */
    public void addAttender(String phone, String name, String eventId) throws AttenderAlreadyExistsException, SportEventNotFoundException, LimitExceededException;

    /**
     * Get an attendee at a sporting event.
     *
     * @pre true.
     * @post returns the sporting event attendee associated
     * with the phone number. In the event that the sporting event does not exist or there is no
     * attendee associated with that telephone number, an error must be indicated
     *
     * @param phone the phone
     * @param sportEventId sport event identifier
     * @return the attendee
     * @throws SportEventNotFoundException if sport event not found
     * @throws AttenderNotFoundException if attendee not found
     */
    public Attender getAttender(String phone, String sportEventId) throws SportEventNotFoundException, AttenderNotFoundException;

    /**
     * Consult the attendees of a sporting event.
     *
     * @pre true.
     * @post returns an iterator with the attendees of the sporting
     * event. In the event that the sporting event does not exist or
     * there are no attendees, an error will be indicated.
     *
     * @param eventId sport event identifier
     * @return the attendees
     * @throws SportEventNotFoundException if sport event not found
     * @throws NoAttendersException if there are no attendees
     */
    public Iterator<Attender> getAttenders(String eventId) throws SportEventNotFoundException, NoAttendersException;

    /**
     * Consult the 5 organizing entities that bring the most attendees to the club
     *
     * @pre true.
     * @post returns an iterator to go through the 5 organizing entities
     * that have brought the most attendees to the sports club. In the
     * event that there is no organizer or event, or there is no
     * attendee, an error will be indicated
     *
     * @return the entities
     * @throws NoAttendersException if there are no attendees
     */
    public Iterator<OrganizingEntity> best5OrganizingEntities() throws NoAttendersException;

    /**
     * Check the event with the most attendees
     *
     * @pre true.
     * @post returns the sporting event with the most
     * attendees. If there is no event or there are no
     * attendees, an error will be indicated.
     *
     * @return the sport event
     * @throws NoSportEventsException if there are no sport events
     */
    public SportEvent bestSportEventByAttenders() throws NoSportEventsException;

    /**
     * Add a player as a follower of another
     *
     * @pre playerId is not a follower of playerFollowerId.
     * @post the number of followers of playerId will be the same plus a
     * new one and the number of followings of playerFollowerId will be the
     * same plus a new one. In the case that the player to follow or the followed
     * player does not exist, an error will be indicated.
     *
     * @param playerId the player
     * @param playerFollowerId the follower
     * @throws PlayerNotFoundException
     */
    public void addFollower(String playerId, String playerFollowerId) throws PlayerNotFoundException;

    /**
     * Consult the list of followers
     *
     * @pre true.
     * @post returns an iterator to traverse a
     * player's followers. In the case that the player does
     * not exist or does not have followers, an error will be indicated
     *
     * @param playerId the player
     * @return the followers
     * @throws PlayerNotFoundException if player not found
     * @throws NoFollowersException if there are no followers
     */
    public Iterator<Player> getFollowers(String playerId) throws PlayerNotFoundException, NoFollowersException;

    /**
     * Consult the list of players who are followed
     *
     * @pre true.
     * @post returns an iterator to loop through the
     * players a player is following. In the case that
     * the player does not exist or is not following
     * any players, an error will be indicated
     *
     * @param playerId the player
     * @return the followings
     * @throws PlayerNotFoundException if player not found
     * @throws NoFollowersException if there are no followins
     */
    public Iterator<Player> getFollowings(String playerId) throws PlayerNotFoundException, NoFollowingException;

    /**
     * Suggest Players To Follow
     *
     * @pre true.
     * @post returns an iterator to loop through suggested
     * players that are the followers of players that are followers
     * of playerId. In the event that the player does not exist or is
     * not followed by any players , an error will be indicated
     *
     * @param playerId the player
     * @return the players
     * @throws PlayerNotFoundException if player not found
     * @throws NoFollowersException if there are no followers
     */
    public Iterator<Player> recommendations(String playerId) throws PlayerNotFoundException, NoFollowersException;

    /**
     * Show the posts made by the players who are followed
     *
     * @pre true.
     * @post returns an iterator to traverse the posts of the
     * playerId's followeds. If the player does not exist or the
     * player’s followed do not have publications, an error will be indicated
     *
     * @param playerId
     * @return
     * @throws PlayerNotFoundException
     * @throws NoPostsException
     */
    public Iterator<Post> getPosts(String playerId) throws PlayerNotFoundException, NoPostsException ;

    ///////////////////////////////////////////////////////////////////
    // AUXILIARY OPERATIONS
    ///////////////////////////////////////////////////////////////////

    public int numPlayers();
    public int numOrganizingEntities();
    public int numFiles();
    public int numRejectedFiles();
    public int numPendingFiles();
    public int numSportEvents();
    public int numSportEventsByPlayer(String playerId);
    public int numPlayersBySportEvent(String sportEventId);
    public int numSportEventsByOrganizingEntity(String orgId);
    public  int numSubstitutesBySportEvent(String sportEventId);

    public Player getPlayer(String playerId);

    public SportEvent getSportEvent(String eventId);

    public OrganizingEntity getOrganizingEntity(String id);

    public File currentFile();

    public int numRoles();

    public Role getRole(String roleId);

    public int numWorkers();
    public Worker getWorker(String dni);

    public int numWorkersByRole(String roleId);

    public int numWorkersBySportEvent(String sportEventId);

    public int numRatings(String playerId);

    public int numAttenders(String sportEventId);

    public int numFollowers(String playerId);
    public int numFollowings(String playerId);

}
