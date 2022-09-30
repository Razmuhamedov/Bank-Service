package uz.isystem.Bank.Service.Model;

import lombok.Getter;
import lombok.Setter;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Bank {
    private Integer id;
    private String name;
    private List<ATM> atmList;
    private List<Card> cardList;
    private Double amount;

    public Bank(){
        this.atmList = new LinkedList<>();
        this.cardList = new LinkedList<>();
    }
}
