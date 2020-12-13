package com.example.demo.Repository;

import com.example.demo.Model.Goods;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GoodsRepository extends CrudRepository<Goods, Integer> {

     //这里贼坑，这里居然是通过函数的名字中的包含的属性来智能的决定后面的参数是什么，fuck，不过是真的好用，是真的香
     Goods findGoodByGuid(String guid);
//    @Modifying
//    @Query("select * from goods g where uuid = :uuid")
//    List<Goods> findGoodsByUuid(@Param("uuid") String uuid);
}
