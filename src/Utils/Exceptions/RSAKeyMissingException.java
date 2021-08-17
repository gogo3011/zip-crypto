package Utils.Exceptions;

public class RSAKeyMissingException extends RuntimeException {

	public RSAKeyMissingException() {
		super("RSE Key cannot be found");
	}

}
