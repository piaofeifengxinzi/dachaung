package com.example.demo.Repository;

import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findUserBynickName(String nickName);
//    @Modifying
//    @Query("UPDATE user SET id_number = :id_number,name = : name , address = :address , phone_number = :phone_number WHERE nick_name = :nick_name")
//    int updateUser(@Param("id_number") String id_number,
//                   @Param("name") String name,
//                   @Param("address") String address,
//                   @Param("phone_number") String phone_number,
//                   @Param("nick_name") String nick_name);
}
