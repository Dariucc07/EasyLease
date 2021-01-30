package com.easylease.EasyLease.control.client;

import com.easylease.EasyLease.control.utility.IdGenerator;
import com.easylease.EasyLease.model.car.Car;
import com.easylease.EasyLease.model.car.DbCarDao;
import com.easylease.EasyLease.model.client.Client;
import com.easylease.EasyLease.model.estimate.DbEstimateDao;
import com.easylease.EasyLease.model.estimate.Estimate;
import com.easylease.EasyLease.model.optional.DbOptionalDao;
import com.easylease.EasyLease.model.optional.Optional;
import com.easylease.EasyLease.model.optional.OptionalDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "RequestEstimateServlet", value = "/RequestEstimateServlet")
public class RequestEstimateServlet extends HttpServlet {

  protected void doPost(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    String role = (String) request.getSession().getAttribute("role");
    if (role == null || (role != null && !role.equals("client"))) {
      request.getRequestDispatcher("/user/homePage.jsp").forward(request, response);
    }
    else {
      Client user = (Client) request.getSession().getAttribute("user");
      String carId = request.getParameter("carId");
      String m = request.getParameter("Mesi");

      if (m==null || user == null || carId == null ) {
        request.getRequestDispatcher("/user/homePage.jsp").forward(request, response);
      }

      else {
        int months = Integer.parseInt(m);
        String[] optionals = request.getParameterValues("optionals");
        List<Optional> optionalList = new ArrayList<>();
        if (optionals != null && optionals.length != 0) {
          OptionalDao optDao = DbOptionalDao.getInstance();
          for (String optionalId : optionals) {
            optionalList.add(optDao.retrieveById(optionalId));
          }
        }
        Car car = DbCarDao.getInstance().retrieveById(carId);
        String id = "ES" + IdGenerator.randomIdGenerator();
        while (DbEstimateDao.getInstance().retrieveById(id) != null) {
          id = "ES" + IdGenerator.randomIdGenerator();
        }

        Estimate estimate = new Estimate(id, 0, (Client) user, null, car,
            months, optionalList, true, "Attesa",
            new Date(System.currentTimeMillis()), null);

        DbEstimateDao.getInstance().insert(estimate);

        request.getRequestDispatcher("/user/homePage.jsp")
            .forward(request, response);
      }
    }
  }

  protected void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }
}
