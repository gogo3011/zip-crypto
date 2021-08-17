package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
	private CryptoUtils() {
	}

	public static void encryptFileAES(File fileToEncrypt, SecretKey key)
			throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		cryptFile(fileToEncrypt, cipher);
	}

	public static void encryptFileRSA(File fileToEncrypt, PublicKey key) throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		cryptFile(fileToEncrypt, cipher);
	}

	public static void decryptFileRSA(File fileToDecrypt, PrivateKey key) throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		cryptFile(fileToDecrypt, cipher);
	}

	public static void decryptFileAES(File fileToDecrypt, SecretKey key)
			throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		cryptFile(fileToDecrypt, cipher);
	}

	private static void cryptFile(File fileToEncrypt, Cipher cipher)
			throws IOException, IllegalBlockSizeException, BadPaddingException {
		File tmpFile = Files.createTempFile(fileToEncrypt.getName(), ".en").toFile();
		try (FileInputStream is = new FileInputStream(fileToEncrypt);
				FileOutputStream os = new FileOutputStream(tmpFile);) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				byte[] encryptedBytes = cipher.update(buffer, 0, len);
				if (encryptedBytes != null)
					os.write(encryptedBytes);
			}
			byte[] finalBytes = cipher.doFinal();
			if (finalBytes != null)
				os.write(finalBytes);
		}
		Files.move(tmpFile.toPath(), fileToEncrypt.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	public static IvParameterSpec generateIv() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	public static SecretKey generateKey(AESKeyLength aesKeyLength) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(aesKeyLength.getLength());
		return keyGenerator.generateKey();
	}

	public static KeyPair generateKeyPair(RSAKeyLength rsaKeyLength) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(rsaKeyLength.getLength());
		return keyPairGenerator.generateKeyPair();
	}

	public static String keyToString(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public static String keyPairToString(KeyPair keyPair) {
		return keyToString(keyPair.getPrivate()) + "\n" + keyToString(keyPair.getPublic());
	}

	public static SecretKey stringToSecretKey(String s, String algType) {
		byte[] bytes = Base64.getDecoder().decode(s);
		return new SecretKeySpec(bytes, 0, bytes.length, algType);
	}

	public static PrivateKey stringToPrivateKey(String s, String algType)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] bytes = Base64.getDecoder().decode(s);
		return KeyFactory.getInstance(algType).generatePrivate(new PKCS8EncodedKeySpec(bytes));
	}
}
