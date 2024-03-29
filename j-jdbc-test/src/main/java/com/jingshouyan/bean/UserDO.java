package com.jingshouyan.bean;

import com.github.jingshouyan.jdbc.comm.annotation.*;
import com.github.jingshouyan.jdbc.comm.bean.DataType;
import com.github.jingshouyan.jdbc.comm.bean.EncryptType;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author jingshouyan
 * 11/29/18 5:20 PM
 */
@Getter
@Setter
@ToString(callSuper = true)
@Table(value = "DEMO_USER", comment = "用户表")
@Index(value = {"age", "nickname"})
@Index({"id", "name"})
public class UserDO extends BaseDO {

    /**
     * 主键注解,主键只能是Long/String,且只能有一个,若没有主键 find/findByIds 方法不能使用
     * 默认当未设值(为 null)时,只用id生成器自动设值
     * 列注解,设置列长度
     */
    @Key(generatorIfNotSet = true)
    @Column(comment = "主键")
    private String id;
    /**
     * 列注解,选择加密方式为属性id的值为key
     * 使用加密后,无法作为查询条件,因为数据库中存放的是密文
     */
    @Column(encryptType = EncryptType.FLIED, encryptKey = "id", immutable = true, router = true)
//    @Index(unique = true)
    private String name;
    /**
     * 列类型使用包装类型,因为在更新操作时 null 值不更新
     */
    @Index
    private Integer age;
    /**
     * 列注解,选择加密方式为固定值abcd
     * 数据以json格式入库,本例为 json 字符串加密后存储,
     * 未来可能移除json同时又是加密字段的模式
     */
    @Column(encryptType = EncryptType.FIXED, encryptKey = "abcd", json = true, length = 1000)
    private List<String> tags;
    /**
     * 设置数据库字段名,默认为属性名
     */
    @Column(value = "NICK_NAME_TT4")
    private String nickname;
    @Column(value = "ENCRYPT_TEST", encryptType = EncryptType.FIXED, encryptKey = "abcdeeee1123")
    private String encryptTest;

    private String key;

    @Decimal(precision = 22, scale = 6)
    private BigDecimal acc1;

    private BigDecimal acc2;

    @Decimal(precision = 22, scale = 6)
    private BigDecimal acc3;

    private BigDecimal acc4;

    @Decimal(precision = 22, scale = 6)
    private BigDecimal acc5;

    private BigDecimal acc6;
    @Decimal(precision = 22, scale = 6)
    private BigDecimal acc7;

    private BigDecimal acc8;

    //    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;
    //    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
    //    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime localTime;

    @Column(dataType = DataType.LONGTEXT)
    private String text;

    private String new1;
    @Column(value = "T_NEW2")
    private String new2;
    @Column(value = "T_NEW4")
    private String new4;

    @Override
    public List<String> updateNullFields() {
        List<String> list = Lists.newArrayList();
        if ("".equals(new1)) {
            list.add("new1");
        }
        if ("".equals(new2)) {
            list.add("new2");
        }
        if ("".equals(new4)) {
            list.add("new4");
        }
        return list;
    }

    /**
     * 当使用string类型主键时,主键前缀
     *
     * @return 主键前缀
     */
    @Override
    public String idPrefix() {
        return "U";
    }

    /**
     * 当使用string类型主键时,主键后缀
     *
     * @return 主键后缀
     */
    @Override
    public String idSuffix() {
        return "@abc";
    }
}
