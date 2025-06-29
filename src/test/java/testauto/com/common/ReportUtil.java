package testauto.com.common;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.restassured.response.Response;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import testauto.com.ui.utils.DriverUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ReportUtil {

    public static ExtentReports initializeReport(Class<?> testClass){
        ExtentReports reports = null;
        String reportName = getReportName(testClass);
        try{
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportName);
            reports = new ExtentReports();
            reports.attachReporter(sparkReporter);
            LogUtil.info("Initialized report to destination '" + reportName + "'.", ReportUtil.class);
        }catch (Exception e){
            LogUtil.error("Couldn't initialize report for '" + testClass.getSimpleName() + "'.", ReportUtil.class, e);
        }
        Objects.requireNonNull(reports, "Reports cannot be null at this point.");
        return reports;
    }

    private static String getReportName(Class<?> testClass){
        String environment = System.getProperty("execution-environment");
        String timeStamp;

        if(environment == null || environment.isBlank()){
            throw new IllegalArgumentException("Execution environment cannot be null. Pass it as a VM option with key 'execution-environment'.");
        }

        timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        LogUtil.info("Set timestamp: " + timeStamp + ".", ReportUtil.class);
        return  "target/reports/" + testClass.getSimpleName()  + "/" + environment + "/automation_" + timeStamp + ".html";
    }

    public static void report(String status, String message, ExtentTest node){
        String platform = System.getProperty("platform");
        if(node == null) throw new IllegalArgumentException("node cannot be null.");
        if(platform == null || platform.isBlank()) throw new IllegalArgumentException("Platform cannot be null. Pass it as a VM option with key 'platform'.");
        if(status.equalsIgnoreCase("pass")){
            node.pass(message, takeScreenshot(platform));
        }else if(status.equalsIgnoreCase("fail")){
            node.fail(message, takeScreenshot(platform));
        }else if(status.equalsIgnoreCase("info")){
            node.info(message, takeScreenshot(platform));
        }else {
            throw new IllegalArgumentException("'" + status + "' is not a supported status.");
        }
    }

    public static void reportAPI(String status, String message, ExtentTest node, Response response){
        if(node == null) throw new IllegalArgumentException("node cannot be null.");
        if(status.equalsIgnoreCase("pass")){
            node.pass(message);
        }else if(status.equalsIgnoreCase("fail")){
            node.fail(message);
        }else if(status.equalsIgnoreCase("info")){
            node.info(message);
        }else {
            throw new IllegalArgumentException("'" + status + "' is not a supported status.");
        }
        node.info(MarkupHelper.createCodeBlock(response.asString()));
    }

    private static Media takeScreenshot(String platform){
        String screenshot = switch (platform.toLowerCase()) {
            case "web" -> ((TakesScreenshot) DriverUtil.getDriver(WebDriver.class)).getScreenshotAs(OutputType.BASE64);
            case "ios" -> DriverUtil.getDriver(IOSDriver.class).getScreenshotAs(OutputType.BASE64);
            case "android" -> DriverUtil.getDriver(AndroidDriver.class).getScreenshotAs(OutputType.BASE64);
            default -> throw new IllegalArgumentException("'" + platform + "' is not a supported platform");
        };
        return MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot).build();
    }
}
