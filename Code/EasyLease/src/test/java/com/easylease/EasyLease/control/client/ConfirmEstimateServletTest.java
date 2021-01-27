package com.easylease.EasyLease.control.client;

import com.easylease.EasyLease.model.advisor.Advisor;
import com.easylease.EasyLease.model.advisor.AdvisorDAO;
import com.easylease.EasyLease.model.advisor.DBAdvisorDAO;
import com.easylease.EasyLease.model.client.Client;
import com.easylease.EasyLease.model.client.ClientDAO;
import com.easylease.EasyLease.model.client.DBClientDAO;
import com.easylease.EasyLease.model.estimate.DBEstimateDAO;
import com.easylease.EasyLease.model.estimate.Estimate;
import com.easylease.EasyLease.model.estimate.EstimateDAO;
import com.easylease.EasyLease.model.order.DBOrderDAO;
import com.easylease.EasyLease.model.order.Order;
import com.easylease.EasyLease.model.order.OrderDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfirmEstimateServletTest {
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

  private ClientDAO clientDao;
  private EstimateDAO estimateDao;
  private OrderDAO orderDao;
  private AdvisorDAO advisorDao;
  private ConfirmEstimateServlet servlet;
  private final Map<String, Object> attributes = new HashMap<>();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servlet = new ConfirmEstimateServlet();
    clientDao = DBClientDAO.getInstance();
    estimateDao = DBEstimateDAO.getInstance();
    orderDao = DBOrderDAO.getInstance();
    advisorDao = DBAdvisorDAO.getInstance();
    when(request.getServletContext()).thenReturn(context);
    when(request.getSession()).thenReturn(session);
    when(context.getContextPath()).thenReturn("");
    when(session.isNew()).thenReturn(true);
    when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

    Mockito.doAnswer((Answer<Object>) invocation -> {
      String key = (String) invocation.getArguments()[0];
      return attributes.get(key);
    }).when(session).getAttribute(anyString());

    Mockito.doAnswer((Answer<Object>) invocation -> {
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
  void confirmEstimateServlet_Confirmed_Success() throws ServletException, IOException {
    List<Order> orderList = orderDao.retrieveAll();
    Client client = clientDao.retrieveById("CLEE8BD");
    when(session.getAttribute("user")).thenReturn(client);
    when(request.getParameter("id_estimate")).thenReturn("ESdnA9G");
    when(request.getParameter("choice")).thenReturn("Confermato");
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/HistoryClientServlet");

    //rollback
    Estimate estimate = estimateDao.retrieveById("ESdnA9G");
    estimate.setState("Stipulato");
    estimate.setVisibility(true);
    estimateDao.update(estimate);
    List<Order> updatedOrders = orderDao.retrieveAll();
    for (Order item : updatedOrders) {
      boolean found = false;
      for(Order item2 : orderList){
        if (found == false && item.getId().equals(item2.getId())) {
          found = true;
        }
      }
      if(found == false){
        orderDao.delete(item);
      }
    }
  }

  @Test
  void confirmEstimateServlet_Refused_Success() throws ServletException, IOException {
    Client client = clientDao.retrieveById("CLEE8BD");
    when(session.getAttribute("user")).thenReturn(client);
    when(request.getParameter("id_estimate")).thenReturn("ESdnA9G");
    when(request.getParameter("choice")).thenReturn("Non confermato");
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/HistoryClientServlet");

    //rollback
    Estimate estimate = estimateDao.retrieveById("ESdnA9G");
    estimate.setState("Stipulato");
    estimate.setVisibility(true);
    estimateDao.update(estimate);
  }

  @Test
  void confirmEstimateServlet_WrongUser_ThrowsException() throws ServletException, IOException {
    Advisor advisor = advisorDao.retrieveById("ADJdybc");
    when(session.getAttribute("user")).thenReturn(advisor);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");

  }
}