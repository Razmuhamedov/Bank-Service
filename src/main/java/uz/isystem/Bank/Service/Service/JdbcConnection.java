package uz.isystem.Bank.Service.Service;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class JdbcConnection {

   private String URL = "jdbc:postgresql://localhost:5432/postgres";

   private String username = "postgres";

   private String password = "root";

   Connection connection;

   public JdbcConnection() {
       try {
           connection = DriverManager.getConnection(URL,username,password);
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
   }

   public Connection getConnection(){
       return connection;
   }

}
