package uz.isystem.Bank.Service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.isystem.Bank.Service.Model.ATM;
import uz.isystem.Bank.Service.Service.AtmService;
import java.util.List;

@RestController
@RequestMapping("/atm")
public class AtmController {
    @Autowired
    private AtmService atmService;

    @PostMapping()
    public ResponseEntity<?> createAtm(@RequestBody ATM atm){
        String result = atmService.createAtm(atm);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAtm(@PathVariable("id") Integer id, @RequestBody ATM atm){
        String result = atmService.updateAtm(id, atm);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAtm(@PathVariable("id") Integer id){
        ATM result = atmService.getAtm(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    public ResponseEntity<?> getAll(){
        List<ATM> result = atmService.getAll();
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAtm(@PathVariable("id") Integer id){
        String result = atmService.deleteAtm(id);
        return ResponseEntity.ok(result);
    }
}
