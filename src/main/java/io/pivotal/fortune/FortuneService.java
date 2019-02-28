package io.pivotal.fortune;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class FortuneService {

  Logger logger = LoggerFactory
          .getLogger(FortuneService.class);

  private final RestTemplate restTemplate;

  @Value("${fortuneServiceURL:https://fortune-service}")
  String fortuneServiceURL;

  public FortuneService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Autowired
  private LoadBalancerClient loadBalancer;

  @HystrixCommand(fallbackMethod = "defaultFortune")
  public String getFortune() {
    logger.debug("Using fortuneServiceURL=[{}]", fortuneServiceURL);

      ServiceInstance instance = loadBalancer.choose("fortune-service");
      URI secondServiceUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));

//      System.out.println(secondServiceUri); // logs http://192.168.0.205:8090, check log below
      logger.debug(secondServiceUri.toString());

      RestTemplate rt = new RestTemplate();

    String fortune = rt.getForObject("https://" + instance.getHost(), String.class);
    logger.debug("Got fortune=[{}]", fortune);
    return fortune;
  }

  public String defaultFortune(Throwable throwable){
    logger.debug("Returning fallback fortune. Error: {}", throwable.toString());
    return "This fortune is no good. Try another.";
  }

}
