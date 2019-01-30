package com.jingshouyan.bean;

import com.github.jingshouyan.jdbc.comm.annotaion.Column;
import com.github.jingshouyan.jdbc.comm.annotaion.Index;
import com.github.jingshouyan.jdbc.comm.annotaion.Key;
import com.github.jingshouyan.jdbc.comm.annotaion.Table;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.comm.bean.EncryptType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jingshouyan
 * 11/29/18 5:20 PM
 */
@Getter@Setter@ToString
//表注解,指定表明,不加默认为类名
@Table(value = "DEMO_USER",comment = "用户表")
@Index({"age","nickname"})
@Index({"id","name"})
public class UserDO extends BaseDO {
    //主键注解,主键只能是Long/String,且只能有一个,若没有主键 find/findByIds 方法不能使用
    //默认当未设值(为 null)时,只用id生成器自动设值
    @Key(generatorIfNotSet = true)
    //列注解,设置列长度
    @Column(length = 50,comment = "主键")
    private String id;
    // 列注解,选择加密方式为属性id的值为key
    // 使用加密后,无法作为查询条件,因为数据库中存放的是密文
    @Column(encryptType = EncryptType.FLIED,encryptKey = "id")
    private String name;
    //列类型使用包装类型,因为在更新操作时 null 值不更新
    private Integer age;
    //列注解,选择加密方式为固定值abcd
    //数据以json格式入库,本例为 json 字符串加密后存储,未来可能移除json同时又是加密字段的模式
    @Column(encryptType = EncryptType.FIXED,encryptKey = "abcd",json = true,length = 1000)
    private List<String> tags;
    //设置数据库字段名,默认为属性名
    @Column(value = "NICK_NAME_TT4")
    private String nickname;


    /**
     * 当使用string类型主键时,主键前缀
     * @return 主键前缀
     */
    @Override
    public String idPrefix() {
        return "U";
    }

    /**
     * 当使用string类型主键时,主键后缀
     * @return 主键后缀
     */
    @Override
    public String idSuffix() {
        return "@abc";
    }
}
