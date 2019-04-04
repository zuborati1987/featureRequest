package com.codecool.web.servlet;

import com.codecool.web.dao.CouponDao;
import com.codecool.web.dao.ShopDao;
import com.codecool.web.dao.UserDao;
import com.codecool.web.dao.database.DatabaseCouponDao;
import com.codecool.web.dao.database.DatabaseShopDao;
import com.codecool.web.dao.database.DatabaseUserDao;
import com.codecool.web.model.Coupon;
import com.codecool.web.model.User;
import com.codecool.web.service.CouponService;
import com.codecool.web.service.LoginService;
import com.codecool.web.service.exception.ServiceException;
import com.codecool.web.service.simple.SimpleCouponService;
import com.codecool.web.service.simple.SimpleLoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/login")
public final class LoginServlet extends AbstractServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = getConnection(req.getServletContext())) {
            UserDao userDao = new DatabaseUserDao(connection);
            LoginService loginService = new SimpleLoginService(userDao);

            String email = req.getParameter("email");
            String password = req.getParameter("password");

            User user = loginService.loginUser(email, password);



            CouponDao couponDao = new DatabaseCouponDao(connection);
            ShopDao shopDao = new DatabaseShopDao(connection);
            CouponService couponService = new SimpleCouponService(couponDao, shopDao);
            List<Coupon> coupons = couponService.getCoupons(user);

            req.getSession().setAttribute("coupons", coupons);

            req.getSession().setAttribute("user", user);
            resp.sendRedirect("protected/profile");
        } catch (ServiceException ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }
}
