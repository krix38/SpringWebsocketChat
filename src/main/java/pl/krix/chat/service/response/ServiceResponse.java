package pl.krix.chat.service.response;

/**
 * Created by krix on 10.12.15.
 */
public class ServiceResponse {
    private Boolean isSuccessfull;
    private String message;

    public ServiceResponse(Boolean isSuccessfull, String message) {
        this.isSuccessfull = isSuccessfull;
        this.message = message;
    }

    public Boolean getSuccessfull() {
        return isSuccessfull;
    }

    public void setSuccessfull(Boolean successfull) {
        isSuccessfull = successfull;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
