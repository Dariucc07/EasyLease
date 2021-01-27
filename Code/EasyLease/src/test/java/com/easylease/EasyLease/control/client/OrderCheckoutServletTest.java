package com.easylease.EasyLease.control.client;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.easylease.EasyLease.model.advisor.Advisor;
import com.easylease.EasyLease.model.client.Client;
import com.easylease.EasyLease.model.order.DBOrderDAO;
import com.easylease.EasyLease.model.order.Order;
import com.easylease.EasyLease.model.order.OrderDAO;
import java.io.IOException;
import java.util.HashMap;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

/**
 * Test of the OrderCheckoutServlet class.
 *
 * @author Antonio Sarro
 * @version 0.1
 * @since 0.1
 */
class OrderCheckoutServletTest {
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

  private OrderDAO orderDAO;
  private OrderCheckoutServlet servlet;
  private final Map<String, Object> attributes = new HashMap<>();
  private Order orderStub;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servlet = new OrderCheckoutServlet();
    orderDAO = DBOrderDAO.getInstance();
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
  void orderCheckoutServletTestSuccess() throws ServletException, IOException {
    orderStub = orderDAO.retrieveById("ORbG567");
    when(request.getSession().getAttribute("user")).thenReturn(new Client());
    when(request.getParameter("submit")).thenReturn("ORbG567");
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
    assertEquals("Pagato", orderDAO.retrieveById("ORbG567").getState());
    orderDAO.update(orderStub);
  }

  @Test
  void orderCheckoutServletTestNullSession() throws ServletException, IOException {
    when(request.getSession()).thenReturn(null);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
  }

  @Test
  void orderCheckoutServletTestWrongUser() throws ServletException, IOException {
    when(request.getSession().getAttribute("user")).thenReturn(new Advisor());
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
  }

  @Test
  void orderCheckoutServletTestNullUser() throws ServletException, IOException {
    when(request.getSession().getAttribute("user")).thenReturn(null);
    servlet.doGet(request, response);
    verify(request).getRequestDispatcher("/user/homePage.jsp");
  }
}