package Utils;

public enum AESKeyLength {
	SHORT(128), MEDIUM(192), LONG(256);

	private int length;

	private AESKeyLength(int n) {
		length = n;
	}

	public int getLength() {
		return length;
	}
}
