# ops-show-log
## pom
```xml
        <dependency>
            <groupId>com.ximalaya.ops</groupId>
            <artifactId>ops-show-log</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```
## web.xml
```xml
    <servlet>
        <servlet-name>log</servlet-name>
        <servlet-class>com.ximalaya.ops.show.log.LogServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>log</servlet-name>
        <url-pattern>/log/*</url-pattern>
    </servlet-mapping>
    <context-param>
        <param-name>logFilePathConfigLocation</param-name>
        <param-value>/logs,/var</param-value>
    </context-param>
```
