package testauto.com.ui.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import testauto.com.common.LogUtil;
import java.time.Duration;
import java.util.Objects;

@Slf4j
public class WaitsUtil {
    private final WebDriver driver;
    private static final int DEFAULT_TIMEOUT = 10;

    public WaitsUtil(WebDriver driver) {
        if(driver == null){
            throw new IllegalArgumentException("Cannot instantiate WaitsUtil with a null driver.");
        }
        this.driver = driver;
    }

    private FluentWait<WebDriver> getWait(int timeout){
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofMillis(1000))
                .ignoring(NoSuchElementException.class);
    }

    public void waitForElementToBePresent(String element, By elementBy, boolean mustExist, int timeout) throws Exception {
        try {
            FluentWait<WebDriver> wait = getWait(timeout);
            wait.until(ExpectedConditions.presenceOfElementLocated(elementBy));
            LogUtil.info("Element '" + element + "' is present on the DOM.", WaitsUtil.class);
        } catch (TimeoutException e) {
            String errorMessage = "Timed out after " + timeout + " seconds while waiting for element '" + element + "' to be present on the DOM.";
            if(mustExist) {
                LogUtil.logAndRethrow(errorMessage, WaitsUtil.class, e);
            }else{
                LogUtil.info(errorMessage, WaitsUtil.class);
            }
        }
    }

    public void waitForElementToBePresent(String element, By elementBy, boolean mustExist) throws Exception {
        waitForElementToBePresent(element, elementBy, mustExist, DEFAULT_TIMEOUT);
    }

    public void waitForElementToBeDisplayed(String element, By elementBy, boolean mustExist, int timeout) throws Exception {
        try {
            FluentWait<WebDriver> wait = getWait(timeout);
            wait.until(ExpectedConditions.visibilityOfElementLocated(elementBy));
            LogUtil.info("Element '" + element + "' is displayed.", WaitsUtil.class);
        } catch (TimeoutException e) {
            String errorMessage = "Timed out after " + timeout + " seconds while waiting for element '" + element + "' to be displayed.";
            if(mustExist) {
                LogUtil.logAndRethrow(errorMessage, WaitsUtil.class, e);
            }else{
                LogUtil.info(errorMessage, WaitsUtil.class);
            }
        }
    }

    public void waitForElementToBeDisplayed(String element,By elementBy, boolean mustExist) throws Exception {
        waitForElementToBeDisplayed(element, elementBy, mustExist, DEFAULT_TIMEOUT);
    }

    public WebElement waitForElementToBeClickable(String element, By elementBy, int timeout){
        WebElement webElement = null;
        try {
            FluentWait<WebDriver> wait = getWait(timeout);
            webElement = wait.until(ExpectedConditions.elementToBeClickable(elementBy));
            LogUtil.info("Element '" + element + "' is clickable.", WaitsUtil.class);
        } catch (TimeoutException e) {
            LogUtil.logAndRethrow("Timed out after " + timeout + " seconds while waiting for element '" + element + "' to be clickable.", WaitsUtil.class, e);
        }
        return Objects.requireNonNull(webElement, "Value of WebElement cannot be null.");
    }

    public WebElement waitForElementToBeClickable(String element, By elementBy) throws Exception {
        return waitForElementToBeClickable(element, elementBy, DEFAULT_TIMEOUT);
    }

    public void waitForFrameAndSwitchToIt(String element, By frame, int timeout) throws Exception {
        try{
            FluentWait<WebDriver> wait = getWait(timeout);
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
            LogUtil.info("Switched to frame '" + element + "' after it became available.", WaitsUtil.class);
        }catch (TimeoutException e){
            LogUtil.logAndRethrow("Timed out after " + timeout + " seconds while waiting for frame '" + element + "' to be available.", WaitsUtil.class, e);
        }
    }

    public void waitForFrameAndSwitchToIt(String element, By elementBy) throws Exception {
        waitForFrameAndSwitchToIt(element, elementBy,DEFAULT_TIMEOUT);
    }

    public void waitForElementToDisappear(String element,By elementBy, int timeout) throws Exception {
        try{
            FluentWait<WebDriver> wait = getWait(timeout);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(elementBy));
            LogUtil.info("Element '" + element + "' is invisible.", WaitsUtil.class);
        }catch (TimeoutException e){
            LogUtil.logAndRethrow("Timed out after " + timeout + " seconds while waiting for element '" + element +  "' to disappear.", WaitsUtil.class, e);
        }
    }

    public void waitForElementToDisappear(String element,By elementBy) throws Exception {
        waitForElementToDisappear(element,elementBy, DEFAULT_TIMEOUT);
    }

}
