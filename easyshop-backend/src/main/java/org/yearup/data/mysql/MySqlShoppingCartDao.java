package org.yearup.data.mysql;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {

        ShoppingCart cart = new ShoppingCart();
        String sql = "SELECT * FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");

                Product product = getProductById(productId);

                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(quantity);

                cart.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cart;
    }

    // Example method to get a Product by its ID
    private Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        Product product = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    product = mapRow(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return product;
       }


@Override
public void addProduct(int userId, int productId, int quantity) throws SQLException {
    try (Connection connection = getConnection()) {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)");
        statement.setInt(1, userId);
        statement.setInt(2, productId);
        statement.setInt(3, quantity);
        statement.executeUpdate();
    }
}

@Override
@ResponseStatus(HttpStatus.NO_CONTENT)
public void removeProduct(int userId, int productId) {
    String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";
    try (Connection connection = getConnection()){
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userId);
        statement.setInt(2, productId);
        statement.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

@Override
public void clearCart(int userId) {
    String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
    try (Connection connection = getConnection()){
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userId);
        statement.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

@Override
public void updateQuantity(int userId, int productId, int quantity) {
    // TODO Implementation here
}

@Override
public void checkout(int userId) {
    // TODO Implementation here
}

    private Product mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }

}