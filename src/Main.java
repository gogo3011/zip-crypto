import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;

import Utils.AESKeyLength;
import Utils.CryptoUtils;
import Utils.FileUtils;
import Utils.RSAKeyLength;
import Utils.Validators;
import Utils.Zipper;
import Utils.Exceptions.AESKeyMissingException;
import Utils.Exceptions.RSAKeyMissingException;

public class Main {

	public static void main(String[] args) throws Exception {
		if (!Validators.validateRunArguments(args))
			throw new InvalidParameterException("Invalid number of parameters");
		Path in = Paths.get(args[1]).toAbsolutePath();
		Path out = Paths.get(args[2]).toAbsolutePath();
		Path keyPath = Paths.get(args[3]).toAbsolutePath();
		if (!Validators.validateFilesExsist(in))
			throw new FileNotFoundException("Source zip file not found");
		Path tmpOut = Files.createTempDirectory("encTemp");
		switch (args[0]) {
		case "encrypt": {
			List<File> filesUnzipped = Zipper.unzip(in.toFile(), tmpOut.toFile());
			SecretKey key = CryptoUtils.generateKey(AESKeyLength.LONG);
			filesUnzipped.forEach(f -> {
				try {
					CryptoUtils.encryptFileAES(f, key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			Path AesKey = tmpOut.resolve("AES_KEY.txt");
			Files.writeString(AesKey, CryptoUtils.keyToString(key));
			KeyPair keyPair = CryptoUtils.generateKeyPair(RSAKeyLength.LONG);
			CryptoUtils.encryptFileRSA(AesKey.toFile(), keyPair.getPublic());
			Zipper.zip(Arrays.asList(tmpOut.toFile().listFiles()), out.toFile());
			Files.writeString(keyPath, CryptoUtils.keyPairToString(keyPair));
		}
			break;
		case "decrypt": {
			if (!Validators.validateFilesExsist(keyPath)) {
				throw new RSAKeyMissingException();
			}
			List<File> filesUnzipped = Zipper.unzip(in.toFile(), tmpOut.toFile());
			String RSAKeyString = Files.readString(keyPath).split("\n")[0];
			PrivateKey privateKey = CryptoUtils.stringToPrivateKey(RSAKeyString, "RSA");
			Path AESKey = tmpOut.resolve("AES_KEY.txt").toAbsolutePath();
			if (!filesUnzipped.remove(AESKey.toFile())) {
				throw new AESKeyMissingException();
			}
			CryptoUtils.decryptFileRSA(AESKey.toFile(), privateKey);
			SecretKey secretKey = CryptoUtils.stringToSecretKey(Files.readString(AESKey), "AES");
			filesUnzipped.forEach(f -> {
				try {
					CryptoUtils.decryptFileAES(f, secretKey);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			Files.delete(AESKey);
			Zipper.zip(Arrays.asList(tmpOut.toFile().listFiles()), out.toFile());
		}
			break;
		default:
			throw new InvalidParameterException("Allowed first parameter values: encrypt / decrypt");
		}
		FileUtils.deleteDir(tmpOut);
	}
}
