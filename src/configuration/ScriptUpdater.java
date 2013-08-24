package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * 
 * 
 * @author Dominik
 * 
 */
public class ScriptUpdater {
	private final static String DEFAULT_PLACEHOLDER = "BUCKET_NAME";
	private String templateFolder;
	private String bucketName;
	private String targetFolder;

	public ScriptUpdater(String templateFolder, String targetFolder, String bucketName) {
		this.templateFolder = templateFolder;
		this.bucketName = bucketName;
		this.targetFolder=targetFolder;
	}

	public void cloneConfiguration(String path) {
		try {
			File targetDir = new File(targetFolder);
			if (targetDir.exists()) {
				FileUtils.cleanDirectory(targetDir);
			}
			FileUtils.copyDirectory(new File(path), targetDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void replaceInFile(File file, String replaced, String replacement) {
		String wholeFile;
		try {
			FileInputStream input = new FileInputStream(file);
			wholeFile = IOUtils.toString(input, "UTF-8");
			wholeFile = wholeFile.replaceAll(replaced, replacement);
			input.close();
			FileOutputStream output = new FileOutputStream(file);
			IOUtils.write(wholeFile, output, "UTF-8");
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateScripts() {
		cloneConfiguration(templateFolder);
		Iterator<File> files = FileUtils.iterateFiles(new File(targetFolder),
				null, true);
		while (files.hasNext()) {
			replaceInFile(files.next(), DEFAULT_PLACEHOLDER, bucketName);
		}
	}
	
	public void uploadScripts() {
		S3Uploader uploader = new S3Uploader();
		uploader.uploadFolder(targetFolder, bucketName, targetFolder);
	}
}
