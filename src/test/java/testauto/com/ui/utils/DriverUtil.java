package testauto.com.ui.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import testauto.com.common.LogUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static testauto.com.common.FilesUtil.getPropertyValue;

public class DriverUtil {

    private static WebDriver webDriver;
    private static AppiumDriver mobileDriver;

    private static final String ARG_DELIMITER = "\\|";

    private static MutableCapabilities loadWebOptions(String propertiesPath) throws Exception {
        try{
            MutableCapabilities driverOptions;
            String browser = getPropertyValue(propertiesPath, "browser");
            Objects.requireNonNull(browser, "Value for browser cannot be null at this point.");
            String argumentsFromProperties = getPropertyValue(propertiesPath, browser + ".arguments");
            String [] arguments = argumentsFromProperties == null ? new String[0] : argumentsFromProperties.split(ARG_DELIMITER);
            if(browser.equalsIgnoreCase("chrome")){
                driverOptions = new ChromeOptions();
                ((ChromeOptions) driverOptions).addArguments(arguments);
            }else if(browser.equalsIgnoreCase("edge")){
                driverOptions = new EdgeOptions();
                ((EdgeOptions) driverOptions).addArguments(arguments);
            }else if(browser.equalsIgnoreCase("firefox")){
                driverOptions = new FirefoxOptions();
                ((FirefoxOptions) driverOptions).addArguments(arguments);
            }else{
                throw new UnsupportedOperationException("'" + browser + "' is not a supported browser. Supported browsers (chrome, edge and firefox).");
            }
            LogUtil.info("Loading web options = '"  + Arrays.toString(arguments) +"'.", DriverUtil.class);
            return Objects.requireNonNull(driverOptions, "Driver options must not be null.");
        }catch (Exception e){
            LogUtil.logAndRethrow("Error while loading web options.", DriverUtil.class, e);
            throw e;
        }
    }

    public static void initializeWebDriver(String propertiesPath) throws Exception {
        MutableCapabilities driverOptions = loadWebOptions(propertiesPath);
        String switchesFromProperties = getPropertyValue(propertiesPath, "experimental.switches");
        String [] switches = switchesFromProperties == null ? new String[0] : switchesFromProperties.split(ARG_DELIMITER);
        if(driverOptions instanceof ChromeOptions){
            ((ChromeOptions) driverOptions).setExperimentalOption("excludeSwitches", Arrays.asList(switches));
            WebDriverManager.chromedriver().setup();
            webDriver = new ChromeDriver((ChromeOptions) driverOptions);
        }else if(driverOptions instanceof EdgeOptions){
            ((EdgeOptions) driverOptions).setExperimentalOption("excludeSwitches", Arrays.asList(switches));
            WebDriverManager.edgedriver().setup();
            webDriver = new EdgeDriver((EdgeOptions) driverOptions);
        }else if(driverOptions instanceof FirefoxOptions){
            WebDriverManager.firefoxdriver().setup();
            webDriver = new FirefoxDriver((FirefoxOptions) driverOptions);
        }
        Objects.requireNonNull(webDriver, "webDriver cannot be null.");
        LogUtil.info("Driver set to '" + driverOptions.getBrowserName() + "'.", DriverUtil.class);
        webDriver.manage().window().maximize();
    }

    private static DesiredCapabilities getMobileCapabilities(String propertiesPath,String platformName, boolean isBrowser) throws IOException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String prefix = platformName + ".";
        capabilities.setCapability("platformName", getPropertyValue(propertiesPath,prefix + "platformName"));
        capabilities.setCapability("appium:deviceName", getPropertyValue(propertiesPath,prefix + "deviceName"));
        capabilities.setCapability("appium:automationName", getPropertyValue(propertiesPath,prefix + "automationName"));
        if(isBrowser){
            capabilities.setCapability("browserName", getPropertyValue(propertiesPath,prefix + "browserName"));
        }else{
            capabilities.setCapability("app", getPropertyValue(propertiesPath,prefix + "app"));
        }
        capabilities.setCapability("appium:platformVersion", getPropertyValue(propertiesPath, prefix + "platformVersion"));
        capabilities.setCapability("appium:noReset", getPropertyValue(propertiesPath, prefix + "noReset"));
        return capabilities;
    }


    public static void initializeMobileDriver(String propertiesPath, boolean isBrowser) throws IOException {
        String platformName = getPropertyValue(propertiesPath, "platform");
        if(platformName == null || platformName.isBlank()) throw new RuntimeException("Platform cannot be null or empty.");
        URL url = new URL(Objects.requireNonNull(getPropertyValue(propertiesPath, "appiumServer")));

        switch (platformName.toLowerCase()){
            case "android":
                mobileDriver = new AndroidDriver(url, getMobileCapabilities(propertiesPath, platformName, isBrowser));
                break;
            case "ios":
                mobileDriver = new IOSDriver(url, getMobileCapabilities(propertiesPath, platformName, isBrowser));
                break;
            default:
                throw new UnsupportedOperationException("'" + platformName + "' is not a supported platform. Supported platforms include: ios and android.");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends WebDriver> T getDriver(Class<T> targetDriver){
        String errorMessage = "Couldn't get '" + targetDriver.getSimpleName() + "' because %s == null. Call %s(String propertiesPath) first.";
        if(IOSDriver.class.isAssignableFrom(targetDriver) || AndroidDriver.class.isAssignableFrom(targetDriver)){
            Objects.requireNonNull(mobileDriver, String.format(errorMessage, "mobileDriver", "initializeMobileDriver"));
            return (T) mobileDriver;
        }else if(WebDriver.class == targetDriver){
            Objects.requireNonNull(webDriver, String.format(errorMessage, "webDriver", "initializeWebDriver"));
            return (T) webDriver;
        }else{
            throw new IllegalArgumentException(targetDriver + " is not a supported option. Supported options include: ios, android and web.");
        }
    }

    public static void quitDriver(){
        if(webDriver != null) {
            webDriver.quit();
            webDriver = null;
            LogUtil.info("Web Driver is quit and set to null.", DriverUtil.class);
        }

        if(mobileDriver != null){
            mobileDriver.quit();
            mobileDriver = null;
            LogUtil.info("Appium Driver is quit and set to null.", DriverUtil.class);
        }
    }

}
