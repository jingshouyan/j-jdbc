# j-jdbc
基于spring-jdbc-template 的 crud 增强

## 目录结构
1. j-jdbc-parent # pom 依赖
2. j-jdbc-common # 工具包
3. j-jdbc-core # 核心代码包
4. j-jdbc-starter # springboot starter ,使用数据库作为主键生成工具
5. j-jdbc-test # 使用示例

## 使用方法
### 创建数据库对象
```java
@Getter@Setter@ToString
public class UserDO extends BaseDO {
    @Key
    private String id;

    @Column(encryptType = EncryptType.FLIED,encryptKey = "id")
    private String name;

    private Integer age;

    @Column(encryptType = EncryptType.FIXED,encryptKey = "abcd",json = true)
    private List<String> tags;

    @Column(value = "NICK_NAME_TT4")
    private String nickname;

    @Override
    public String idPrefix() {
        return "U";
    }
}

```