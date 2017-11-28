package io.syndesis.qe.pages.integrations.edit;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;

import com.codeborne.selenide.SelenideElement;

import io.syndesis.qe.pages.SyndesisPageObject;
import io.syndesis.qe.pages.integrations.edit.steps.StepComponent;
import io.syndesis.qe.pages.integrations.edit.steps.StepComponentFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegrationEditPage extends SyndesisPageObject {

	private static final class Element {
		public static final By ROOT = By.cssSelector("syndesis-integrations-edit-page");
	}
	@Getter
	private ActionConfigureComponent actionConfigureComponent = new ActionConfigureComponent();
	@Getter
	private ActionConfigureComponentTwitterSearch twitterSearchComponent = new ActionConfigureComponentTwitterSearch();
	@Getter
	private ListActionsComponent listActionsComponent = new ListActionsComponent();
	@Getter
	private IntegrationFlowViewComponent flowViewComponent = new IntegrationFlowViewComponent();
	@Getter
	private IntegrationBasicsComponent integrationBasicsComponent = new IntegrationBasicsComponent();
	@Getter
	private IntegrationConnectionSelectComponent integrationConnectionSelectComponent = new IntegrationConnectionSelectComponent();
	@Getter
	private IntegrationAddStepComponent addStepComponent = new IntegrationAddStepComponent();

	private StepComponentFactory stepComponentFactory = new StepComponentFactory();
	private StepComponent stepComponent;

	@Override
	public SelenideElement getRootElement() {
		SelenideElement elementRoot = $(Element.ROOT).shouldBe(visible);
		return elementRoot;
	}

	@Override
	public boolean validate() {
		return getRootElement().is(visible);
	}

	public StepComponent getStepComponent(String stepType, String parameter) {
		stepComponent = stepComponentFactory.getStep(stepType, parameter);
		return stepComponent;
	}

}