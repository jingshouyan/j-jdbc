package com.github.jingshouyan.jdbc.core.mapper;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.EncryptType;
import com.github.jingshouyan.jdbc.comm.bean.TableInfo;
import com.github.jingshouyan.jdbc.core.encryption.EncryptionProvider;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.github.jingshouyan.jdbc.core.util.json.JsonUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author jingshouyan
 * 11/27/18 3:24 PM
 */
public class BeanRowMapper<T> implements RowMapper<T>,Constant {
    private TableInfo tableInfo;
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();
    @Getter
    private Class<T> mappedClass;
    public BeanRowMapper(Class<T> mappedClass){
        init(mappedClass);
    }

    private void init(Class<T> mappedClass){
        this.mappedClass = mappedClass;
        this.tableInfo = TableUtil.tableInfo(mappedClass);
    }


    @Override
    public T mapRow(ResultSet rs, int i) throws SQLException {
        assert this.mappedClass != null :"Mapped class was not specified";
        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        initBeanWrapper(bw);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<ColumnInfo,Integer> map = Maps.newHashMap();
        for (int j = 1; j <= columnCount; j++) {
            String column = JdbcUtils.lookupColumnName(rsmd, j).toLowerCase();
            ColumnInfo columnInfo = tableInfo.getLowerCaseColumnMap().get(column);
            //数据库中表字段在实体中没有对应的字段
            if (columnInfo == null) {
                continue;
            }
            map.put(columnInfo,j);
        }
        for (ColumnInfo columnInfo: map.keySet()){
            Object dbValue = dbValue(map,rs,columnInfo);
            if(dbValue != null){
                bw.setPropertyValue(columnInfo.getFieldName(), dbValue);
            }
        }
        return mappedObject;
    }

    private Object dbValue(Map<ColumnInfo,Integer> map,ResultSet rs,ColumnInfo columnInfo) throws SQLException{
        Object value = null;
        Integer index = map.get(columnInfo);
        if(null == index){
            return null;
        }
        Class<?> clazz = String.class;
        if(!columnInfo.isJson() || !columnInfo.isEncrypt()){
            clazz = columnInfo.getField().getType();
        }
        value = JdbcUtils.getResultSetValue(rs, index, clazz);
        if(null == value){
            return null;
        }
        if(columnInfo.isEncrypt()){
            String password = null;
            if(columnInfo.getEncryptType() == EncryptType.FIXED){
                password = columnInfo.getEncryptKey();
            } else if(columnInfo.getEncryptType() == EncryptType.FLIED){
                String fieldName = columnInfo.getEncryptKey();
                ColumnInfo pwdColumn = tableInfo.getFieldNameMap().get(fieldName);
                Object pwd = dbValue(map,rs,pwdColumn);
                if(pwd != null){
                    password = String.valueOf(pwd);
                }
            }
            Preconditions.checkNotNull(password,"encryptType/decrypt password is null");
            value = EncryptionProvider.decrypt(value.toString(),password);
        }
        if(columnInfo.isJson()){
            try {
                value = JsonUtil.toBean(value.toString(),columnInfo.getField().getGenericType());
            }catch (Exception e){
                throw new SQLException(e);
            }
        }
        return value;
    }

    private void initBeanWrapper(BeanWrapper beanWrapper){
        beanWrapper.setConversionService(this.conversionService);
    }

}

