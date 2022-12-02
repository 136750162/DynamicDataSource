# DynamicDataSource
基于Spring开发的数据库动态数据源
简单实用0侵入禁止商用，仅供参考与学习。代码版本已发布到Maven中央仓库，如需使用，可直接使用Maven依赖引入使用，欢迎各位大佬来指点
```
<dependency>
    <groupId>io.github.136750162</groupId>
    <artifactId>spring-boot-starter-dynamicDataSource</artifactId>
    <version>0.1.1</version>
</dependency>
```
使用只需要编写好配置文件添加注解@TargetDataSource("数据源分组Key")
配置文件编写按照
第一层级为分组Key、最后层级为最终的属性的Key格式编写配置即可
