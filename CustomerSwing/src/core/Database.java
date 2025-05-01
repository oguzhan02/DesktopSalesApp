package core;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database instance=null;
    private Connection connection = null;
    private final String DB_URL="*************************";
    private final String DB_USER="*******";
    private final String DB_PASSWORD="*******";

    private Database(){
        try {
            this.connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Connection getConnection(){
        return connection;
    }

    public static Connection getInstance() {
        try {
            if(instance==null || instance.connection.isClosed()){
                instance=new Database();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return instance.getConnection();
    }
}
