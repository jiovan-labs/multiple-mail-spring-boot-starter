[English](README.md) | [簡體中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# 多郵件 Spring Boot Starter

讓 Spring Boot 應用快速整合多個郵件服務。

## 快速開始

- 匯入相依性

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>multiple-mail-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> 預設情況下，此 starter 會自動生效，你可以透過 `jiovan.multiple-mail.enabled = false` 關閉它。

- 使用字首 `jiovan.multiple-mail` 設定 Spring Boot 的 `application.yml`

```yml
jiovan:
    multiple-mail:
        primary:
            host: '192.168.0.1'
            port: 25
            username: 'foo1'
            password: 'bar1'
            properties:
        secondary:
            host: '192.168.0.2'
            port: 25
            username: 'foo2'
            password: 'bar2'
            properties:
        tertiary:
        quaternary:
        quinary:
        senary:
```

> 此 starter 最多支援 6 個 `JavaMailSenderImpl`。

- 透過 `@Autowired`/`@Resource` 註解並配合 `@Qualifier` 註解注入以下 Bean（以 `primary` 為例）

| Bean 型別 | 限定符 |
|--------------------|--------------------------------------------|
| JavaMailSenderImpl | PrimaryMailSenderConfiguration.MAIL_SENDER |

## 文件

- GitHub：https://github.com/jiovan-labs/multiple-mail-spring-boot-starter

## 環境要求

- jdk 17+

## 許可證

本專案採用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 許可證。

請參閱 `NOTICE.txt` 檔案瞭解所需的宣告與歸屬資訊。

## 捐贈

喜歡這個工具包嗎？歡迎[向我們捐贈](https://jiovan.com/donation)以支援開發。

## 版權

北京玖漫科技有限公司

## 網站

- 玖漫：https://jiovan.com
