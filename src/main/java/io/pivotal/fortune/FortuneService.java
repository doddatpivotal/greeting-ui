package io.pivotal.fortune;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

//  @HystrixCommand(fallbackMethod = "defaultFortune")
  public String getFortune() {
    logger.debug("Using fortuneServiceURL=[{}]", fortuneServiceURL);
    RestTemplate rt = new RestTemplate();
    String fortune = rt.getForObject(fortuneServiceURL, String.class);
    logger.debug("Got fortune=[{}]", fortune);
    return fortune;
  }

  public String defaultFortune(Throwable throwable){
    logger.debug("Returning fallback fortune. Error: {}", throwable.toString());
    return "This fortune is no good. Try another.";
  }

}
