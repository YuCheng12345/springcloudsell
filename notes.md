# 1.无法执行mvn clean pakage，会出错

没解决

# 2.SpringCloud客户端Client启动时自动停止

解决方法：
	在pom.xml中加入
	<dependency>  
	    <groupId>org.springframework.boot</groupId>  
	    <artifactId>spring-boot-starter-web</artifactId>  
	</dependency>  


https://blog.csdn.net/CodeFarmer_/article/details/80592388 



# 3. springcloud中服务间两种restful调用方式

+ RestTemplate
+ Feign



# 4. 实现restTemplate

**方法一：直接使用restTemplate， url写死**

```java
@RestController
@Slf4j
public class ClientController {

    @GetMapping("/getProductMsg")
    public String getProductMsg(){
        //第一种方式，直接使用restTemplate， url写死
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://localhost:8080/msg", String.class);
        log.info("response={}", response);

        return response;
    }
}
```

**方法二：利用loadBalancerClient获取信息来构造url，然后使用restTemplate**

```java
@RestController
@Slf4j
public class ClientController {
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/getProductMsg")
    public String getProductMsg(){

        //第二种方式，利用loadBalancerClient获取信息来构造url，然后使用restTemplate
        RestTemplate restTemplate = new RestTemplate();
        //名称来自eureka页面的application的名称
        ServiceInstance serviceInstance = loadBalancerClient.choose("PRODUCT");
        String url = String.format("http://%s:%s/msg", serviceInstance.getHost(), serviceInstance.getPort());
        String response = restTemplate.getForObject(url, String.class);
        log.info("response={}", response);

        return response;
    }
}

```

**方法三：(利用@LoadBalanced，可在restTemplate中使用应用名称)**

现建一个bean

```java
package com.yc.config;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

```java
@RestController
@Slf4j
public class ClientController {

    //在config中配置了这个bean
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getProductMsg")
    public String getProductMsg(){
        //第三种方式(利用@LoadBalanced，可在restTemplate中使用应用名称)
        String response = restTemplate.getForObject("http://PRODUCT/msg", String.class);
        log.info("response={}", response);
        return response;
    }
}

```



# 5. 改变负载均衡策略

```yml
#改变负载均衡策略，PRODUCT为应用名称，默认为轮寻，现改为随机
PRODUCT:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```



# 6. Feign的使用

Feign是声明式REST客户端，采用基于接口的注解

第一步：引入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
    <version>1.4.2.RELEASE</version>
</dependency>
```

第二步：在启动主类上添加注解

```java
@EnableFeignClients
```

第三步：定义好调用的接口

```java
package com.yc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="product")//应用的名称
public interface ProductClient {

    //服务端与客户端就是通过这个GetMapping("/msg")来连接的
    @GetMapping("/msg")
    String producrMsg();
}

```

ClientController.java

```java
package com.yc.controller;

import com.yc.client.ProductClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ClientController {

    @Autowired
    private ProductClient productClient;

    @GetMapping("/getProductMsg")
    public String getProductMsg(){

        String response = productClient.producrMsg();
        log.info("response={}", response);

        return response;
    }
}

```







# 7. 多模块化与服务调用

order_master项目需要调用product_master中的接口，这时需要product_master生成自己的jar包。

在product_master项目的终端中输入以下命令进行打包，发完包将当前目录下的需要被调用的jar包复制到maven仓库中。

```shell
mvn -Dmaven.test.skip=true -U clean install
```

需要注意的是（经验所得）没有启动类的模块中的pom.xml文件中不要添加以下内容，只有有启动类的模块才需要添加，否则出现不能找到main方法的错误：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```



















































