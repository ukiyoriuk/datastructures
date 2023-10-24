package uoc.ds.pr.model;

import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;

import java.util.Objects;

public class Role {
    private String roleId;
    private String description;
    private List<Worker> workers;

    public Role(String roleId, String description) {
        this.setRoleId(roleId);
        this.setDescription(description);
        workers = new LinkedList<>();
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getDescription() {
        return description;
    }

    public boolean is(String id) {
        return roleId.equals(id);
    }

    public Iterator<Worker> getWorkers() {
        return workers.values();
    }

    public void addWorker(Worker worker) {
        workers.insertEnd(worker);
    }

    public int getNumWorkers() {
        return workers.size();
    }

    public void removeWorker(Worker worker) {
        Traversal<Worker> tr = workers.positions();
        boolean found = false;

        while(tr.hasNext() && !found) {
            Position<Worker> p = tr.next();
            Worker w = tr.next().getElem();
            if (Objects.equals(w.getDni(), worker.getDni())) {
                workers.delete(p);
                found = true;
            }
        }
    }

}
