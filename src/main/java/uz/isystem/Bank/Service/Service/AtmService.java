package uz.isystem.Bank.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.isystem.Bank.Service.Model.ATM;
import uz.isystem.Bank.Service.exception.BadRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
@Component
public class AtmService {
    @Autowired
    JdbcConnection jdbcConnection;
    public ATM findAtm(Integer id){
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select * from atm where aid =?");
            ps.setInt(1,id);
            ResultSet resultSet = ps.executeQuery();
            ATM atm = new ATM();
            if (resultSet.next()){
                convertAtm(resultSet, atm);
                return atm;
            }
            throw new BadRequest("Atm not found!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void convertAtm(ResultSet resultSet, ATM atm) throws SQLException{
        atm.setId(resultSet.getInt("aid"));
        atm.setNumber(resultSet.getString("number"));
        atm.setPinCode(resultSet.getString("pinCode"));
        atm.setAddress(resultSet.getString("address"));
        atm.setAmount(resultSet.getDouble("amount"));
        atm.setStatus(resultSet.getBoolean("status"));
        atm.setBid(resultSet.getInt("bid"));
    }
    public void checkAtm(ATM atm){
        if(atm.getNumber().length()!=22) {
            throw new BadRequest("Atm number error!");
        }
        if(String.valueOf(atm.getPinCode()).length() !=6) {
            throw new BadRequest("Atm pin error!");
        }
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select * from atm where number = ?");
            ps.setString(1,atm.getNumber());
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                throw new BadRequest("Atm is present!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ATM getAtm(Integer id){
        return findAtm(id);
    }

    public String createAtm(ATM atm){
        try {
            PreparedStatement ps =  jdbcConnection.getConnection().
                    prepareStatement("INSERT INTO atm(number, pinCode, address, amount, status)" +
                            "values (?,?,?,?,?)");
            ps.setString(1,atm.getNumber());
            ps.setString(2,atm.getPinCode());
            ps.setString(3,atm.getAddress());
            ps.setDouble(4,0.0);
            ps.setBoolean(5,false);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Atm not created!");
            }
            return "Atm created!";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateAtm(Integer id, ATM atm){
        checkAtm(atm);
        findAtm(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("update atm set number=?, address=?, pinCode=?, bid=? where aid = ?");
            ps.setString(1,atm.getNumber());
            ps.setString(2,atm.getAddress());
            ps.setString(3,atm.getPinCode());
            ps.setInt(4,atm.getBid());
            ps.setInt(5,id);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Atm not updated!");
            }
            return "Atm updated!";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteAtm(Integer id) {
        findAtm(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("delete from atm where aid =?");
            ps.setInt(1, id);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Atm not deleted!");
            }
            return "Atm deleted!";
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<ATM> getAll() {
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select * from atm");
            ResultSet resultSet = ps.executeQuery();
            ATM atm = new ATM();
            List<ATM> atmList = new LinkedList<>();
            while (resultSet.next()){
                convertAtm(resultSet, atm);
                atmList.add(atm);
            }
            if(atmList.isEmpty()){
                throw new BadRequest("Atms not found!");
            }
            return atmList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
