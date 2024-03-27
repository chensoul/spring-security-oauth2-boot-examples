# learn-spring-security-oauth2-boot

使用 [Spring Security OAuth2 Boot](https://github.com/spring-attic/spring-security-oauth2-boot) 搭建 OAuth2 授权和认证服务。

> 注意：
> - Spring Security OAuth2 Boot 是 Spring Security OAuth2 的一个子项目，它提供了一种快速搭建 OAuth2 授权和认证服务的方式。最新版本为 2.6.8。
> - 该项目2022年5月31日归档，不再维护，推荐使用 [Spring Authorization Server](https://github.com/spring-projects/spring-authorization-server) 代替。


## 生成证书

生成 keystore：

```bash
keytool -genkeypair -alias authorizationserver -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -storepass password -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=CN" -validity 3650
```

导出公钥文件：

```bash
keytool -list -rfc --keystore keystore.p12 -storepass password | openssl x509 -inform pem -pubkey > public.key
```

导出私钥文件：

```bash
keytool -importkeystore -srckeystore keystore.p12 -srcstorepass password -destkeystore private.p12 -deststoretype PKCS12 -deststorepass password -destkeypass password

#输入密码 storepass
openssl pkcs12 -in private.p12 -nodes -nocerts -out private.key
```
