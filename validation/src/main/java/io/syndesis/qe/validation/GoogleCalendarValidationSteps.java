package io.syndesis.qe.validation;

import io.syndesis.qe.utils.GoogleCalendarUtils;

import org.junit.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;

import io.cucumber.java.en.Then;

public class GoogleCalendarValidationSteps {
    @Autowired
    private GoogleCalendarUtils gcu;

    @Then("verify that event {string} exists in calendar {string} using account {string}")
    public void verifyThatEventExistsInCalendarUsingAccount(String eventSummary, String calendarName, String googleAccount) throws Throwable {
        verifyEvent(eventSummary, null, gcu.getAliasedCalendarName(calendarName), googleAccount);
    }

    @Then("verify that event {string} with description {string} exists in calendar {string} using account {string}")
    public void verifyThatEventWithDescriptionExistsInCalendarUsingAccount(String eventSummary, String eventDescription, String calendarName, String googleAccount) throws IOException {
        verifyEvent(eventSummary, eventDescription, gcu.getAliasedCalendarName(calendarName), googleAccount);
    }

    private void verifyEvent(String eventSummary, String eventDescription, String aliasedCalendarName, String googleAccount) throws IOException {
        Calendar c = gcu.getPreviouslyCreatedCalendar(googleAccount, aliasedCalendarName);
        Event e = gcu.getEventBySummary(googleAccount, c.getId(), eventSummary);
        Assert.assertNotNull(String.format("Event with summary \"%s\" does not exist in calendar %s", eventSummary, aliasedCalendarName), e);
        Assert.assertEquals("Summary of the event does not match expected value.", eventSummary, e.getSummary());
        if (eventDescription != null) {
            Assert.assertEquals("Description of the event does not match expected value.", eventDescription, e.getDescription());
        }
    }
}
