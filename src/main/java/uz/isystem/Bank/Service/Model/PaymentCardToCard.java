package uz.isystem.Bank.Service.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCardToCard {
    private Integer fromId;
    private Integer toId;
    private Double cash;
    private String fromCode;

}
