package com.easylease.easylease.control.advisor;

import com.easylease.easylease.control.utility.EmailManager;
import com.easylease.easylease.model.advisor.Advisor;
import com.easylease.easylease.model.order.DbOrderDao;
import com.easylease.easylease.model.order.Order;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *  This Servlet has the task to validate or not an order.
 *
 * @author Caprio Mattia
 * @version 0.9
 * @since 0.1
 */
@WebServlet(name = "OrderValidationServlet", urlPatterns = "/OrderValidationServlet")

public class OrderValidationServlet extends HttpServlet {
  SimpleDateFormat htmlFormat = new SimpleDateFormat("yyyy-MM-dd");
  private final Logger logger = Logger.getLogger(
      OrderValidationServlet.class.getName());

  @Override
  protected void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    if (session != null) {
      try {
        if (!(session.getAttribute("user") instanceof Advisor)) {
          throw new ServletException("Section dedicated to a registered user"
              + " on the platform correctly as an Advisor");
        }
        String id = request.getParameter("id");
        if (id == null || id.length() != 7 || !id.startsWith("OR")) {
          throw new ServletException("The id sent is incorrect");
        }
        DbOrderDao dbOrderDao = (DbOrderDao) DbOrderDao.getInstance();
        Order order = dbOrderDao.retrieveById(id);
        if (order == null) {
          throw new ServletException("The order doesn't exist");
        }
        if (!order.getState().equals("Pagato")) {
          throw new ServletException("The chosen order cannot be validated");
        }
        if (Boolean.parseBoolean(request.getParameter("choice")) == false) {
          order.setState("Non convalidato");
          dbOrderDao.update(order);
          request.setAttribute("order", order);
          request.getRequestDispatcher("/advisor/orderManagementAdvisor.jsp")
              .forward(request, response);
          return;
        }
        Date startDate = null;
        try {
          startDate = htmlFormat.parse(request.getParameter("date"));
        } catch (ParseException e) {
          request.getRequestDispatcher("/advisor/orderValidation.jsp")
              .forward(request, response);
        }
        order.setStartDate(startDate);
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTime(order.getStartDate());
        endDate.add(Calendar.MONTH, order.getEstimate().getPeriod());
        order.setEndDate(endDate.getTime());
        order.setState("Convalidato");
        dbOrderDao.update(order);
        request.setAttribute("order", order);
        EmailManager.sendOrderNotification(order.getEstimate().getClient(), order);
        request.getRequestDispatcher("/advisor/orderManagementAdvisor.jsp")
            .forward(request, response);
      } catch (ServletException | MessagingException e) {
        logger.log(Level.SEVERE, e.getMessage());
        request.getRequestDispatcher("/user/homePage.jsp")
            .forward(request, response);
      }
    } else {
      request.getRequestDispatcher("/user/homePage.jsp")
          .forward(request, response);
    }
  }

  @Override
  protected void doPost(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
