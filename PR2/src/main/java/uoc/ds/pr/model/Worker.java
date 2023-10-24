package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.List;

import java.time.LocalDate;

public class Worker {
    private String dni;
    private String name;
    private String surname;
    private LocalDate birthday;
    private String roleId;

    public Worker(String dni, String name, String surname, LocalDate birthday, String roleId) {
        this.setDni(dni);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
        this.setRoleId(roleId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDni() {
        return dni;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getRoleId() {
        return roleId;
    }

    public boolean is(String workerDni) {
        return dni.equals(workerDni);
    }

}
