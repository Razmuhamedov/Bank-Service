package uz.isystem.Bank.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.isystem.Bank.Service.Model.Card;
import uz.isystem.Bank.Service.Model.PaymentCardToCard;
import uz.isystem.Bank.Service.exception.BadRequest;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
@Component
public class CardService {
    @Autowired
    JdbcConnection jdbcConnection;

    public Card findCard(Integer id){
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                            prepareStatement("SELECT * FROM card where cid = ?");
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            Card card = new Card();
            if(resultSet.next()){
                convertCard(resultSet, card);
                return card;
            }
            throw new BadRequest("Card not found!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void convertCard(ResultSet resultSet, Card card) throws SQLException {
        card.setId(resultSet.getInt("cid"));
        card.setNumber(resultSet.getString("number"));
        card.setName(resultSet.getString("name"));
        card.setPinCode(resultSet.getString("pinCode"));
        card.setDate(LocalDate.now().plusYears(5));
        card.setAmount(resultSet.getDouble("amount"));
        card.setStatus(resultSet.getBoolean("status"));
        card.setBid(resultSet.getInt("bid"));
    }

    public void checkCard(Card card){

        if(card.getNumber().length()!=16) {
            throw new BadRequest("Card number error!");
        }
        if(card.getPinCode().length() !=4) {
            throw new BadRequest("Card pin error!");
        }
        if(card.getName().length()>25) {
            card.setName(card.getName().substring(0,25));}

        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("SELECT * FROM card WHERE number = ?");
            ps.setString(1, card.getNumber());
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                throw new BadRequest("Card is present!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Card getByNumber(String number){
        try {
            PreparedStatement ps = jdbcConnection.getConnection().prepareStatement
                    ("SELECT * FROM card where number=?");
            ps.setString(1,number);
            ResultSet resultSet = ps.executeQuery();
            Card card = new Card();
            while (resultSet.next()){
                convertCard(resultSet,card);
            }
            if(card.getId()==null){
                throw new BadRequest("Card not found!");
            }
            return card;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String createCard(Card card) {
        checkCard(card);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                        prepareStatement("INSERT INTO card(number,name,pinCode, date, amount, status)" +
                                " values (?,?,?,?,?,?)");
        ps.setString(1,card.getName());
        ps.setString(2,card.getNumber());
        ps.setString(3,card.getPinCode());
        ps.setDate(4, Date.valueOf(LocalDate.now().plusYears(5)));
        ps.setDouble(5,0.0);
        ps.setBoolean(6,false);
        int i = ps.executeUpdate();
        if(i==0){
            throw new BadRequest("Card not created!");
        }
        return "Card added";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String updateCard(Integer id, Card card) {
        checkCard(card);
        findCard(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("UPDATE card SET name =?,pinCode=?,number=?, bid=? WHERE cid =?");
            ps.setString(1,card.getName());
            ps.setString(2,card.getPinCode());
            ps.setString(3,card.getNumber());
            ps.setInt(4, card.getBid());
            ps.setInt(5,id);
            int i = ps.executeUpdate();
            if(i==0) {
                throw new BadRequest("Card not updated!");
            }
            return "Card updated!";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Card getCard(Integer id) {
        return findCard(id);
    }

    public String deleteCard(Integer id) {
        findCard(id);
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("delete from card where cid = ?");
            ps.setInt(1, id);
            int i = ps.executeUpdate();
            if(i==0){
                throw new BadRequest("Card not deleted!");
            }
            return "Card deleted!";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Card> getAll() {
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("SELECT * FROM card order by cid");
            List<Card> cardList = new LinkedList<>();
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                Card card = new Card();
                convertCard(resultSet,card);
                cardList.add(card);
            }
            if(cardList.isEmpty()){
                throw new BadRequest("Cards not found!");
            }
            return cardList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //todo
    public String payment(PaymentCardToCard payment){
        Card fromCard = findCard(payment.getFromId());
        if(!fromCard.getStatus()){
            throw new BadRequest("Card status inactive");
        }

        if(!fromCard.getPinCode().equals(payment.getFromCode())) {
            throw new BadRequest("Card pin error!");
        }
        Card toCard = findCard(payment.getToId());
        if(!toCard.getStatus()){
            throw new BadRequest("Card status inactive");
        }

        if(fromCard.getAmount()< payment.getCash()){
            throw new BadRequest("Not enough money!");
        }
        try {
            PreparedStatement ps = jdbcConnection.getConnection().
                    prepareStatement("with t as(update card set amount = amount - ? where cid = ?)" +
                            "update card set amount = amount + ? where cid = ?");
            ps.setDouble(1,payment.getCash());
            ps.setInt(2,payment.getToId());
            ps.setDouble(3,payment.getCash());
            ps.setInt(4,payment.getFromId());
            int i = ps.executeUpdate();
            if (i == 0) {
                throw new BadRequest("Payment failed!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Payment successfully!";
    }

}
