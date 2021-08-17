package Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Validators {
	private Validators() {
	}

	public static boolean fileIsDirectory(File file) {
		return file.isDirectory();
	}

	public static boolean validateValidFilePath(File file) {
		return !file.isDirectory() && (file.getParentFile().mkdirs() || file.getParentFile().exists());
	}

	public static boolean validateRunArguments(String[] args) {
		return args.length == 4;
	}

	public static boolean validateFilesExsist(Path paths) {
		return Files.exists(paths);
	}
}
