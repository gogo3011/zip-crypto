package Utils.Exceptions;

public class AESKeyMissingException extends RuntimeException {

	public AESKeyMissingException() {
		super("AES Key cannot be found");
	}

}
