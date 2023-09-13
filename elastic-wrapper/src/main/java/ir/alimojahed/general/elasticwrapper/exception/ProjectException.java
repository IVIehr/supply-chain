package ir.alimojahed.general.elasticwrapper.exception;

/**
 * @author Ali Mojahed on 6/3/2021
 * @project spring-template
 **/
public class ProjectException extends Exception {
    protected ExceptionStatus status;
    protected String message;

    public ProjectException(ExceptionStatus status) {
        this.status = status;
        this.message = status.getMessage();
    }

    public ProjectException(ExceptionStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ExceptionStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
