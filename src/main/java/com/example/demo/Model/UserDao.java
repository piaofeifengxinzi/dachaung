package com.example.demo.Model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface UserDao extends CrudRepository<DAOUser, Integer> {
    DAOUser findByUsername(String username);
//    @Modifying
//    @Query("UPDATE user_log SET type = :type WHERE name = :name")
//    int updateType(@Param("type") String type, @Param("name") String name);
}
