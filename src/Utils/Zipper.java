package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper {
	private Zipper() {
	}

	public static List<File> unzip(File zipPath, File destDir) throws IOException {
		List<File> filesUnzipped = new ArrayList<File>();
		try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(zipPath));) {
			byte[] buffer = new byte[1024];
			ZipEntry entry = zipInput.getNextEntry();
			while (entry != null) {
				File newFile = new File(destDir, entry.getName());
				if (!destDir.isDirectory() && !destDir.mkdirs())
					throw new IOException("Cannot create destination directory: " + destDir);
				try (FileOutputStream fos = new FileOutputStream(newFile)) {
					int len;
					while ((len = zipInput.read(buffer, 0, 1024)) != -1) {
						fos.write(buffer, 0, len);
					}
				}
				filesUnzipped.add(newFile);
				entry = zipInput.getNextEntry();
			}
		}
		return filesUnzipped;
	}

	public static void zip(List<File> filesToZip, File dest) throws IOException {
		try (ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(dest))) {
			filesToZip.forEach(f -> {
				try (FileInputStream fis = new FileInputStream(f);) {
					ZipEntry entry = new ZipEntry(f.getName());
					zipOutput.putNextEntry(entry);
					byte[] buffer = new byte[1024];
					int len;
					while ((len = fis.read(buffer)) != -1) {
						zipOutput.write(buffer, 0, len);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
}
