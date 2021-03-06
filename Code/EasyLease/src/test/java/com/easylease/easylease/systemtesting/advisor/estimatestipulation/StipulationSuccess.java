package com.easylease.easylease.systemtesting.advisor.estimatestipulation;

import com.easylease.easylease.model.DBPool.DbConnection;
import com.easylease.easylease.model.estimate.DbEstimateDao;
import com.easylease.easylease.model.estimate.Estimate;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.concurrent.TimeUnit;

/**
 * @author Caprio Mattia
 *
 * @since 0.1
 * @version 0.2
 */
public class StipulationSuccess {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private static DbConnection dbConnection = DbConnection.getInstance();
  private Estimate estimate = DbEstimateDao.getInstance()
      .retrieveById("ESjg9I7");

  @BeforeAll
  static void init() throws Exception {
    dbConnection = DbConnection.getInstance();
    MysqlDataSource mysqlDataSource = new MysqlDataSource();
    mysqlDataSource.setURL("jdbc:mysql://localhost:3306/easylease");
    mysqlDataSource.setUser("root");
    mysqlDataSource.setPassword("root");
    mysqlDataSource.setServerTimezone("UTC");
    mysqlDataSource.setVerifyServerCertificate(false);
    mysqlDataSource.setUseSSL(false);
    dbConnection.setDataSource(mysqlDataSource);
  }

  @BeforeEach
  public void setUp() throws Exception {
    System.setProperty("webdriver.edge.driver",
        "src/test/java/com/easylease/EasyLease/systemtesting/msedgedriver.exe");
    driver = new EdgeDriver();
    baseUrl = "https://www.google.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  @DisplayName("ST_ADVISOR_2_07")
  public void testStipulationSuccess() throws Exception {
    driver.get("http://localhost:8080/EasyLease_war_exploded/HomePageServlet");
    driver.findElement(By.linkText("Login")).click();
    driver.findElement(By.id("email")).click();
    driver.findElement(By.id("email")).clear();
    driver.findElement(By.id("email")).sendKeys("marcoGreco@easylease.com");
    driver.findElement(By.id("password")).click();
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys("pass");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    driver.findElement(By.xpath("//a[contains(@href, '#')]")).click();
    driver.findElement(By.linkText("Ordini e Preventivi")).click();
    Thread.sleep(500);
    driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
    Thread.sleep(500);
    driver.findElement(By.id("ESjg9I7")).click();
    Thread.sleep(500);
    driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
    Thread.sleep(500);
    driver.findElement(By.linkText("Stipula")).click();
    Thread.sleep(500);
    driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
    Thread.sleep(500);
    driver.findElement(By.id("Vetri brillantinati in madreperla")).click();
    driver.findElement(By.id("Vetri brillantinati in madreperla")).clear();
    driver.findElement(By.id("Vetri brillantinati in madreperla"))
        .sendKeys("120");
    driver.findElement(By.id("Assicurazione furto")).click();
    driver.findElement(By.id("Assicurazione furto")).clear();
    driver.findElement(By.id("Assicurazione furto")).sendKeys("100");
    driver.findElement(By.id("stipulate")).click();
    Thread.sleep(500);
    driver.findElement(By.cssSelector("body"))
        .sendKeys(Keys.CONTROL, Keys.HOME);
    Thread.sleep(500);
    driver.findElement(By.xpath("//a[contains(@href, '#')]")).click();
    driver.findElement(By.linkText("Logout")).click();
  }

  @AfterEach
  public void tearDown() throws Exception {
    DbEstimateDao.getInstance().update(estimate);
    driver.quit();
  }
}
