/**
 * Custom exception thrown when a withdrawal or transfer is attempted
 * for more money than the account actually holds.
 *
 * Writing a custom exception like this is a common thing examiners look
 * for in a 1st year OOP project, since it shows exception handling +
 * inheritance (this class extends the built-in Exception class).
 */
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
