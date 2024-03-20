# learning-spring-security-oauth2-boot

使用 [Spring Security OAuth2 Boot](https://github.com/spring-attic/spring-security-oauth2-boot) 搭建 OAuth2 授权和认证服务。

> 注意：
> - Spring Security OAuth2 Boot 是 Spring Security OAuth2 的一个子项目，它提供了一种快速搭建 OAuth2 授权和认证服务的方式。最新版本为 2.6.8。
> - 该项目2022年5月31日归档，不再维护，推荐使用 [Spring Authorization Server](https://github.com/spring-projects/spring-authorization-server) 代替。


## JWT 私钥公钥生成

- 生成 JKS 文件

```bash
keytool -genkeypair -alias myalias -storetype PKCS12 -keyalg RSA -keypass keypass -keystore mykeystore.jks -storepass storepass \
 -dname "CN=Spring Authorization Server,OU=Spring,O=Pivotal,L=San Francisco,ST=CA,C=US" -validity 3650
```

- 导出证书文件

```bash
keytool -exportcert -alias myalias -storepass storepass -keystore mykeystore.jks -file public.cer
```

- 导出公钥文件

```bash
keytool -list -rfc --keystore mykeystore.jks -storepass storepass | openssl x509 -inform pem -pubkey > public.key
```

- 导出私钥文件：

```bash
keytool -importkeystore -srckeystore mykeystore.jks -srcstorepass storepass -destkeystore private.p12 -deststoretype PKCS12 -deststorepass storepass -destkeypass keypass

#输入密码 storepass
openssl pkcs12 -in private.p12 -nodes -nocerts -out private.key
```