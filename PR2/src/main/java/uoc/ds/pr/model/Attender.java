package uoc.ds.pr.model;

public class Attender {
    private String phone;
    private String name;
    private String eventId;

    public Attender(String phone, String name, String eventId) {
        this.setPhone(phone);
        this.setName(name);
        this.setEventId(eventId);
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getEventId() {
        return eventId;
    }
}
