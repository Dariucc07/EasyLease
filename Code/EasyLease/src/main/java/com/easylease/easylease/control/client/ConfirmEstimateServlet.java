package com.easylease.easylease.control.client;

import com.easylease.easylease.control.utility.IdGenerator;
import com.easylease.easylease.model.client.Client;
import com.easylease.easylease.model.estimate.DbEstimateDao;
import com.easylease.easylease.model.estimate.Estimate;
import com.easylease.easylease.model.order.DbOrderDao;
import com.easylease.easylease.model.order.Order;
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

/**
 * This servlet takes care of confirming a customer's estimate,
 * creating a specific order for that estimate.
 *
 * @author Mattia Mori
 * @version 0.3
 * @since 0.1
 */
@WebServlet(name = "ConfirmEstimateServlet",
    urlPatterns = "/ConfirmEstimateServlet")
public class ConfirmEstimateServlet extends HttpServlet {
  private DbEstimateDao estimateDao = (DbEstimateDao) DbEstimateDao.getInstance();
  private DbOrderDao orderDao = (DbOrderDao) DbOrderDao.getInstance();

  protected void doPost(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    try {
      if (!(session.getAttribute("user") instanceof Client)) {
        throw new ServletException("Section dedicated to a registered user "
            + "on the platform correctly as a Client");
      }
      String estimateId = request.getParameter("id_estimate");
      String choice = request.getParameter("choice");
      Estimate estimate = estimateDao.retrieveById(estimateId);
      if (choice.equals("Confermato")) {
        estimate.setState("Confermato");
        estimate.setVisibility(false);
        estimateDao.update(estimate);
        Order order = new Order();
        String tryId = "";
        boolean checked = false;
        while (checked == false) {
          tryId = "OR" + IdGenerator.randomIdGenerator();
          if (orderDao.retrieveById(tryId) == null) {
            checked = true;
          }
        }
        order.setIdOrder("OR" + IdGenerator.randomIdGenerator());
        session.setAttribute("id_order", order.getIdOrder());
        order.setState("Attesa");
        Date date = new Date();
        order.setStartDate(date);
        order.setEstimate(estimate);
        order.setVisibility(true);
        order.setState("Attesa");
        order.setCreationDate(new Date());
        orderDao.insert(order);
        request.getRequestDispatcher("/HistoryClientServlet")
            .forward(request, response);
      }
      if (choice.equals("Non confermato")) {
        estimate.setState("Non Confermato");
        estimateDao.update(estimate);
        request.getRequestDispatcher("/HistoryClientServlet")
            .forward(request, response);
      }
    } catch (ServletException e) {
      Logger logger = Logger.getLogger(
          ConfirmEstimateServlet.class.getName());
      logger.log(Level.SEVERE, e.getMessage());
      request.getRequestDispatcher("/user/homePage.jsp");
    }
  }

  protected void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }
}
