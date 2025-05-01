package dao;

import core.Database;
import entity.Basket;
import entity.Customer;
import entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BasketDao {
    private Connection connection;
    private ProductDao productDao;
    public BasketDao() {
        this.connection = Database.getInstance();
        this.productDao = new ProductDao();
    }
    public boolean save(Basket basket){
        String query="INSERT INTO basket(product_id) VALUES(?)";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setInt(1,basket.getProductID());
            return preparedStatement.executeUpdate()>0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Basket match(ResultSet resultSet) throws SQLException {
        Basket basket = new Basket();
        basket.setId(resultSet.getInt("id"));
        basket.setProductID(resultSet.getInt("product_id"));

        Product product = this.productDao.getById(basket.getProductID());
        if (product == null) {
            System.err.println("UYARI: Basket id=" + basket.getId() + " için geçersiz product_id=" + basket.getProductID());
            return null; // Bu basket'i listeye ekleme
        }

        basket.setProduct(product);
        return basket;
    }

    public ArrayList<Basket> findAll() {
        ArrayList<Basket> baskets = new ArrayList<>();
        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery("SELECT * FROM basket");
            while (resultSet.next()) {
                Basket basket = this.match(resultSet);
                if (basket != null) {
                    baskets.add(basket);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return baskets;
    }
    public boolean clear(){
        String query ="DELETE FROM basket";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            return preparedStatement.executeUpdate()>0;

        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}
