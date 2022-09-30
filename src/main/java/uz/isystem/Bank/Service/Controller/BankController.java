package uz.isystem.Bank.Service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.isystem.Bank.Service.Model.ATM;
import uz.isystem.Bank.Service.Model.Bank;
import uz.isystem.Bank.Service.Model.Card;
import uz.isystem.Bank.Service.Service.BankService;
import java.util.List;

@RestController
@RequestMapping("/bank")
public class BankController {
    @Autowired
    BankService bankService;

    @PostMapping
    public ResponseEntity<?> createBank(@RequestBody Bank bank){
        String result = bankService.createBank(bank);
       return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getBank(@PathVariable("id") Integer id){
        Bank result = bankService.getBank(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBank(@PathVariable("id") Integer id,
                                        @RequestBody Bank bank){
        String result = bankService.updateBank(id, bank);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        List<Bank> result = bankService.getAll();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBank(@PathVariable("id") Integer id){
        String result = bankService.deleteBank(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/addCard")
    public ResponseEntity<?> addCard(@RequestParam("card") Integer cardId,
                                     @RequestParam("bank") Integer bankId){
        String result = bankService.addCard(cardId,bankId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/addAtm")
    public ResponseEntity<?> addAtm(@RequestParam("atm") Integer atmId,
                                     @RequestParam("bank") Integer bankId){
        String result = bankService.addAtm(atmId,bankId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllCards")
    public ResponseEntity<?> getAllCard(@RequestParam("bank") Integer id){
        List<Card> result = bankService.getCards(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllAtms")
    public ResponseEntity<?> getAllAtm(@RequestParam("bank") Integer id){
        List<ATM> result = bankService.getAtms(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/paymentToCard")
    public ResponseEntity<?> paymentToCard(@RequestParam("bank") Integer bankId,
                                           @RequestParam("card") Integer cardId,
                                           @RequestParam("cash") Double cash){
        String result = bankService.paymentBankToCard(bankId,cardId,cash);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/startAtm/{id}")
    ResponseEntity<?> startAtm(@PathVariable("id") Integer id){
        String result = bankService.startAtm(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/finishAtm/{id}")
    ResponseEntity<?> finishAtm(@PathVariable("id") Integer id){
        String result = bankService.finishAtm(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/atm/paymentToCard")
    public ResponseEntity<?> paymentAtmToCard(@RequestParam("atm") Integer atmId,
                                           @RequestParam("card") Integer cardId,
                                           @RequestParam("cash") Double cash){
        String result = bankService.paymentAtmToCard(atmId,cardId,cash);
        return ResponseEntity.ok(result);
    }


}
