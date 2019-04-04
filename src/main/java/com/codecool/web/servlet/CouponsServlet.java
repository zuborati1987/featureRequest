package com.codecool.web.servlet;

import com.codecool.web.dao.CouponDao;
import com.codecool.web.dao.ShopDao;
import com.codecool.web.dao.database.DatabaseCouponDao;
import com.codecool.web.dao.database.DatabaseShopDao;
import com.codecool.web.model.Coupon;
import com.codecool.web.model.User;
import com.codecool.web.service.CouponService;
import com.codecool.web.service.exception.ServiceException;
import com.codecool.web.service.simple.SimpleCouponService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/couponsservlet")
public final class CouponsServlet extends AbstractServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = getConnection(req.getServletContext())) {
            CouponDao couponDao = new DatabaseCouponDao(connection);
            ShopDao shopDao = new DatabaseShopDao(connection);
            CouponService couponService = new SimpleCouponService(couponDao, shopDao);
            User user =  getCurrentUser(req);
            List<Coupon> coupons = couponService.getCoupons(user);

            req.setAttribute("coupons", coupons);
            req.getRequestDispatcher("coupons.jsp").forward(req, resp);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = getConnection(req.getServletContext())) {
            CouponDao couponDao = new DatabaseCouponDao(connection);
            ShopDao shopDao = new DatabaseShopDao(connection);
            CouponService couponService = new SimpleCouponService(couponDao, shopDao);

            String name = req.getParameter("name");
            String percentage = req.getParameter("percentage");

            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            int id = user.getId();
            Coupon coupon = couponService.addCoupon(name, percentage, id);

            String info = String.format("Coupon %s with id %s has been created", coupon.getName(), coupon.getId());
            req.setAttribute("info", info);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        } catch (ServiceException ex) {
            req.setAttribute("error", ex.getMessage());
        }
        doGet(req, resp);
    }

    private User getCurrentUser(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        return (User) session.getAttribute("user");
    }
}
