package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "user_log")
public class DAOUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    //这个username和user表的nickname一样，但是，咱不提供更改昵称服务，想改昵称，吃屎去吧
    private String username;
    @Column
    @JsonIgnore
    private String password;
    @Column
    //当前用户登录角色，影响权限,收件人，发件人，快递员分别用0，1，2代表
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
