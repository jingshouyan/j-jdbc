package com.jingshouyan.bean;

import com.github.jingshouyan.jdbc.comm.annotaion.Foreign;
import com.github.jingshouyan.jdbc.comm.annotaion.Key;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.jingshouyan.bean.UserDO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2019/3/7 14:51
 */
@Data
public class ForeignDO extends BaseDO {
    @Key
    String id;
    String userId;
    @Foreign(thisKey = "userId",thatKey = "id")
    List<UserDO> users;
    @Foreign(thisKey = "userId",thatKey = "id")
    ArrayList<UserDO> u2;
    @Foreign(thisKey = "userId",thatKey = "id")
    UserDO u ;
}
