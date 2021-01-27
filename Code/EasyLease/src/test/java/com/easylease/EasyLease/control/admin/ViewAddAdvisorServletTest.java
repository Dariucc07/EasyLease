package com.easylease.EasyLease.control.admin;

import static org.mockito.Mockito.*;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.servlet.http.HttpSession;

class ViewAddAdvisorServletTest {
    private ViewAddAdvisorServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private ServletContext context;
    private ServletConfig config;

    @BeforeEach
    public void setUp() throws IOException, ServletException {
        servlet = new ViewAddAdvisorServlet();
        config =mock(ServletConfig.class);
        servlet.init(config);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session= mock(HttpSession.class);
        context =mock(ServletContext.class);
        dispatcher= mock(RequestDispatcher.class);
        when(request.getSession()).thenReturn(session);
        when(servlet.getServletContext()).thenReturn(context);
        when(context.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void RoleNull() throws ServletException, IOException {
        when(request.getSession().getAttribute("role")).thenReturn(null);

        servlet.doGet(request,response);
        verify(context).getRequestDispatcher("/fragments/error403.jsp");
    }

    @Test
    void UserNotAdmin() throws ServletException, IOException {
        when(request.getSession().getAttribute("role")).thenReturn("client");

        servlet.doGet(request,response);
        verify(context).getRequestDispatcher("/fragments/error403.jsp");
    }

    @Test
    void doGet() throws ServletException, IOException {
        when(request.getSession().getAttribute("role")).thenReturn("admin");

        servlet.doGet(request,response);
        verify(context).getRequestDispatcher("/admin/addAdvisorJSP.jsp");
    }
}