package com.codecool.web.dao.database;

import com.codecool.web.dao.CouponDao;
import com.codecool.web.model.Coupon;
import com.codecool.web.model.Shop;
import com.codecool.web.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseCouponDao extends AbstractDao implements CouponDao {

    public DatabaseCouponDao(Connection connection) {
        super(connection);
    }

    @Override
    public List<Coupon> findAll(User user) throws SQLException {
        int this_user = user.getId();
        List<Coupon> coupons = new ArrayList<>();
        String sql = "SELECT id, name, percentage, user_id FROM coupons WHERE user_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            statement.setInt(1, this_user);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                coupons.add(fetchCoupon(resultSet));
            }
            return coupons;
        }
    }


    public List<Coupon> findByShop(User user, Shop shop) throws SQLException {
        int this_user = user.getId();
        int this_shop = shop.getId();
        List<Coupon> coupons = new ArrayList<>();
        String sql = "SELECT coupons.id, coupons.name, percentage, user_id FROM coupons INNER JOIN coupons_shops ON coupons_shops.coupon_id = coupons.id INNER JOIN shops ON coupons_shops.shop_id = shops.id where shop_id = ? and user_id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            statement.setInt(1, this_shop);
            statement.setInt(2, this_user);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                coupons.add(fetchCoupon(resultSet));
            }
            return coupons;
        }
    }

    @Override
    public Coupon findById(int id) throws SQLException {
        String sql = "SELECT id, name, percentage, user_id FROM coupons WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return fetchCoupon(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Coupon add(String name, int percentage, int user_id) throws SQLException {
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO coupons (name, percentage, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, name);
            statement.setInt(2, percentage);
            statement.setInt(3, user_id);
            executeInsert(statement);
            int id = fetchGeneratedId(statement);
            connection.commit();
            return new Coupon(id, name, percentage, user_id);
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    public void add(int couponId, int... shopIds) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO coupons_shops (coupon_id, shop_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            statement.setInt(1, couponId);
            for (int shopId : shopIds) {
                statement.setInt(2, shopId);
                executeInsert(statement);
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    private Coupon fetchCoupon(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int percentage = resultSet.getInt("percentage");
        int user_id = resultSet.getInt("user_id");
        return new Coupon(id, name, percentage, user_id);
    }
}
