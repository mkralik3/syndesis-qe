package io.syndesis.qe.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.syndesis.qe.account.Account;
import io.syndesis.qe.account.AccountsDirectory;
import io.syndesis.qe.utils.jms.JMSUtils;

import org.assertj.core.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class JmsValidationSteps {

    private final String messageText = "ZIL SOM NAPLNO";

    public JmsValidationSteps() {
    }

    @Then("verify that JMS message using {string} protocol, published on {string} named {string} has arrived to {string} named {string} consumer")
    public void verifyJMSconnection(String protocol, String typeFrom, String destinationFrom, String typeTo, String destinationTo) {
        JMSUtils.sendMessage(protocol, JMSUtils.Destination.valueOf(typeFrom.toUpperCase()), destinationFrom, messageText);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String textMessage = JMSUtils.getMessageText(protocol, JMSUtils.Destination.valueOf(typeTo.toUpperCase()), destinationTo);
        Assertions.assertThat(textMessage).isEqualTo(messageText);
    }

    @Then("verify that JMS message with content {string} was received from {string} {string}")
    public void verifyThatJMSMessageWithContentWasReceivedFrom(String content, String type, String destination) {
        final String message = JMSUtils.getMessageText("tcp", JMSUtils.Destination.valueOf(type.toUpperCase()), destination);
        assertThat(message).isEqualTo(content);
    }

    @When("publish message with content {string} to {string} with name {string}")
    public void publishMessageToDestinationWithName(String content, String type, String name) {
        JMSUtils.sendMessage("tcp", JMSUtils.Destination.valueOf(type.toUpperCase()), name, content);
    }

    @Given("clean destination type {string} with name {string}")
    public void cleanDestination(String type, String name) {
        JMSUtils.clear(JMSUtils.Destination.valueOf(type.toUpperCase()), name);
    }

    /**
     * Load JMS message from resource and send it to the topic/queue with name
     *
     * @param resourceName - name of resource file with the message
     * @param type - queue or topic
     * @param name - name of topic/queue
     */
    @When("publish JMS message from resource {string} to {string} with name {string}")
    public void publishMessageFromResourceToDestinationWithName(String resourceName, String type, String name) throws IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL fileUrl = classLoader.getResource("jms_messages/" + resourceName);
        if (fileUrl == null) {
            fail("File with name " + resourceName + " doesn't exist in the resources");
        }

        File file = new File(fileUrl.getFile());
        String jmsMessage = new String(Files.readAllBytes(file.toPath()));
        JMSUtils.sendMessage("tcp", JMSUtils.Destination.valueOf(type.toUpperCase()), name, jmsMessage);
    }

    @Then("verify that the JMS queue {string} is empty")
    public void verifyEmptyQueue(String queue) {
        // This waits up to 60 seconds to get something for the queue
        Assertions.assertThat(JMSUtils.getMessage(JMSUtils.Destination.QUEUE, queue)).isNull();
    }

    @Then("^verify that (\\d+) messages? (?:were|was) received from JMS queue \"([^\"]*)\"$")
    public void verifyEmptyQueue(int count, String queue) {
        for (int i = 0; i < count; i++) {
            Assertions.assertThat(JMSUtils.getMessage(JMSUtils.Destination.QUEUE, queue)).isNotNull();
        }
        verifyEmptyQueue(queue);
    }

    @Then("verify that JMS queue {string} received a message in {int} seconds")
    public void verifyEmptyQueue(String queue, int secondsTimeout) {
        Assertions.assertThat(JMSUtils.getMessage("tcp", JMSUtils.Destination.QUEUE, queue, secondsTimeout * 1000L)).isNotNull();
    }

    @When("send {string} message to {string} queue on {string} broker")
    public void sendMessageToQueueOnBroker(String message, String queue, String brokerAccount) {
        Account brokerCredentials = AccountsDirectory.getInstance().get(brokerAccount);
        final String userName = brokerCredentials.getProperty("username");
        final String password = brokerCredentials.getProperty("password");
        final String brokerpod = brokerCredentials.getProperty("appname");
        JMSUtils.sendMessage(brokerpod, "tcp", userName, password, JMSUtils.Destination.QUEUE, queue, message);
    }
}
