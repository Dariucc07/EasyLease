package com.easylease.easylease.control.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.easylease.easylease.model.DBPool.DbConnection;
import com.easylease.easylease.model.client.Client;
import com.easylease.easylease.model.estimate.DbEstimateDao;
import com.easylease.easylease.model.estimate.Estimate;
import com.easylease.easylease.model.estimate.EstimateDao;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


class RequestEstimateServletTest {
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private ServletContext context;
  @Mock
  private HttpSession session;
  @Mock
  private RequestDispatcher dispatcher;

  private EstimateDao dbEstimate;
  private RequestEstimateServlet servlet;
  private final Map<String, Object> attributes = new HashMap<>();
  private static DbConnection dbConnection;

  @BeforeEach
  void setUp() throws SQLException {
    MockitoAnnotations.openMocks(this);
    servlet = new RequestEstimateServlet();
    dbConnection = DbConnection.getInstance();
    MysqlDataSource mysqlDataSource = new MysqlDataSource();
    mysqlDataSource.setURL("jdbc:mysql://localhost:3306/easylease");
    mysqlDataSource.setUser("root");
    mysqlDataSource.setPassword("root");
    mysqlDataSource.setServerTimezone("UTC");
    mysqlDataSource.setVerifyServerCertificate(false);
    mysqlDataSource.setUseSSL(false);

    dbConnection.setDataSource(mysqlDataSource);
    dbEstimate = DbEstimateDao.getInstance();
    when(request.getServletContext()).thenReturn(context);
    when(request.getSession()).thenReturn(session);
    when(context.getContextPath()).thenReturn("");
    when(session.isNew()).thenReturn(true);
    when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void doGetAllParameters() throws ServletException, IOException {
    List<String> estimateOldList = new ArrayList<>();
    for (Estimate e : dbEstimate.retrieveAll()) {
      estimateOldList.add(e.getIdEstimate());
    }
    Client client = new Client();
    client.setIdUser("CLEE8BD");
    String[] optionals = {"OPUi78M", "OPhbN65"};
    when(request.getSession().getAttribute("role")).thenReturn("client");
    when(request.getSession().getAttribute("user")).thenReturn(client);
    when(request.getParameter("carId")).thenReturn("CA6qSDe");
    when(request.getParameter("Mesi")).thenReturn("24");
    when(request.getParameterValues("optionals")).thenReturn(optionals);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    List<Estimate> estimateNewList = dbEstimate.retrieveAll();
    assertEquals(estimateOldList.size() + 1, estimateNewList.size());

    //rollback
    for (Estimate estimate : estimateNewList) {
      if (!estimateOldList.contains(estimate.getIdEstimate())) {
        dbEstimate.delete(estimate);
      }
    }
  }

  @Test
  void doGetNoOptional() throws ServletException, IOException {
    List<String> estimateOldList = new ArrayList<>();
    for (Estimate e : dbEstimate.retrieveAll()) {
      estimateOldList.add(e.getIdEstimate());
    }
    Client client = new Client();
    client.setIdUser("CLEE8BD");
    String[] optionals = {};
    when(request.getSession().getAttribute("role")).thenReturn("client");
    when(request.getSession().getAttribute("user")).thenReturn(client);
    when(request.getParameter("carId")).thenReturn("CA6qSDe");
    when(request.getParameter("Mesi")).thenReturn("24");
    when(request.getParameterValues("optionals")).thenReturn(optionals);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    List<Estimate> estimateNewList = dbEstimate.retrieveAll();
    assertEquals(estimateOldList.size() + 1, estimateNewList.size());

    //rollback
    for (Estimate estimate : estimateNewList) {
      if (!estimateOldList.contains(estimate.getIdEstimate())) {
        dbEstimate.delete(estimate);
      }
    }
  }

  @Test
  void doGetWrongRole() throws ServletException, IOException {
    List<String> estimateOldList = new ArrayList<>();
    for (Estimate e : dbEstimate.retrieveAll()) {
      estimateOldList.add(e.getIdEstimate());
    }
    when(request.getSession().getAttribute("role")).thenReturn("admin");
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    List<Estimate> estimateNewList = dbEstimate.retrieveAll();
    assertEquals(estimateOldList.size(), estimateNewList.size());
  }

  @Test
  void doGetNoRole() throws ServletException, IOException {
    List<String> estimateOldList = new ArrayList<>();
    for (Estimate e : dbEstimate.retrieveAll()) {
      estimateOldList.add(e.getIdEstimate());
    }
    when(request.getSession().getAttribute("role")).thenReturn(null);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    List<Estimate> estimateNewList = dbEstimate.retrieveAll();
    assertEquals(estimateOldList.size(), estimateNewList.size());
  }

  @Test
  void doGetFailClientNull() throws ServletException, IOException {
    List<String> estimateOldList = new ArrayList<>();
    for (Estimate e : dbEstimate.retrieveAll()) {
      estimateOldList.add(e.getIdEstimate());
    }
    Client client = new Client();
    client.setIdUser("CLEE8BD");
    String[] optionals = {};
    when(request.getSession().getAttribute("role")).thenReturn("client");
    when(request.getSession().getAttribute("user")).thenReturn(null);
    when(request.getParameter("carId")).thenReturn("CA6qSDe");
    when(request.getParameter("Mesi")).thenReturn("24");
    when(request.getParameterValues("optionals")).thenReturn(optionals);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    List<Estimate> estimateNewList = dbEstimate.retrieveAll();
    assertEquals(estimateOldList.size(), estimateNewList.size());
  }


  @Test
  void doGetNoMonths() throws ServletException, IOException {
    List<String> estimateOldList = new ArrayList<>();
    for (Estimate e : dbEstimate.retrieveAll()) {
      estimateOldList.add(e.getIdEstimate());
    }
    Client client = new Client();
    client.setIdUser("CLEE8BD");
    String[] optionals = {};
    when(request.getSession().getAttribute("role")).thenReturn("client");
    when(request.getSession().getAttribute("user")).thenReturn(client);
    when(request.getParameter("carId")).thenReturn("CA6qSDe");
    when(request.getParameter("Mesi")).thenReturn(null);
    when(request.getParameterValues("optionals")).thenReturn(optionals);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    List<Estimate> estimateNewList = dbEstimate.retrieveAll();
    assertEquals(estimateOldList.size(), estimateNewList.size());
  }

  @Test
  void doGetNoCar() throws ServletException, IOException {
    List<String> estimateOldList = new ArrayList<>();
    for (Estimate e : dbEstimate.retrieveAll()) {
      estimateOldList.add(e.getIdEstimate());
    }
    Client client = new Client();
    client.setIdUser("CLEE8BD");
    String[] optionals = {};
    when(request.getSession().getAttribute("role")).thenReturn("client");
    when(request.getSession().getAttribute("user")).thenReturn(client);
    when(request.getParameter("carId")).thenReturn(null);
    when(request.getParameter("Mesi")).thenReturn("24");
    when(request.getParameterValues("optionals")).thenReturn(optionals);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    List<Estimate> estimateNewList = dbEstimate.retrieveAll();
    assertEquals(estimateOldList.size(), estimateNewList.size());
  }

}