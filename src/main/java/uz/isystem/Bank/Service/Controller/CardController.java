package uz.isystem.Bank.Service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.isystem.Bank.Service.Model.Card;
import uz.isystem.Bank.Service.Model.PaymentCardToCard;
import uz.isystem.Bank.Service.Service.CardService;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping()
    public ResponseEntity<?> createCard(@RequestBody Card card){
        String result = cardService.createCard(card);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@PathVariable("id") Integer id, @RequestBody Card card){
          String result = cardService.updateCard(id, card);
          return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCard(@PathVariable("id") Integer id){
        Card result = cardService.getCard(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    public ResponseEntity<?> getAll(){
         List<Card> result = cardService.getAll();
         return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable("id") Integer id){
       String result = cardService.deleteCard(id);
      return ResponseEntity.ok(result);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment(@RequestBody PaymentCardToCard payment){
        String result = cardService.payment(payment);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getByNumb/{number}")
    public ResponseEntity<?> getByNumber(@PathVariable("number") String number){
        Card result = cardService.getByNumber(number);
        return ResponseEntity.ok(result);
    }


}
