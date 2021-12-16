package it.polimi.db2_project_20212022_fontana_gerosa.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    private int employeeId;
    private String email;
    private String password;

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }


    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
