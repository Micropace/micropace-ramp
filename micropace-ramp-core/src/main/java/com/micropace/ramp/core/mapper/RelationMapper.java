package com.micropace.ramp.core.mapper;

import com.micropace.ramp.base.common.IMyBatisSuperMapper;
import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.entity.CUser;
import com.micropace.ramp.base.entity.Relation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RelationMapper extends IMyBatisSuperMapper<Relation> {

    @Select("select * from relation where id_buser=#{idBuser} and id_cuser=#{idCuser}")
    Relation selectByBindedIds(@Param("idBuser") Long idBuser, @Param("idCuser") Long idCuser);

    /**
     * 查询指定C用户关注的B用户列表
     *
     * @param idCuser C用户ID
     * @return B用户列表
     */
    @Select("select * from b_user\n" +
            "    right join relation\n" +
            "        on relation.id_buser = b_user.id\n" +
            "    left join c_user\n" +
            "        on relation.id_cuser = c_user.id\n" +
            "    where c_user.id=#{idCuser}")
    List<BUser> selectFollowedBusers(@Param("idCuser") Long idCuser);

    /**
     * 查询B用户的关注者C用户列表
     *
     * @param idBuser B用户ID
     * @return C用户列表
     */
    @Select("select * from c_user\n" +
            "    right join relation\n" +
            "        on relation.id_buser = c_user.id\n" +
            "    left join b_user\n" +
            "        on relation.id_cuser = b_user.id\n" +
            "    where b_user.id=#{idBuser}")
    List<CUser> selectFollowers(@Param("idBuser") Long idBuser);
}
