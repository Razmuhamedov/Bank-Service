package uz.isystem.Bank.Service.exception;

public class BadRequest extends RuntimeException{
    public BadRequest(String message){
        super(message);
    }

}
