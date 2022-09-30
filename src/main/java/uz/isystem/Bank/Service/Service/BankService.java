package uz.isystem.Bank.Service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.isystem.Bank.Service.Model.ATM;
import uz.isystem.Bank.Service.Model.Bank;
import uz.isystem.Bank.Service.Model.Card;
import uz.isystem.Bank.Service.exception.BadRequest;
import uz.isystem.Bank.Service.exception.ServiceUnavailable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Component
public class BankService {
    @Autowired
    JdbcConnection jdbcConnection;
    @Autowired
    CardService cardService;
    @Autowired
    AtmService atmService;

    public Bank findBank(Integer id){
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select * from bank where bid =?");
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            Bank bank = new Bank();
            if(resultSet.next()){
                bank.setId(resultSet.getInt("bid"));
                bank.setName(resultSet.getString("name"));
                bank.setAmount(resultSet.getDouble("amount"));
                //todo
                bank.setCardList(bank.getCardList());
                bank.setAtmList(bank.getAtmList());
                return bank;
            }
            throw new BadRequest("Bank not found!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String createBank(Bank bank){
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("insert into bank(name, amount) values (?,?)");
            ps.setString(1, bank.getName());
            ps.setDouble(2,1000000);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Bank not created!");
            }
            return "Bank created!";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Bank getBank(Integer id){
       return findBank(id);
    }

    public String updateBank(Integer id, Bank bank){
        findBank(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("update bank set name=? where bid=?");
            ps.setString(1, bank.getName());
            ps.setInt(2,id);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Bank not updated!");
            }
            return "Bank name updated";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bank> getAll(){
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select * from bank inner join card on bank.bid=card.bid");
            ResultSet resultSet = ps.executeQuery();
            List<Bank> bankList = new LinkedList<>();
            while (resultSet.next()){
                Bank bank = new Bank();
                bank.setId(resultSet.getInt("bid"));
                bank.setName(resultSet.getString("name"));
                bank.setAmount(resultSet.getDouble("amount"));
                bankList.add(bank);
            }
            if(bankList.isEmpty()){
                throw new BadRequest("Banks not found!");
            }
            return bankList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteBank(Integer id){
        findBank(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("delete from bank where bid = ?");
            ps.setInt(1, id);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Bank not deleted!");
            }
            return "Bank deleted!";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String addCard(Integer cardId, Integer bankId){
        Card card = cardService.findCard(cardId);
        findBank(bankId);
        if(card.getStatus()) throw new BadRequest("Card already activated!");
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("with t as(update card set bid = ?, amount = ?, status=? where cid = ?)" +
                            "update bank set amount = amount - ? where bid = ?");
            ps.setInt(1, bankId);
            ps.setDouble(2,5000);
            ps.setBoolean(3, true);
            ps.setInt(4, cardId);
            ps.setDouble(5,5000);
            ps.setInt(6,bankId);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Card not added to bank!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Card added to bank!";
    }

    public String addAtm(Integer atmId, Integer bankId){
        atmService.getAtm(atmId);
        findBank(bankId);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("update atm set bid=? where aid=?");
            ps.setInt(1,bankId);
            ps.setInt(2,atmId);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Atm not added to bank");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "ATM added to bank";
    }

    public List<Card> getCards(Integer id){
        Bank bank = findBank(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select * from card where bid = ?");
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            Card card = new Card();
            while (resultSet.next()) {
                cardService.convertCard(resultSet, card);
                bank.getCardList().add(card);
            }
            if(bank.getCardList().isEmpty()){
                throw new BadRequest("Cards don't exist in this bank!");
            }
            return bank.getCardList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ATM> getAtms(Integer id){
        Bank bank = findBank(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select * from atm where bid =?");
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            ATM atm = new ATM();
            while (resultSet.next()){
                atmService.convertAtm(resultSet, atm);
                bank.getAtmList().add(atm);
            }
            if (bank.getAtmList().isEmpty()){
                throw new BadRequest("Atms don't exist in this bank!");
            }
            return bank.getAtmList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String paymentBankToCard(Integer bankId, Integer cardId, Double cash){
        Bank bank = findBank(bankId);
        if(bank.getAmount()<cash) throw new ServiceUnavailable("Not enough money!");
        Card card = cardService.findCard(cardId);
        if(!card.getStatus()) throw new BadRequest("Card is not active!");
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("with t as(update bank set amount = amount - ? where bid = ?)" +
                            "update card set amount = amount + ? where cid = ?");
            ps.setDouble(1, cash);
            ps.setInt(2,bankId);
            ps.setDouble(3,cash);
            ps.setInt(4,cardId);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Payment failed!");
            }
            return "Payment successfully";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String paymentAtmToCard(Integer atmId, Integer cardId, Double cash){
        ATM atm = atmService.findAtm(atmId);
        if(!atm.getStatus()) throw new BadRequest("ATM offline!");
        Card card = cardService.findCard(cardId);
        if(!card.getStatus()) throw new BadRequest("Card not active!");
        if(atm.getAmount()<cash) throw new ServiceUnavailable("Not enough money!");
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("with t as(update atm set amount = amount - ? where aid = ?)" +
                            "update card set amount = amount + ? where cid = ?");
            ps.setDouble(1, cash);
            ps.setInt(2, atmId);
            ps.setDouble(3, cash);
            ps.setInt(4, cardId);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Payment failed!");
            }
           // if(atm.getAmount()<10000) finishAtm(atmId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Payment successfully!";
    }

    public String startAtm(Integer atmId){
        try {
            Bank bank = new Bank();
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("select bid from atm where aid = ?");
            ps.setInt(1,atmId);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
            bank = findBank(resultSet.getInt("bid"));}
            if(bank.getAmount()<50000.0) {
                throw new ServiceUnavailable("Bank is bankrupt!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ATM atm = atmService.findAtm(atmId);
        if(atm.getStatus()){
            throw new BadRequest("ATM already activated!");
        }
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("with s as(update atm set status = true, amount = 50000 where aid = ?)" +
                            "update bank set amount = amount - 50000 where bid = (select bid from atm where aid = ?)");
            ps.setInt(1,atmId);
            ps.setInt(2,atmId);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Atm activate failed!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "ATM is activated!";
    }

    public String  finishAtm(Integer atmId){
        ATM atm = atmService.findAtm(atmId);
        if(!atm.getStatus()){
            throw new BadRequest("ATM already deactivated!");
        }
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("update bank set amount = amount + ? where bid = (select bid from atm where aid =?)");
            ps.setDouble(1, atm.getAmount());
            ps.setInt(2,atmId);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Atm finish failed!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("update atm set status = false, amount = 0 where aid = ?");
            ps.setInt(1,atmId);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Atm finish failed!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "ATM is deactivated!";
    }
}
