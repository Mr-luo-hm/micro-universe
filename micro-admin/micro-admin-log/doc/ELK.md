# ELK

> 描述
- 本项目的ELK统一使用logback
- 统一使用同一套模板服务
- logstash 需要自己进行搭建，本文档只提供相关配置

- 索引模板：是为了统一es索引的配置，字段类型按照模板的来
```shell
curl -XPUT "http://localhost:9100/_template/template_logstash" -H 'Content-Type: application/json' -d'{ "template":"micro_log_*","settings":{"number_of_shards":2,"number_of_replicas":1},"mappings":{"properties":{"level":{"type":"keyword"},"host":{"type":"keyword"},"message":{"type":"text"},"thread_name":{"type":"keyword"},"traceId":{"type":"keyword"},"projectName":{"type":"keyword"},"moduleName":{"type": "keyword"}}}}'
```

- logstash 配置文件
```shell
input {
  tcp{
     mode => "server"
    # 这个需要配置成本机IP，不然logstash无法启动
    host => "192.168.1.6"
    # 端口号
    port => 4567
    codec => json_lines
  }
}

output {
  elasticsearch {
    #hosts => ["http://localhost:9200"]
    action=>"index"
    hosts => ["192.168.1.4:9100","192.168.1.4:9101","192.168.1.4:9102"] # es集群
    index => "micro_log_%{+YYYY_MM}"  # 索引产生规则 这边是按照月来的
    # index=> "micro-logstash"
    #user => "username"
    #password => "pass"
  }
}
```
- logback配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="false">
    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义后，可以使“${}”来使用变量。 -->
    <property name="log.path" value="logs/${project.artifactId}"/>
    <!-- 彩色日志格式 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%X{traceId}]){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter"/>
    <conversionRule conversionWord="wex"
                    converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"/>
    <conversionRule conversionWord="wEx"
                    converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"/>
    <!--1. 输出到控制台-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <Pattern>${CONSOLE_LOG_PATTERN}</Pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

<!--    &lt;!&ndash; 国际化配置 &ndash;&gt;-->
<!--    <bean id = "messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">-->
<!--        &lt;!&ndash; 配置国际化资源文件名和路径&ndash;&gt;-->
<!--        <property name="basenames" value="message"/>-->
<!--    </bean>-->

    <!--2. 输出到文档-->
    <!-- 2.1 level为 DEBUG 日志，时间滚动输出  -->
    <appender name="DEBUG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/debug.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/%d{yyyy-MM, aux}/debug.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <!--日志文档最大大小-->
            <maxFileSize>100MB</maxFileSize>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录debug级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>debug</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 2.2 level为 INFO 日志，时间滚动输出  -->
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/info.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/%d{yyyy-MM, aux}/info.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <!--日志文档最大大小-->
            <maxFileSize>100MB</maxFileSize>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录info级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>info</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 2.3 level为 WARN 日志，时间滚动输出  -->
    <appender name="WARN_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/warn.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/%d{yyyy-MM, aux}/warn.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <!--日志文档最大大小-->
            <maxFileSize>100MB</maxFileSize>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录warn级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>warn</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 2.4 level为 ERROR 日志，时间滚动输出  -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/error.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/%d{yyyy-MM, aux}/error.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <!--日志文档最大大小-->
            <maxFileSize>100MB</maxFileSize>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录error级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>error</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>
	<appender name="stash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
		<!--  这是是logstash服务器地址 端口-->
		<destination>micro-logstash:5044</destination>
		<!--输出的格式，推荐使用这个-->

		<encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <!--写死的项目名和模块名-->
			<customFields>{"moduleName":"micro_xxx"}</customFields>
			<providers>
				<timestamp>
					<timeZone>CST</timeZone>
				</timestamp>
				<version/>
				<message/>
				<loggerName/>
				<threadName/>
				<logLevel/>
				<callerData/>
			</providers>
		</encoder>
	</appender>
	<!--<appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
		<destination>192.168.1.6:4567</destination>
		<encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder" />
	</appender>-->
    <!--
        root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
        level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
        不能设置为INHERITED或者同义词NULL。默认是DEBUG
        可以包含零个或多个元素，标识这个appender将会添加到这个logger。
    -->
    <!-- 开发环境:打印控制台-->
    <springProfile name="dev">
        <root level="info">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="DEBUG_FILE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="ERROR_FILE" />
            <appender-ref ref="WARN_FILE" />
			<appender-ref ref="stash" />
        </root>
        <logger name="org.apache.kafka" level="off" />
    </springProfile>

    <!-- 测试环境:输出到文档 -->
    <springProfile name="test">
        <root level="debug">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="DEBUG_FILE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="ERROR_FILE" />
            <appender-ref ref="WARN_FILE" />
        </root>
        <logger name="org.apache.kafka" level="off" />
    </springProfile>
	<!-- 预发布环境:输出到文档 -->
	<springProfile name="uat">
		<root level="info">
			<appender-ref ref="CONSOLE" />
			<appender-ref ref="DEBUG_FILE" />
			<appender-ref ref="INFO_FILE" />
			<appender-ref ref="ERROR_FILE" />
			<appender-ref ref="WARN_FILE" />
		</root>
		<logger name="org.apache.kafka" level="off" />
	</springProfile>
    <!-- 生产环境:输出到文档 -->
    <springProfile name="prod">
        <root level="info">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="DEBUG_FILE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="ERROR_FILE" />
            <appender-ref ref="WARN_FILE" />
			<appender-ref ref="stash" />
        </root>
        <logger name="org.apache.kafka" level="off" />
    </springProfile>

</configuration>
```
