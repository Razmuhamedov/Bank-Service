package uz.isystem.Bank.Service.Model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class Card {
    private Integer id;
    private String number;
    private String name;
    private String pinCode;
    private LocalDate date;
    private Double amount;
    private Boolean status;
    private Integer bid;
}
