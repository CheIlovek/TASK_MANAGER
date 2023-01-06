package ru.tskmngr.task_manager.models;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity(name = "authority")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String authority;

    public Authority(long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public Authority() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
