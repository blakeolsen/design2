package design2.prototype1.clientside.Exceptions;

/**
 * UnableToFindComPortException
 * def: thrown if the program is unable to find the port that the
 *      arduino is writing to
 */
public class UnableToFindComPortException extends Exception {
    public UnableToFindComPortException() {
        super();
    }

    public UnableToFindComPortException(String msg) {
        super(msg);
    }
}
