package mjh.tm.service.exception;

public class ProjectAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 1L;

    public ProjectAlreadyExistsException(String message) {
        super(message);
    }

}
