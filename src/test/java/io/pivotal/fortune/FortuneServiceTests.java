package io.pivotal.fortune;

import io.pivotal.GreetingUIApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreetingUIApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.application.name=greeting-ui",
        "spring.cloud.circuit.breaker.enabled=false",
        "hystrix.stream.queue.enabled=false",
        "logging.level.io.pivotal.fortune=DEBUG",
        "logging.level.org.springframework.web.client.RestTemplate=DEBUG"})
@AutoConfigureStubRunner(
    ids = {"io.pivotal:fortune-service:+"},
    stubsMode = StubRunnerProperties.StubsMode.REMOTE,
    repositoryRoot = "${REPO_WITH_BINARIES}"
)

public class FortuneServiceTests {

    @Autowired
    FortuneService fortuneService;


    @Test
    @Ignore
    public void shouldSendRequestToFortune() {
        // when
        String fortune = fortuneService.getFortune();
        // then
        BDDAssertions.then(fortune).isEqualTo("foo fortune");
    }

}

