package testauto.com.ui.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import testauto.com.common.LogUtil;

public class ActionsUtils {

    private final WebDriver driver;
    private final WaitsUtil waits;
    private final PageObjectsUtil pageObjects;
    private static final int DEFAULT_TIMEOUT = 10;

    public ActionsUtils(WebDriver driver, String pageObjectsFilePath){
        this.driver = driver;
        waits = new WaitsUtil(driver);
        pageObjects = new PageObjectsUtil(pageObjectsFilePath);
    }

    public void click(String element, int timeout) throws Exception {
        try{
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            driver.findElement(elementBy).click();
            LogUtil.info("Clicked on '" + element + "'", ActionsUtils.class);
        }catch (Exception e){
            LogUtil.logAndRethrow("Error while attempting click on '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public void click(String element) throws Exception {
        click(element, DEFAULT_TIMEOUT);
    }

    public void enter(String element,String data, int timeout) throws Exception {
        try{
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            WebElement field = driver.findElement(elementBy);
            field.clear();
            field.sendKeys(data);
            LogUtil.info("Entered '" + data + "' into '" + element + "'", ActionsUtils.class);
        }catch (Exception e){
            LogUtil.logAndRethrow("Error while attempting to enter '" + data + "' into '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public void enter(String element, String data) throws Exception {
        enter(element, data, DEFAULT_TIMEOUT);
    }

    public <T> void select(String element,int timeout, String strategy, T data) throws Exception {
        Select select;
        try{
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            select = new Select(driver.findElement(elementBy));

            switch(strategy.toLowerCase()){
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
                    if(data instanceof Integer) select.selectByIndex((int) data);
                    else throw new IllegalArgumentException("Index has to be of integer type.");
                    break;
                default:
                    throw new IllegalArgumentException("'" + strategy + "' is not a supported selection strategy. Supported selection include: visible_text, contains_visible_text, value and index.");
            }

            LogUtil.info("Selected '" + data + "' from '" + element + "' using strategy '" + strategy + "'", ActionsUtils.class);

        }catch (Exception e){
            LogUtil.logAndRethrow("Error while attempting to select '" + data + "' from '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public <T> void select(String element, String strategy, T data) throws Exception {
        select(element, DEFAULT_TIMEOUT, strategy, data);
    }

    public <T> void deselect(String element,int timeout, String strategy, T data) throws Exception {
        Select select;
        try{
            By elementBy = pageObjects.getElementBy(element);
            waits.waitForElementToBeClickable(element, elementBy, timeout);
            select = new Select(driver.findElement(elementBy));

            switch(strategy.toLowerCase()){
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
                    if(data instanceof Integer) select.deselectByIndex((int) data);
                    else throw new IllegalArgumentException("Index has to be of integer type.");
                    break;
                case "all":
                    select.deselectAll();
                    break;
                default:
                    throw new IllegalArgumentException("'" + strategy.toLowerCase() + "' is not a supported selection strategy. Supported selection include: visible_text, contains_visible_text, value, index and all.");
            }

            LogUtil.info("Deselected '" + data + "' from '" + element + "' using strategy '" + strategy + "'", ActionsUtils.class);

        }catch (Exception e){
            LogUtil.logAndRethrow("Error while attempting to deselect '" + data + "' from '" + element + "'.", ActionsUtils.class, e);
        }
    }

    public <T> void deselect(String element, String strategy, T data) throws Exception {
        deselect(element, DEFAULT_TIMEOUT, strategy, data);
    }


}