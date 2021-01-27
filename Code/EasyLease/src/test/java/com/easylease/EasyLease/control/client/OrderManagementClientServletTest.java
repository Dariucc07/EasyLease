package com.easylease.EasyLease.control.client;

import com.easylease.EasyLease.model.client.Client;
import com.easylease.EasyLease.model.client.ClientDAO;
import com.easylease.EasyLease.model.client.DBClientDAO;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestOrderManagementClientServlet {
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
  private OrderManagementClientServlet servlet;
  private final Map<String, Object> attributes = new HashMap<>();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servlet = new OrderManagementClientServlet();
    clientDao = DBClientDAO.getInstance();
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
      attributes.put(key,value);
      return null;
    }).when(session).setAttribute(anyString(), any());
  }

  @AfterEach
  void tearDown(){

  }

  @Test
  void orderManagementClientServletTest_Success() throws ServletException, IOException {
    Client client = clientDao.retrieveById("CLEE8BD");
    when(session.getAttribute("user")).thenReturn(client);
    when(request.getParameter("id_order")).thenReturn("ORhfga2");
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/client/orderManagementClientJSP.jsp");
  }

  @Test
  void orderManagementClientServletTest_WrongUser_ExceptionThrown() throws ServletException, IOException {
    when(session.getAttribute("user")).thenReturn("ADgjksf");
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }

  @Test
  void orderManagementClientServletTest_WrongId_ExceptionThrown() throws ServletException, IOException {
    Client client = clientDao.retrieveById("CLEE8BD");
    when(session.getAttribute("user")).thenReturn(client);
    when(request.getParameter("id_order")).thenReturn("HHhKs85");
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }

  @Test
  void orderManagementClientServletTest_ShortId_ExceptionThrown() throws ServletException, IOException {
    Client client = clientDao.retrieveById("CLEE8BD");
    when(session.getAttribute("user")).thenReturn(client);
    when(request.getParameter("id_order")).thenReturn("HH");
    servlet.doPost(request, response);
    verify(request).getRequestDispatcher("/user/homePageJSP.jsp");
  }
}