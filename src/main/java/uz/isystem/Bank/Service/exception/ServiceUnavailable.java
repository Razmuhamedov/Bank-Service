package uz.isystem.Bank.Service.exception;

public class ServiceUnavailable extends RuntimeException {
    public ServiceUnavailable(String message){
        super(message);
    }
}
