package uz.isystem.Bank.Service.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATM {
    private Integer id;
    private String number;
    private String pinCode;
    private String address;
    private Double amount;
    private Boolean status;
    private Integer bid;

}
