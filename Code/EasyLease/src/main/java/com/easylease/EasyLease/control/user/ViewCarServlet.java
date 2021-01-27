package com.easylease.EasyLease.control.user;

import com.easylease.EasyLease.model.car.Car;
import com.easylease.EasyLease.model.car.DBCarDAO;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "ViewCarServlet", value = "/ViewCarServlet")
public class ViewCarServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    DBCarDAO dao = (DBCarDAO) DBCarDAO.getInstance();
    String model = request.getParameter("model");
    request.removeAttribute("model");
    Car car;
    car = dao.retrieveByModel(model);
    request.setAttribute("car", car);
    request.getRequestDispatcher("/user/viewCarJSP.jsp")
        .forward(request, response);
  }
}
