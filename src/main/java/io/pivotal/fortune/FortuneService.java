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

@Component
public class FortuneService {

    Logger logger = LoggerFactory
        .getLogger(FortuneService.class);

    RestTemplate restTemplate = new RestTemplate();

    private LoadBalancerClient loadBalancer;

    public FortuneService(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @HystrixCommand(fallbackMethod = "defaultFortune")
    public String getFortune() {

        // Using service template because of issue getting stub runner to register with https properly
        ServiceInstance instance = loadBalancer.choose("fortune-service");

        String resolvedAndCalculatedUrl = instance.getUri().toString();

        logger.debug("Following instance was returned for fortune-service. " + resolvedAndCalculatedUrl);

        if(instance.getPort() == 80) {
            // In this case, we have the issue with stub runner registering with http on cloud foundry
            resolvedAndCalculatedUrl = "https://" + instance.getHost();
            logger.debug("Forcing https.  Using this Url: " + resolvedAndCalculatedUrl);
        }

        String fortune = restTemplate.getForObject(resolvedAndCalculatedUrl, String.class);

        logger.debug("Got fortune=[{}]", fortune);

        return fortune;

    }

    public String defaultFortune(Throwable throwable) {
        logger.debug("Returning fallback fortune. Error: {}", throwable.toString());
        return "This fortune is no good. Try another.";
    }

}
