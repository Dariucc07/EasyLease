package com.easylease.EasyLease.control.advisor;

import com.easylease.EasyLease.model.advisor.Advisor;
import com.easylease.EasyLease.model.advisor.DBAdvisorDAO;
import com.easylease.EasyLease.model.client.Client;
import com.easylease.EasyLease.model.estimate.DBEstimateDAO;
import com.easylease.EasyLease.model.estimate.Estimate;
import com.easylease.EasyLease.model.optional.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * @author Caprio Mattia
 * @version 0.1
 * @since 0.1
 */
class EstimateStipulationServletTest {

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

  private final DBAdvisorDAO dbAdvisorDAO = (DBAdvisorDAO) DBAdvisorDAO.getInstance();
  private DBEstimateDAO dbEstimateDAO;
  private EstimateStipulationServlet servlet;
  private final Map<String, Object> attributes = new HashMap<>();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servlet = new EstimateStipulationServlet();
    dbEstimateDAO = (DBEstimateDAO) DBEstimateDAO.getInstance();
    when(request.getServletContext()).thenReturn(context);
    when(request.getSession()).thenReturn(session);
    when(context.getContextPath()).thenReturn("");
    when(session.isNew()).thenReturn(true);
    when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

    doAnswer((Answer<Object>) invocation -> {
      String key = (String) invocation.getArguments()[0];
      attributes.get(key);
      return null;
    }).when(session).getAttribute(anyString());

    doAnswer((Answer<Object>) invocation -> {
      String key = (String) invocation.getArguments()[0];
      Object value = invocation.getArguments()[1];
      attributes.put(key, value);
      return null;
    }).when(session).setAttribute(anyString(), any());
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void SuccessTakenState() throws ServletException, IOException {
    Estimate estimate = dbEstimateDAO.retrieveById("ESfn9IO");
    when(request.getSession().getAttribute("user")).thenReturn(new Advisor());
    when(request.getParameter("id")).thenReturn("ESfn9IO");
    for (Optional o : estimate.getOptionalList()) {
      when(request.getParameter(o.getName())).thenReturn("100.00");
    }
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher(
        "/advisor/estimateManagementAdvisorJSP.jsp");
    assertEquals("Stipulato", dbEstimateDAO.retrieveById("ESfn9IO").getState());
    dbEstimateDAO.update(estimate);
  }

  @Test
  void SuccessStipulatedState() throws ServletException, IOException {
    when(request.getSession().getAttribute("user")).thenReturn(new Advisor());
    when(request.getParameter("id")).thenReturn("ESdnA9G");
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }

  @Test
  void nullSession() throws ServletException, IOException {
    when(request.getSession()).thenReturn(null);
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }

  @Test
  void wrongUserGiven() throws ServletException, IOException {
    when(request.getSession().getAttribute("user")).thenReturn(new Client());
    when(request.getParameter("id")).thenReturn("ESgY65R");
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }

  @Test
  void wrongEstimateGiven() throws ServletException, IOException {
    when(request.getSession().getAttribute("user")).thenReturn(
        dbAdvisorDAO.retrieveById("ADJdybc"));
    when(request.getParameter("id")).thenReturn(null);
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }

  @Test
  void nullEstimateGiven() throws ServletException, IOException {
    when(request.getSession().getAttribute("user")).thenReturn(
        dbAdvisorDAO.retrieveById("ADJdybc"));
    when(request.getParameter("id")).thenReturn("ESxxxxx");
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }
}