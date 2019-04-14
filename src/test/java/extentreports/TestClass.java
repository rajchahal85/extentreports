package extentreports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.utils.FileUtil;
import freemarker.template.SimpleDate;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestClass {
    public WebDriver driver;

    public ExtentHtmlReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest test;

    @BeforeClass
    public void setupBrowser()
    {
        driver = new ChromeDriver();
        driver.get("https://www.amazon.com");
        driver.manage().window().maximize();


    }

    @BeforeTest
    public void setExtent()
    {
        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/myReport.html");
        htmlReporter.config().setDocumentTitle("Automation Report");
        htmlReporter.config().setReportName("Functional test report");
        htmlReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Hostname", "Localhost");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Tester", "Raj Chahal");
        extent.setSystemInfo("Browser", "Chrome");
    }
    @Test
    public void verifyTitleTest()
    {
        test = extent.createTest("Verify Title Test");
        Assert.assertEquals(driver.getTitle(), "amazon");
    }

    @Test
    public void getLogoTest()
    {
        test = extent.createTest("getLogo Test");
        Assert.assertTrue(true);
    }

@AfterTest
public void endReport()
{
    extent.flush();
}
    @AfterMethod
    public void tearDown(ITestResult result)
    {
        if(result.getStatus() == ITestResult.FAILURE)
        {
            test.log(Status.FAIL, "Test Case failed is : " + result.getName()); //to add name in extent report
            test.log(Status.FAIL, "Test Case failed is : " + result.getThrowable()); //to add error to report

            String screenShotPath = TestClass.getScreenshot(driver, result.getName());
            System.out.println(screenShotPath);
            try {
                //test.addScreenCaptureFromPath(screenShotPath); // adding screenshot
                test.addScreenCaptureFromPath(screenShotPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if(result.getStatus() == ITestResult.SUCCESS)
        {
            test.log(Status.PASS, "Test Case Passed is : " + result.getName());
        }

        extent.flush();
    }

    private static String getScreenshot(WebDriver driver, String screenshotName) {
        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot)driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        //After execution folder will be created
        String destination = System.getProperty("user.dir") + "/test-output/" + screenshotName + dateName + ".png";
        File finalDestination = new File(destination);
        try {
            FileUtils.copyFile(source, finalDestination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  destination;
    }

}
