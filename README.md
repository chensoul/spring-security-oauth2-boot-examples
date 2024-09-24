# learn-spring-security-oauth2-boot

使用 [Spring Security OAuth2 Boot](https://github.com/spring-attic/spring-security-oauth2-boot) 搭建 OAuth2 授权和认证服务。

> 注意：
> - Spring Security OAuth2 Boot 是 Spring Security OAuth2 的一个子项目，它提供了一种快速搭建 OAuth2 授权和认证服务的方式。最新版本为 2.6.8。
> - 该项目2022年5月31日归档，不再维护，推荐使用 [Spring Authorization Server](https://github.com/spring-projects/spring-authorization-server) 代替。


## 生成证书

JRE 提供了一个简单的证书管理工具——keytool。它位于您的JRE_HOME\bin目录下。以下代码中的命令生成一个自签名证书并将其放入 PKCS12 KeyStore 中。除了 KeyStore 的类型之外，您还需要设置其有效期、别名以及文件名。在开始生成过程之前，keytool会要求您输入密码和一些其他信息，如下所示：

```bash
keytool -genkeypair -alias mytest -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore mytest.p12 -storepass mypass \
    -dname "CN=WebServer,OU=Unit,O=Organization,L=City,S=State,C=CN" -validity 3650
```

导出公钥文件：
```bash
keytool -list -rfc --keystore mytest.p12 -storepass mypass | \
    openssl x509 -inform pem -pubkey > public.key
```

导出私钥文件：
```bash
keytool -importkeystore -srckeystore mytest.p12 -srcstorepass mypass \
    -destkeystore private.p12 -deststoretype PKCS12 \
    -deststorepass mypass -destkeypass mytest

#输入 storepass 密码 
openssl pkcs12 -in private.p12 -nodes -nocerts -out private.key
```
