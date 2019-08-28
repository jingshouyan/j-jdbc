# j-jdbc
基于spring-jdbc-template 的 crud 增强

## 目录结构
1. j-jdbc #项目主目录 pom 依赖
2. j-jdbc-common # 工具包
3. j-jdbc-core # 核心代码包
4. j-jdbc-starter # springboot starter ,使用数据库作为主键生成工具
5. j-jdbc-test # 使用示例

## 使用方法

### 添加pom引用
引入 j-jdbc-core

这里使用的主键生成器为 Snowflake 的改版(奇偶间隔生成)
```pom
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-jdbc-core</artifactId>
    <version>${j-jdbc-version}</version>
</dependency>
```
或者 引入 j-jdbc-starter

这里使用的主键生成器为 基于数据库种子表的 id生成器
主要 添加了自动建表和日志功能
```pom
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-jdbc-starter</artifactId>
    <version>${j-jdbc-version}</version>
</dependency>
```
spring 配置
```yml
j-jdbc:
  tableInit: 3 # 1:自动创建不存在的表,2:删除表并创建表(慎用),3:自动创建不存在的表并添加缺少字段
```

### 创建数据库对象
```java
@Getter@Setter@ToString
//表注解,指定表明,不加默认为类名
@Table(value = "DEMO_USER",comment = "用户表")
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

```
### 创建 Dao
```java
public interface UserDao extends BaseDao<UserDO> {
}
```
### 创建 DaoImpl
```java
@Repository
public class UserDaoImpl extends BaseDaoImpl<UserDO> implements UserDao {

}
```
### 使用 Dao
参见 [BaseDao](j-jdbc-core/src/main/java/com/github/jingshouyan/jdbc/core/dao/BaseDao.java)

List/<Condition/> 可以由 [ConditionUtil](j-jdbc-common/src/main/java/com/github/jingshouyan/jdbc/comm/util/ConditionUtil.java) 快速创建
```java
List<Condition> conditions = ConditionUtil.newInstance()
        .field("age").gt(20).lte(89)
        .field("nickname").like("%张三%")
        .conditions();
List<UserDO> userBeans = userDao.query(conditions);
```

### 其他配置
1. 数据加密可以使用 
[EncryptionProvider](j-jdbc-core/src/main/java/com/github/jingshouyan/jdbc/core/encryption/EncryptionProvider.java) 
来设置自定义的加密算法,默认为AES
2. 主键生成器可以使用
[KeyGeneratorProvider](j-jdbc-core/src/main/java/com/github/jingshouyan/jdbc/core/keygen/KeyGeneratorProvider.java)
来设置自定义的主键生成器