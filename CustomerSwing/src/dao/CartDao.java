package dao;

import com.mysql.cj.protocol.Resultset;
import core.Database;
import entity.Cart;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CartDao {
    private Connection connection;
    private ProductDao productDao;
    private CustomerDao customerDao;
    public CartDao() {
        this.connection = Database.getInstance();
        this.productDao = new ProductDao();
        this.customerDao = new CustomerDao();
    }
    public ArrayList<Cart> findAll(){
        ArrayList<Cart> carts=new ArrayList<>();
        try {
            ResultSet rs =this.connection.createStatement().executeQuery("SELECT * FROM cart");
            while(rs.next()){
                carts.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return carts;
    }
    public boolean save(Cart cart){
        String query="INSERT INTO cart(price,note,date,customer_id,product_id) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setInt(1,cart.getPrice());
            preparedStatement.setString(2,cart.getNote());
            preparedStatement.setDate(3, Date.valueOf(cart.getDate()));
            preparedStatement.setInt(4,cart.getCustomerID());
            preparedStatement.setInt(5,cart.getProductID());
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Cart match(ResultSet rs) throws SQLException{
        Cart cart=new Cart();
        cart.setId(rs.getInt("cart_id"));
        cart.setProductID(rs.getInt("product_id"));
        cart.setCustomerID(rs.getInt("customer_id"));
        cart.setPrice(rs.getInt("price"));
        cart.setNote(rs.getString("note"));
        cart.setDate(LocalDate.parse(rs.getString("date")));
        cart.setCustomer(this.customerDao.getById(cart.getCustomerID()));
        cart.setProduct(this.productDao.getById(cart.getProductID()));
        return cart;
    }

}
