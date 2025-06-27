package testauto.com.ui.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import testauto.com.common.LogUtil;

import java.util.Objects;
import java.util.Optional;

public class ActionsUtils {

    private final WebDriver driver;
    private final WaitsUtil waits;
    private final PageObjectsUtil pageObjects;
    private static final int DEFAULT_TIMEOUT = 10;
    private final JavascriptExecutor jsExecutor;

    public ActionsUtils(WebDriver driver, String pageObjectsFilePath) {
        Objects.requireNonNull(driver, "Cannot instantiate ActionsUtil with a null driver reference.");
        this.driver = driver;
        jsExecutor = (JavascriptExecutor) driver;
        waits = new WaitsUtil(driver);
        pageObjects = new PageObjectsUtil(pageObjectsFilePath);
    }

    public void click(String element, int timeout) throws Exception {
        try {
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            driver.findElement(elementBy).click();
            LogUtil.info("Clicked on '" + element + "'", ActionsUtils.class);
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting click on '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public void click(String element) throws Exception {
        click(element, DEFAULT_TIMEOUT);
    }

    public void enter(String element, String data, int timeout) throws Exception {
        try {
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            WebElement field = driver.findElement(elementBy);
            field.clear();
            field.sendKeys(data);
            LogUtil.info("Entered '" + data + "' into '" + element + "'", ActionsUtils.class);
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to enter '" + data + "' into '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public void enter(String element, String data) throws Exception {
        enter(element, data, DEFAULT_TIMEOUT);
    }

    public <T> void select(String element, int timeout, String strategy, T data) throws Exception {
        Select select;
        try {
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            select = new Select(driver.findElement(elementBy));

            switch (strategy.toLowerCase()) {
                case "visible_text":
                    select.selectByVisibleText(String.valueOf(data));
                    break;
                case "contains_visible_text":
                    select.selectByContainsVisibleText(String.valueOf(data));
                    break;
                case "value":
                    select.selectByValue(String.valueOf(data));
                    break;
                case "index":
                    if (data instanceof Integer) select.selectByIndex((int) data);
                    else throw new IllegalArgumentException("Index has to be of integer type.");
                    break;
                default:
                    throw new IllegalArgumentException("'" + strategy + "' is not a supported selection strategy. Supported selection include: visible_text, contains_visible_text, value and index.");
            }

            LogUtil.info("Selected '" + data + "' from '" + element + "' using strategy '" + strategy + "'", ActionsUtils.class);

        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to select '" + data + "' from '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public <T> void select(String element, String strategy, T data) throws Exception {
        select(element, DEFAULT_TIMEOUT, strategy, data);
    }

    public <T> void deselect(String element, int timeout, String strategy, T data) throws Exception {
        Select select;
        try {
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            select = new Select(driver.findElement(elementBy));

            switch (strategy.toLowerCase()) {
                case "visible_text":
                    select.deselectByVisibleText(String.valueOf(data));
                    break;
                case "contains_visible_text":
                    select.deSelectByContainsVisibleText(String.valueOf(data));
                    break;
                case "value":
                    select.deselectByValue(String.valueOf(data));
                    break;
                case "index":
                    if (data instanceof Integer) select.deselectByIndex((int) data);
                    else throw new IllegalArgumentException("Index has to be of integer type.");
                    break;
                case "all":
                    select.deselectAll();
                    break;
                default:
                    throw new IllegalArgumentException("'" + strategy.toLowerCase() + "' is not a supported selection strategy. Supported selection include: visible_text, contains_visible_text, value, index and all.");
            }

            LogUtil.info("Deselected '" + data + "' from '" + element + "' using strategy '" + strategy + "'", ActionsUtils.class);

        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to deselect '" + data + "' from '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public <T> void deselect(String element, String strategy, T data) throws Exception {
        deselect(element, DEFAULT_TIMEOUT, strategy, data);
    }

    public void clickWithJs(String element, boolean mustExist, int timeout) throws Exception {
        try {
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBePresent(element, elementBy, mustExist, timeout);
            String script = "arguments[0].click();";
            jsExecutor.executeScript(script, driver.findElement(elementBy));
            LogUtil.info("Clicked on element '" + element + "' with javascript executor.", ActionsUtils.class);
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting click on element '" + element + "' with javascript executor.", ActionsUtils.class, e);
        }
    }

    public void clickWithJs(String element, boolean mustExist) throws Exception {
        clickWithJs(element, mustExist, DEFAULT_TIMEOUT);
    }

    public void scrollToBottom() throws Exception {
        try {
            String script = "window.scrollTo(0,document.body.scrollHeight);";
            jsExecutor.executeScript(script);
            LogUtil.info("Scrolled to the bottom of the screen.", ActionsUtils.class);
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while scrolling to the bottom.", ActionsUtils.class, e);
        }
    }

    public void scrollToElement(String element, boolean mustExist, int timeout) throws Exception {
        try {
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeDisplayed(element, elementBy, mustExist, timeout);
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", driver.findElement(elementBy));
            LogUtil.info("Scrolled to element '" + element + "'.", ActionsUtils.class);
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting scroll to element '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public void scrollToElement(String element, boolean mustExist) throws Exception {
        scrollToElement(element, mustExist, DEFAULT_TIMEOUT);
    }

    public void scrollBy(int x, int y) throws Exception {
        try {
            String script = String.format("window.scrollBy(%d,%d);", x, y);
            jsExecutor.executeScript(script);
            LogUtil.info("Scrolled by x: " + x + ", y: " + y, ActionsUtils.class);
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to scroll by x=" + x + " and y=" + y + ".", ActionsUtils.class, e);
        }
    }

    public void enterDataWithJs(String element, boolean mustExist, String data, int timeout) throws Exception {
        try {
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBePresent(element, elementBy, mustExist, timeout);
            String script = "arguments[0].value='" + data.replace("'", "\\'") + "';";
            jsExecutor.executeScript(script, driver.findElement(elementBy));
            LogUtil.info("Successfully set value '" + data + "' into field '" + element + "'.", ActionsUtils.class);
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to set value '" + data + "' with javascript executor into field '" + element + "'.", ActionsUtils.class, e);
        }
    }

    private Optional<Alert> getAlertIfPresent() {
        try {
            Alert alert = driver.switchTo().alert();
            return Optional.of(alert);
        } catch (NoAlertPresentException e) {
            return Optional.empty();
        }
    }

    public void acceptAlert() throws Exception {
        try {
            Optional<Alert> alert = getAlertIfPresent();
            if (alert.isPresent()) {
                LogUtil.info("Alert is present.", ActionsUtils.class);
                alert.get().accept();
                LogUtil.info("Accepted alert.", ActionsUtils.class);
            } else {
                LogUtil.info("Alert is not present.", ActionsUtils.class);
            }
        } catch (Exception e) {

            LogUtil.logAndRethrow("Error while attempting to accept an alert box.", ActionsUtils.class, e);
        }
    }

    public void dismissAlert() throws Exception {
        try {
            Optional<Alert> alert = getAlertIfPresent();
            if (alert.isPresent()) {
                LogUtil.info("Alert is present.", ActionsUtils.class);
                alert.get().dismiss();
                LogUtil.info("Dismissed alert.", ActionsUtils.class);
            } else {
                LogUtil.info("Alert is not present.", ActionsUtils.class);
            }
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to dismiss alert", ActionsUtils.class, e);
        }
    }

    public Optional<String> getTextFromAlert() throws Exception {
        try {
            Optional<Alert> alert = getAlertIfPresent();
            if (alert.isPresent()) {
                LogUtil.info("Alert is present.", ActionsUtils.class);
                String extractedText = alert.get().getText();
                LogUtil.info("Extracted text '" + extractedText + "' from alert.", ActionsUtils.class);
                return Optional.of(extractedText);
            } else {
                LogUtil.info("Alert is not present.", ActionsUtils.class);
            }
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to extract text from alert.", ActionsUtils.class, e);
        }
        return Optional.empty();
    }

    public void enterDataToPromptBox(String data) throws Exception {
        try {
            Optional<Alert> alert = getAlertIfPresent();
            if (alert.isPresent()) {
                LogUtil.info("Alert is present.", ActionsUtils.class);
                alert.get().sendKeys(data);
                LogUtil.info("Entered '" + data + "' to prompt box.", ActionsUtils.class);
            } else {
                LogUtil.info("Alert is not present.", ActionsUtils.class);
            }
        } catch (Exception e) {
            LogUtil.logAndRethrow("Error while attempting to enter data into prompt box.", ActionsUtils.class, e);
        }
    }

}