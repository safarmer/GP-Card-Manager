/**
 * 
 */
package au.com.nullpointer.gp.der;


/**
 * @author shane
 * 
 */
public class DecodingException extends Exception {
    private static final long serialVersionUID = -5778825159123926546L;

    public DecodingException() {
    }
    
    public DecodingException(int expected, int actual) {
        super(String.format("Unexpected tag found. Expected %d, got %d", expected, actual));
    }

    public DecodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecodingException(String message) {
        super(message);
    }

    public DecodingException(Throwable cause) {
        super(cause);
    }

}
