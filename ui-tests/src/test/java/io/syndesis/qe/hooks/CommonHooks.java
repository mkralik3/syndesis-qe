package io.syndesis.qe.hooks;

import io.syndesis.qe.resource.ResourceFactory;
import io.syndesis.qe.resource.impl.AMQ;
import io.syndesis.qe.resource.impl.DV;
import io.syndesis.qe.resource.impl.FHIR;
import io.syndesis.qe.resource.impl.MySQL;
import io.syndesis.qe.resource.impl.PublicOauthProxy;
import io.syndesis.qe.steps.CommonSteps;
import io.syndesis.qe.utils.SampleDbConnectionManager;
import io.syndesis.qe.utils.fhir.FhirClientManager;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.springframework.beans.factory.annotation.Autowired;

import com.codeborne.selenide.WebDriverRunner;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonHooks {
    @Autowired
    CommonSteps cs;

    @AfterStep
    public void afterScreenshot(Scenario scenario) {
        if (scenario.isFailed() && WebDriverRunner.hasWebDriverStarted()) {
            byte[] screenshotAsBytes = ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshotAsBytes, "image/png", "Screenshot");
        }
    }

    //we can close it after specific scenarios, but it will do nothing if connection == null and I do not know exactly all scenarios which opens DB
    // connection
    //@After("@scenario1,@scenario2")
    @After
    public void closeDBConnection() {
        log.debug("Closing DB connection if it exists");
        SampleDbConnectionManager.closeConnections();
    }

    @After("@integrations-mqtt,@integrations-amqp-to-amqp,@integrations-openwire-to-openwire")
    public void closeAMQBroker() {
        log.info("Deleting AMQ broker");
        ResourceFactory.destroy(AMQ.class);
    }

    @After("@publicapi")
    public void cleanPublicApi() {
        log.info("Deleting Public API");
        ResourceFactory.destroy(PublicOauthProxy.class);
    }

    @After("@integrations-db-to-db-mysql")
    public void cleanMYSQLserver() {
        log.info("Deleting MySQL server");
        ResourceFactory.destroy(MySQL.class);
    }

    @After("@3scale")
    public void default3scaleAnnotation() {
        log.info("Removing 3scale discovery functionality");
        cs.disable3scaleEnvVar();
    }

    @After("@dv")
    public void cleanDv() {
        log.info("Deleting DV addon");
        ResourceFactory.destroy(DV.class);
    }

    @After("@fhir")
    public void cleanFHIR() {
        log.info("Removing FHIR");
        ResourceFactory.destroy(FHIR.class);
        FhirClientManager.closeClient();
    }
}
