/**
 * A class to represent errors in user-specified commands.
 */
public class JarvisException extends Exception {
    /**
     * Creates a JarvisException with the given message.
     */
    public JarvisException(String message) {
        super(message);
    }
}