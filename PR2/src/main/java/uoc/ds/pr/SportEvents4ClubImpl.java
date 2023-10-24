package uoc.ds.pr;

import java.time.LocalDate;
import java.util.Objects;

import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.nonlinear.graphs.*;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.adt.sequential.Queue;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;
import uoc.ds.pr.util.OrderedVector;


public class SportEvents4ClubImpl implements SportEvents4Club {

    private HashTable<String, OrganizingEntity> organizingEntities;
    private Dictionary<String, SportEvent> sportEvents;
    private OrderedVector<SportEvent> bestSportEvent;
    private SportEvent bestSportEventByAttenders;
    private HashTable<String, Worker> workers;
    private Role[] roles;
    private Queue<File> files;
    private Dictionary<String, Player> players;
    private Player mostActivePlayer;
    private OrderedVector<OrganizingEntity> bestOrganizingEntities;
    private int numPlayers;
    private int numOrganizingEntities;
    private int totalFiles;
    private int rejectedFiles;
    private int numRoles;
    private DirectedGraphImpl<Player, String> socialNetwork;

    public SportEvents4ClubImpl() {
        players = new DictionaryAVLImpl<>();
        sportEvents = new DictionaryAVLImpl<>();
        bestSportEvent = new OrderedVector<>(MAX_NUM_SPORT_EVENTS, SportEvent.CMP_V);
        numPlayers = 0;
        organizingEntities = new HashTable<>(MAX_NUM_ORGANIZING_ENTITIES);
        numOrganizingEntities = 0;
        files = new PriorityQueue<>();
        totalFiles = 0;
        rejectedFiles = 0;
        mostActivePlayer = null;
        bestSportEventByAttenders = null;
        roles = new Role[MAX_ROLES];
        numRoles = 0;
        workers = new HashTable<>();
        bestOrganizingEntities = new OrderedVector<>(MAX_ORGANIZING_ENTITIES_WITH_MORE_ATTENDERS, OrganizingEntity.CMP_V);
        socialNetwork = new DirectedGraphImpl<>();
    }


    public void addPlayer(String playerId, String name, String surname, LocalDate birthday) {
        Player u = getPlayer(playerId);
        if (u != null) {
            u.setName(name);
            u.setSurname(surname);
            u.setBirthday(birthday);
        } else {
            u = new Player(playerId, name, surname, birthday);
            players.put(playerId, u);
            numPlayers++;
        }
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public void addOrganizingEntity(String id, String name, String description) {
        OrganizingEntity organizingEntity = getOrganizingEntity(id);
        if (organizingEntity != null) {
            organizingEntity.setName(name);
            organizingEntity.setDescription(description);
        } else {
            organizingEntity = new OrganizingEntity(id, name, description);
            organizingEntities.put(id, organizingEntity);
            numOrganizingEntities++;
        }
    }

    public OrganizingEntity getOrganizingEntity(String organizationId) {
        return organizingEntities.get(organizationId);
    }

    public void addFile(String id, String eventId, String orgId, String description,
                        Type type, byte resources, int max, LocalDate startDate, LocalDate endDate) throws OrganizingEntityNotFoundException {
        OrganizingEntity organization = getOrganizingEntity(orgId);
        if (organization == null) {
        	throw new OrganizingEntityNotFoundException();
        }

        files.add(new File(id, eventId, description, type, startDate, endDate, resources, max, organization));
        totalFiles++;
    }

    public File updateFile(Status status, LocalDate date, String description) throws NoFilesException {
        File file = files.poll();
        if (file  == null) {
        	throw new NoFilesException();
        }

        file.update(status, date, description);
        if (file.isEnabled()) {
            SportEvent sportEvent = file.newSportEvent();
            sportEvents.put(sportEvent.getEventId(), sportEvent);
        }
        else {
        	rejectedFiles++;
        }

        return file;
    }

    @Override
    public void signUpEvent(String playerId, String eventId) throws PlayerNotFoundException, SportEventNotFoundException, LimitExceededException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        player.addEvent(sportEvent);
        if (!sportEvent.isFull()) {
            sportEvent.addEnrollment(player);
        } else {
            sportEvent.addEnrollmentAsSubstitute(player);
            throw new LimitExceededException();
        }

        updateMostActivePlayer(player);
    }

    public File currentFile() {
        return (files.size() > 0 ? files.peek() : null);
    }

    public Iterator<SportEvent> getSportEventsByOrganizingEntity(String organizationId) throws NoSportEventsException {
        OrganizingEntity organizingEntity = getOrganizingEntity(organizationId);

        if (organizingEntity==null || !organizingEntity.hasActivities()) {
        	throw new NoSportEventsException();
        }
        return organizingEntity.sportEvents();
    }

    @Override
    public Iterator<SportEvent> getAllEvents() throws NoSportEventsException{
        Iterator<SportEvent> it = sportEvents.values();
        if (!it.hasNext()) throw new NoSportEventsException();
        return it;
    }

    @Override
    public Iterator<SportEvent> getEventsByPlayer(String playerId) throws NoSportEventsException {
        Player player = getPlayer(playerId);
        if (player==null || !player.hasEvents()) {
            throw new NoSportEventsException();
        }

        return player.getEvents();
    }

    public double getRejectedFiles() {
        return (double) rejectedFiles / totalFiles;
    }

    public void addRating(String playerId, String eventId, Rating rating, String message)
            throws SportEventNotFoundException, PlayerNotFoundException, PlayerNotInSportEventException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
        	throw new SportEventNotFoundException();
        }

        Player player = getPlayer(playerId);
        if (player == null) {
        	throw new PlayerNotFoundException();
        }

        if (!player.isInSportEvent(eventId)) {
        	throw new PlayerNotInSportEventException();
        }

        sportEvent.addRating(rating, message, player);
        player.incNumRatings();
        updateBestSportEvent(sportEvent);
    }

    private void updateBestSportEvent(SportEvent sportEvent) {
        bestSportEvent.delete(sportEvent);
        bestSportEvent.update(sportEvent);
    }

    public Iterator<uoc.ds.pr.model.Rating> getRatingsByEvent(String eventId) throws SportEventNotFoundException, NoRatingsException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent  == null) {
        	throw new SportEventNotFoundException();
        }

        if (!sportEvent.hasRatings()) {
        	throw new NoRatingsException();
        }

        return sportEvent.ratings();
    }

    private void updateMostActivePlayer(Player player) {
        if (mostActivePlayer == null) {
            mostActivePlayer = player;
        }
        else if (player.numSportEvents() > mostActivePlayer.numSportEvents()) {
            mostActivePlayer = player;
        }
    }

    public Player mostActivePlayer() throws PlayerNotFoundException {
        if (mostActivePlayer == null) {
        	throw new PlayerNotFoundException();
        }

        return mostActivePlayer;
    }

    public SportEvent bestSportEvent() throws SportEventNotFoundException {
        if (bestSportEvent.size() == 0) {
        	throw new SportEventNotFoundException();
        }

        return bestSportEvent.elementAt(0);
    }

    public int numPlayers() {
        return numPlayers;
    }

    public int numOrganizingEntities() {
        return numOrganizingEntities;
    }

    public int numPendingFiles() {
        return files.size();
    }

    public int numFiles() {
        return totalFiles;
    }

    public int numRejectedFiles() {
        return rejectedFiles;
    }

    public int numSportEvents() {
        return sportEvents.size();
    }

    public int numSportEventsByPlayer(String playerId) {
        Player player = getPlayer(playerId);

        return (player!=null?player.numEvents():0);
    }

    public int numPlayersBySportEvent(String sportEvenId) {
        SportEvent sportEvent = getSportEvent(sportEvenId);

        return (sportEvent!=null?sportEvent.numPlayers(): 0);
    }


    public int numSportEventsByOrganizingEntity(String organizationId) {
        OrganizingEntity oe = getOrganizingEntity(organizationId);

        return oe.numEvents();
    }


    public  int numSubstitutesBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);

        return (sportEvent!=null?sportEvent.getNumSubstitutes():0);
    }

    public SportEvent getSportEvent(String eventId) {
        return sportEvents.get(eventId);
    }

    ///////////////////////////////////////////////////////////////////
    // PR2
    ///////////////////////////////////////////////////////////////////

    @Override
    public void addRole(String roleId, String description) {
        Role r = getRole(roleId);
        if (r != null) {
            r.setRoleId(roleId);
            r.setDescription(description);
        } else {
            r = new Role(roleId, description);
            roles[numRoles++] = r;
        }
    }

    @Override
    public void addWorker(String dni, String name, String surname, LocalDate birthDay, String roleId) {
        Worker w = getWorker(dni);
        Role r = getRole(roleId);
        if (w != null) {
            w.setDni(dni);
            w.setName(name);
            w.setSurname(surname);
            w.setBirthday(birthDay);
            if (!Objects.equals(w.getRoleId(), roleId)) {
                Role oldRole = getRole(w.getRoleId());
                oldRole.removeWorker(w);
                w.setRoleId(roleId);
                r.addWorker(w);

            }
        } else {
            w = new Worker(dni, name, surname, birthDay, roleId);
            workers.put(w.getDni(), w);
            r.addWorker(w);
        }
    }

    @Override
    public void assignWorker(String dni, String eventId) throws WorkerNotFoundException, WorkerAlreadyAssignedException, SportEventNotFoundException {
        SportEvent s = getSportEvent(eventId);
        Worker w = getWorker(dni);

        if (s == null) {
            throw new SportEventNotFoundException();
        } else {
            if(w == null) {
                throw new WorkerNotFoundException();
            } else if (s.isWorkerInSportEvent(dni)) {
                throw new WorkerAlreadyAssignedException();
            }
        }

        s.addWorker(w);
    }

    @Override
    public Iterator<Worker> getWorkersBySportEvent(String eventId) throws SportEventNotFoundException, NoWorkersException {
        SportEvent sportEvent = getSportEvent(eventId);

        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }

        if (!sportEvent.getWorkers().hasNext()) {
            throw new NoWorkersException();
        }

        return sportEvent.getWorkers();
    }

    @Override
    public Iterator<Worker> getWorkersByRole(String roleId) throws NoWorkersException {
        Role role = getRole(roleId);

        if (role.getNumWorkers() == 0) {
            throw new NoWorkersException();
        }

        return role.getWorkers();
    }

    @Override
    public Level getLevel(String playerId) throws PlayerNotFoundException {
        Player player = getPlayer(playerId);

        if (player == null) {
            throw new PlayerNotFoundException();
        }

        return player.getLevel();
    }

    @Override
    public Iterator<Enrollment> getSubstitutes(String eventId) throws SportEventNotFoundException, NoSubstitutesException {
        SportEvent se = getSportEvent(eventId);

        if (se == null) {
            throw new SportEventNotFoundException();
        }

        if (se.getNumSubstitutes() == 0) {
            throw new NoSubstitutesException();
        }

        return se.getSubstitutes();
    }

    @Override
    public void addAttender(String phone, String name, String eventId) throws AttenderAlreadyExistsException, SportEventNotFoundException, LimitExceededException {
        SportEvent se = getSportEvent(eventId);
        Attender attender = new Attender(phone, name, eventId);

        if (se == null) {
            throw new SportEventNotFoundException();
        }

        int totalPeople = se.numAttenders() + se.numPlayers();
        Iterator<Attender> it = se.getAttenders();

        while(it.hasNext()) {
            Attender att = it.next();
            if(Objects.equals(att.getPhone(), attender.getPhone())) {
                throw new AttenderAlreadyExistsException();
            }
        }

        if(totalPeople == se.getMax()) {
            throw new LimitExceededException();
        } else {
            se.addAttender(phone, attender);
        }
    }

    @Override
    public Attender getAttender(String phone, String sportEventId) throws SportEventNotFoundException, AttenderNotFoundException {
        SportEvent sportEvent = getSportEvent(sportEventId);

        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }

        if (sportEvent.getAttender(phone) == null) {
            throw new AttenderNotFoundException();
        }

        return sportEvent.getAttender(phone);
    }

    @Override
    public Iterator<Attender> getAttenders(String eventId) throws SportEventNotFoundException, NoAttendersException {
        SportEvent sportEvent = getSportEvent(eventId);

        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }

        if(sportEvent.numAttenders() == 0) {
            throw new NoAttendersException();
        }

        return sportEvent.getAttenders();
    }

    @Override
    public Iterator<OrganizingEntity> best5OrganizingEntities() throws NoAttendersException {
        Iterator<OrganizingEntity> it = organizingEntities.values();
        int totalAttenders = 0;

        while(it.hasNext()) {
            OrganizingEntity oe = it.next();
            if (oe.numAttenders() != 0) {
                totalAttenders += oe.numAttenders();
                bestOrganizingEntities.delete(oe);
                bestOrganizingEntities.update(oe);
            }
        }

        if (totalAttenders == 0) {
            throw new NoAttendersException();
        }

        return bestOrganizingEntities.values();
    }

    @Override
    public SportEvent bestSportEventByAttenders() throws NoSportEventsException {
        Iterator<SportEvent> it = sportEvents.values();
        bestSportEventByAttenders = it.next();

        if (sportEvents.isEmpty()) {
            throw new NoSportEventsException();
        }

        while(it.hasNext()) {
            SportEvent se = it.next();
            if (se.numAttenders() > bestSportEventByAttenders.numAttenders()) {
                bestSportEventByAttenders = se;
            }
        }

        return bestSportEventByAttenders;
    }

    @Override
    public void addFollower(String playerId, String playerFollowerId) throws PlayerNotFoundException {
        Player p = getPlayer(playerId);
        Player follower = getPlayer(playerFollowerId);

        if (p == null || follower == null) {
            throw new PlayerNotFoundException();
        }

        Vertex<Player> vP = socialNetwork.getVertex(p);
        Vertex<Player> vFollower = socialNetwork.getVertex(follower);

        if (socialNetwork.getVertex(p) == null) {
            vP = socialNetwork.newVertex(p);
        }

        if (socialNetwork.getVertex(follower) == null) {
            vFollower = socialNetwork.newVertex(follower);
        }

        Edge<String, Player> edge1a = socialNetwork.newEdge(vP, vFollower);
        edge1a.setLabel("follower");
        Edge<String, Player> edge1b = socialNetwork.newEdge(vFollower, vP);
        edge1b.setLabel("following");
        p.incNumFollowers();
        follower.incNumFollowings();
    }

    @Override
    public Iterator<Player> getFollowers(String playerId) throws PlayerNotFoundException, NoFollowersException {
        Player p = getPlayer(playerId);

        if (p == null) {
            throw new PlayerNotFoundException();
        }

        DirectedVertexImpl<Player, String> _vPlayer = (DirectedVertexImpl<Player, String>) socialNetwork.getVertex(p);
        if (p.numFollowers() == 0 || _vPlayer == null) {
            throw new NoFollowersException();
        }

        Iterator<Edge<String, Player>> it = _vPlayer.edges();
        List<Player> followers = new LinkedList<>();

        while(it.hasNext()) {
            DirectedEdge<String, Player> edge = (DirectedEdge<String, Player>)it.next();
            if (Objects.equals(edge.getLabel(), "follower" ) && edge.getVertexSrc().getValue() == p) {
                Player follower = edge.getVertexDst().getValue();
                followers.insertEnd(follower);
            }
        }

        return followers.values();
    }

    @Override
    public Iterator<Player> getFollowings(String playerId) throws PlayerNotFoundException, NoFollowingException {
        Player p = getPlayer(playerId);

        if (p == null) {
            throw new PlayerNotFoundException();
        }

        DirectedVertexImpl<Player, String> _vPlayer = (DirectedVertexImpl<Player, String>) socialNetwork.getVertex(p);
        if (_vPlayer == null || p.numFollowings() == 0) {
            throw new NoFollowingException();
        }
        Iterator<Edge<String, Player>> it = _vPlayer.edges();
        List<Player> followings = new LinkedList<>();

        while(it.hasNext()) {
            DirectedEdge<String, Player> edge = (DirectedEdge<String, Player>)it.next();
            if (Objects.equals(edge.getLabel(), "following") && edge.getVertexSrc().getValue() == p) {
                Player following = edge.getVertexDst().getValue();
                followings.insertEnd(following);
            }
        }

        return followings.values();
    }

    @Override
    public Iterator<Player> recommendations(String playerId) throws PlayerNotFoundException, NoFollowersException {
        Player p = getPlayer(playerId);
        boolean found = false;

        if (p == null) {
            throw new PlayerNotFoundException();
        }

        //Vertex Principal (P)
        DirectedVertexImpl<Player, String> _vPlayer = (DirectedVertexImpl<Player, String>) socialNetwork.getVertex(p);
        if (p.numFollowers() == 0 || _vPlayer == null) {
            throw new NoFollowersException();
        }

        //Edges de Vertex Principal (P)---
        Iterator<Edge<String, Player>> it = _vPlayer.edges();
        List<Player> followers = new LinkedList<>();

        while(it.hasNext()) {
            //Primer Edge de Vertex Principal (P)---
            DirectedEdge<String, Player> edge = (DirectedEdge<String, Player>)it.next();
            //if Edge = follower && SRC = Player Principal
            if (Objects.equals(edge.getLabel(), "follower" ) && edge.getVertexSrc().getValue() == p) {
                //Vertex follower de Principal (P)---(F)
                DirectedVertexImpl<Player, String> _vFollowerPlayer = (DirectedVertexImpl<Player, String>) socialNetwork.getVertex(edge.getVertexDst().getValue());
                //Edges de Vertex Follower (P)---(F)---
                Iterator<Edge<String, Player>> it2 = _vFollowerPlayer.edges();

                while(it2.hasNext()) {
                    //(P)---(F)---
                    DirectedEdge<String, Player> edge2 = (DirectedEdge<String, Player>)it2.next();
                    //(F)
                    Player follower = edge2.getVertexSrc().getValue();
                    //if Edge2 = follower && SRC = Player F
                    if (Objects.equals(edge2.getLabel(), "follower" ) && edge2.getVertexSrc().getValue() == follower) {
                        //Vertex follower de follower de Principal (P)---(F)---(FF)
                        Player follower2 = edge2.getVertexDst().getValue();
                        //Iterator del Vertex Principal para comprobar si el follower ya existe en este
                        DirectedVertexImpl<Player, String> _vPlayerSearch = (DirectedVertexImpl<Player, String>) socialNetwork.getVertex(p);
                        Iterator<Edge<String, Player>> itSearch = _vPlayerSearch.edges();
                        while(itSearch.hasNext()) {
                            DirectedEdge<String, Player> edge3 = (DirectedEdge<String, Player>)itSearch.next();
                            if (follower2 == edge3.getVertexDst().getValue()) {
                                found = true;
                            }
                        }

                        if (follower2 != p && !found) {
                            if (!followers.isEmpty()) {
                                Iterator<Player> itFollowers = followers.values();
                                while(itFollowers.hasNext()) {
                                    Player pFollower = itFollowers.next();
                                    if (Objects.equals(follower2.getId(), pFollower.getId())) {
                                        found = true;
                                    }
                                }
                            }
                        }

                        if (!found) {
                            followers.insertEnd(follower2);
                        }

                        found = false;
                    }
                }

            }
        }

        return followers.values();
    }

    @Override
    public Iterator<Post> getPosts(String playerId) throws PlayerNotFoundException, NoPostsException {
        /*Player p = getPlayer(playerId);

        if (p == null) {
            throw new PlayerNotFoundException();
        }

        DirectedVertexImpl<Player, String> _vPlayer = (DirectedVertexImpl<Player, String>) socialNetwork.getVertex(p);
        if (_vPlayer == null || p.numFollowings() == 0) {
            throw new NoPostsException();
        }

        Iterator<Edge<String, Player>> it = _vPlayer.edges();
        List<Post> followings = new LinkedList<>();

        while(it.hasNext()) {
            DirectedEdge<String, Player> edge = (DirectedEdge<String, Player>)it.next();
            if (Objects.equals(edge.getLabel(), "following") && edge.getVertexSrc().getValue() == p) {
                Player following = edge.getVertexDst().getValue();
                System.out.println(following.getId());
                if (following.getMessage() != null) {
                    //System.out.println("LLEGA AQUI???");
                    Post post = new Post(following, following.getMessage());
                    followings.insertEnd(post);
                }
            }
        }

        return followings.values();*/
        return null;
    }

    ///////////////////////////////////////////////////////////////////
    // AUXILIARY OPERATIONS PR2
    ///////////////////////////////////////////////////////////////////
    @Override
    public int numRoles() {
        return numRoles;
    }

    @Override
    public Role getRole(String roleId) {
        for (Role r : roles) {
            if (r == null) {
                return null;
            } else if (r.is(roleId)){
                return r;
            }
        }
        return null;
    }

    @Override
    public int numWorkers() {
        return workers.size();
    }

    @Override
    public Worker getWorker(String dni) {
        return workers.get(dni);
    }

    @Override
    public int numWorkersByRole(String roleId) {
        Role role = getRole(roleId);
        return role.getNumWorkers();
    }

    @Override
    public int numWorkersBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);
        return sportEvent.getNumWorkers();
    }

    @Override
    public int numRatings(String playerId) {
        Player p = getPlayer(playerId);
        return p.getNumRatings();
    }

    @Override
    public int numAttenders(String sportEventId) {
        SportEvent se = getSportEvent(sportEventId);
        return (se!=null?se.numAttenders():0);
    }

    @Override
    public int numFollowers(String playerId) {
        Player p = getPlayer(playerId);
        return (p!=null?p.numFollowers():0);
    }

    @Override
    public int numFollowings(String playerId) {
        Player p = getPlayer(playerId);
        return (p!=null?p.numFollowings():0);
    }


}
