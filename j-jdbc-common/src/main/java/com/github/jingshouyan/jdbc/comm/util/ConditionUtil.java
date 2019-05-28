package com.github.jingshouyan.jdbc.comm.util;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.bean.Between;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jingshouyan
 * 11/27/18 4:50 PM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConditionUtil {

    private static final String DELETE_FIELD_NAME = "deletedAt";

    public static ConditionUtil newInstance(){
        return new ConditionUtil();
    }

    private List<Condition> conditions = new ArrayList<>();

    public List<Condition> conditions(){
        return conditions;
    }

    private Condition last;

    public ConditionUtil field(String field){
        last = new Condition();
        conditions.add(last);
        last.setField(field);
        return this;
    }
    public ConditionUtil like(String like){
        last.setLike(like);
        return this;
    }
    public ConditionUtil gt(Object gt){
        last.setGt(gt);
        return this;
    }
    public ConditionUtil lt(Object lt){
        last.setLt(lt);
        return this;
    }
    public ConditionUtil gte(Object gte){
        last.setGte(gte);
        return this;
    }
    public ConditionUtil lte(Object lte){
        last.setLte(lte);
        return this;
    }
    public ConditionUtil eq(Object eq){
        last.setEq(eq);
        return this;
    }
    public ConditionUtil ne(Object ne){
        last.setNe(ne);
        return this;
    }
    public ConditionUtil in(Collection<?> in){
        last.setIn(in);
        return this;
    }
    public ConditionUtil notIn(Collection<?> notIn){
        last.setNotIn(notIn);
        return this;
    }
    public ConditionUtil empty(boolean empty){
        last.setEmpty(empty);
        return this;
    }

    public ConditionUtil between(Object start,Object end) {
        last.setBetween(new Between(start,end));
        return this;
    }


    public ConditionUtil noDeleted(){
        return field(DELETE_FIELD_NAME).eq(Constant.NO_DELETE);
    }

    public ConditionUtil deleted(){
        return field(DELETE_FIELD_NAME).ne(Constant.NO_DELETE);
    }
}
