package com.easylease.EasyLease.control.advisor;

import com.easylease.EasyLease.model.advisor.Advisor;
import com.easylease.EasyLease.model.order.DBOrderDAO;
import com.easylease.EasyLease.model.order.Order;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "OrderValidationServlet", urlPatterns = "/OrderValidationServlet")
public class OrderValidationServlet extends HttpServlet {
  SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
  SimpleDateFormat htmlFormat = new SimpleDateFormat("yyyy/MM/dd");
  private final Logger logger = Logger.getLogger(OrderValidationServlet.class.getName());
  @Override
  protected void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    if(session != null){
      try{
        if (!(session.getAttribute("user") instanceof Advisor)
            || session.getAttribute("user") == null) {
          throw new ServletException("Section dedicated to a registered user"
              + "on the platform correctly as an Advisor");
        }
        String id = request.getParameter("id");
        if(id.length() != 7 || !id.startsWith("OR")){
          throw new ServletException("The id sent is incorrect");
        }
        DBOrderDAO dbOrderDao = (DBOrderDAO) DBOrderDAO.getInstance();
        Order order = dbOrderDao.retrieveById(id);
        if(order == null){
          throw new ServletException("The order doesn't exist");
        }
        if(!order.getState().equals("Pagato")){
          throw new ServletException("The chosen order cannot be validated");
        }
        order.setPickupDate(format.parse(format.format(
            htmlFormat.parse(request.getParameter("date")))));
        order.setStartDate(order.getPickupDate());
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTime(order.getPickupDate());
        endDate.add(Calendar.MONTH, order.getEstimate().getPeriod());
        order.setEndDate(format.parse(String.valueOf(endDate)));
        System.out.println(format.format(endDate));
        order.setState("Convalidato");
        dbOrderDao.update(order);
        //TODO cambiare la redirect dopo aver aggiunto i controlli sulla jsp
        request.getRequestDispatcher("/advisor/historyAdvisorJSP.jsp")
            .forward(request, response);
      } catch(ServletException | ParseException e) {
        logger.log(Level.SEVERE, e.getMessage());
        request.getRequestDispatcher("/user/homePageJSP.jsp").forward(request, response);
      }
    }
  }

  @Override
  protected void doPost(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
