package com.easylease.EasyLease.control.client;

import com.easylease.EasyLease.model.client.Client;
import com.easylease.EasyLease.model.estimate.Estimate;
import com.easylease.EasyLease.model.order.DBOrderDAO;
import com.easylease.EasyLease.model.order.Order;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet(name = "ConfirmOrderServlet", urlPatterns = "/ConfirmOrderServlet")
public class ConfirmOrderServlet extends HttpServlet {
  private DBOrderDAO orderDao = (DBOrderDAO) DBOrderDAO.getInstance();

  protected void doPost(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  protected void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    try {
      if (!(session.getAttribute("user") instanceof Client)) {
        throw new ServletException("Section dedicated to a registered user "
            + "on the platform correctly as a Client");
      }
      String choice = request.getParameter("choice");
      String idOrdine = request.getParameter("id_order");
      Order order = orderDao.retrieveById(idOrdine);
      if (choice.equals("Confermato")) {
        request.setAttribute("order", order);
        order.setState(choice);
        orderDao.update(order);
        request.getRequestDispatcher("/client/orderCheckout.jsp")
            .forward(request, response);
      } else if (choice.equals("Non confermato")) {
        order.setState(choice);
        order.setVisibility(false);
        orderDao.update(order);
        request.getRequestDispatcher("/HistoryClientServlet")
            .forward(request, response);
      } else if (choice.equals("Paga")) {
        request.setAttribute("order", order);
        request.getRequestDispatcher("/client/orderCheckout.jsp")
            .forward(request, response);
      }
    } catch (ServletException e) {
      Logger logger = Logger.getLogger(
          EstimateManagementClientServlet.class.getName());
      logger.log(Level.SEVERE, e.getMessage());
      request.getRequestDispatcher("/user/homePage.jsp");
    }
  }
}

