##特色功能

- 完全基于注解的权限控制，项目自定义了`@IgnoreAuthorize`,`@ApiAuthority`注解，通过在RESTful资源上添加相应
注解，就能控制资源的访问权限；
- 基于RBAC模型的权限控制，资源authority信息不需要手动添加到数据库权限表；项目启动自动扫描注解@ApiAuthority里
的属性信息，添加到 admin权限表或者 member权限表，供管理员后台web页面给用户分配。
- 基于jwt的权限认证，根据引入的jar包情况，自动配置 token manager（refresh token manager）；若项目中引入了redis，会自动使用redis token
manager，可以统计在线会员信息，引入refresh token manager 可以解决jwt权限认证，难以提前取消之前签发会员的token问题（比如冻结屏蔽账号）的情况；
- 系统级别普通账户、管理员账户认证分离，App客户端、Web客户端认证分离；账号登录连续出错，暂时暂停认证功能

## 使用注意事项
###### @ApiAuthority注解
    
```
    //不会添加数据到权限表authority，只会作为权限鉴定使用，说明注解所在api接口为 只要是普通会员都可以访问
    @ApiAuthority(ApiAuthority.Type.MEMBER)
    //会添加数据到权限表authority,表示此api接口，拥有 demo:table:add 权限的会员 才能访问；
    @ApiAuthority(value = ApiAuthority.Type.MEMBER, name = "demo:table:add", description = "添加数据") 
```
###### @AuthorizeIgnore注解
```yaml
    #系统默认访问所有restful接口都要鉴权，若接口不需要鉴权，可以在接口上标注@IgnoreAuthgrize注解即可。
    #还有另外一种方式也可以实现，就是在配置参数里添加ant风格的暴露接口格式，比如：
    security:
      jwt:
        permitEndpoints: "/swagger-resources/**,/swagger-ui.html,/v2/api-docs,/webjars/**"
```

###### 基本参数配置
```yaml
security:
  jwt: # 设置jwt 相关的参数
    tokenExpirationTime: 30 # 分钟
    refreshTokenExpTime: 14400 # 分钟  10 days
    refreshTokenReuse: false #设置为false，可以实现用户连续登录，就不需要重新登录认证，否则用户间隔10天，就要重新认证，
    tokenIssuer: http://www.kabour.online
    tokenSigningKey: xEV6Hy5R0MFK4EEACID0uQus #换成你自己的密钥
    annotationPackages: online.kabour #扫描@ApiAuthority(value = ApiAuthority.Type.MEMBER, name = "demo:table:add", description = "添加数据") 的基础路径
    permitEndpoints: "/swagger-resources/**,/swagger-ui.html,/v2/api-docs,/webjars/**" 
  ajax:
    enabled: true #是否需要开启 ajax认证模块，如果开启，系统会暴露 token签发 和 refresh token端点
```
