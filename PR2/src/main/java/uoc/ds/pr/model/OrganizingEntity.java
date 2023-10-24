package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;

import java.util.Comparator;

public class OrganizingEntity implements Comparable<OrganizingEntity> {
    public static final Comparator<OrganizingEntity> CMP_V = (oe1, oe2)->Double.compare(oe1.numAttenders(), oe2.numAttenders());
    private String organizationId;
    private String description;
    private String name;
    private List<SportEvent> events;

    public OrganizingEntity(String organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        events = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterator<SportEvent> activities() {
        return events.values();
    }

    public void addEvent(SportEvent sportEvent) {
        events.insertEnd(sportEvent);
    }

    public int numEvents() {
        return events.size();
    }

    public boolean hasActivities() {
        return events.size() > 0;
    }

    public Iterator<SportEvent> sportEvents() {
        return events.values();
    }

    public int compareTo(OrganizingEntity oe2) {
        return Double.compare(numAttenders(), oe2.numAttenders());
    }

    public int numAttenders() {
        int totalAttenders = 0;
        Iterator<SportEvent> it = events.values();
        while(it.hasNext()) {
            SportEvent se = it.next();
            totalAttenders += se.numAttenders();
        }
        return totalAttenders;
    }
}
