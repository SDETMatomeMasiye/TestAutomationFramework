package testauto.com.ui.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import testauto.com.common.LogUtil;
import java.util.Arrays;
import java.util.Objects;

import static testauto.com.common.FilesUtil.getPropertyValue;

public class DriverUtil {

    private static WebDriver driver;
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
            driver = new ChromeDriver((ChromeOptions) driverOptions);
        }else if(driverOptions instanceof EdgeOptions){
            ((EdgeOptions) driverOptions).setExperimentalOption("excludeSwitches", Arrays.asList(switches));
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver((EdgeOptions) driverOptions);
        }else if(driverOptions instanceof FirefoxOptions){
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver((FirefoxOptions) driverOptions);
        }
        Objects.requireNonNull(driver, "driver cannot be null.");
        LogUtil.info("Driver set to '" + driverOptions.getBrowserName() + "'.", DriverUtil.class);
        driver.manage().window().maximize();
    }

    public static WebDriver getWebDriver(){
        if(driver == null){
            throw new IllegalStateException("Cannot get driver because driver == null. Call initializeWebDriver(String) first.");
        }
        return driver;
    }

    public static void quitDriver(){
        if(driver != null){
            driver.quit();
        }
    }

}
