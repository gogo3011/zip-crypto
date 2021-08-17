package Utils;

public enum RSAKeyLength {
	SHORT(512), MEDIUM(1024), LONG(2048);

	private int length;

	private RSAKeyLength(int n) {
		length = n;
	}

	public int getLength() {
		return length;
	}
}
