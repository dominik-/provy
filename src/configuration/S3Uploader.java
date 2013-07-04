package configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Uploader {

	private final static Logger logger = Logger.getLogger(S3Uploader.class
			.getSimpleName());
	public final static String DEFAULT_AWS_AUTH = "aws/credentials.properties";
	private static AmazonS3Client s3;

	public S3Uploader() {
		this(null);
	}

	public S3Uploader(String credentialsFilePath) {
		boolean authenticated = authenticateWithProvider(credentialsFilePath);
		if (!authenticated)
			throw new RuntimeException("Authentication failed. Aborting.");
	}

	public boolean authenticateWithProvider(String authenticationFile) {
		AWSCredentials credentials;

		if (authenticationFile == null) {
			try {
				credentials = new EnvironmentVariableCredentialsProvider()
						.getCredentials();
				logger.log(Level.INFO, "Credentials loaded from environment.");
				s3 = new AmazonS3Client(credentials);
			} catch (AmazonClientException ase) {
				try {
					credentials = new PropertiesCredentials(new File(
							DEFAULT_AWS_AUTH));
					if (!checkCredentialsValid(credentials)) {
						return false;
					}
					logger.log(Level.INFO,
							"Credentials loaded from default file.");
					s3 = new AmazonS3Client(credentials);
				} catch (FileNotFoundException e) {
					logger.log(Level.SEVERE,
							"No credentials found in 'aws/credentials.properties'.");
					return false;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					logger.log(Level.SEVERE,
							"Could not access AWS credentials in file '"
									+ authenticationFile + "'.");
					return false;
				}
				return true;
			}
		} else {
			try {
				credentials = new PropertiesCredentials(new File(
						authenticationFile));
				if (!checkCredentialsValid(credentials)) {
					return false;
				}
				logger.log(Level.INFO,
						"Credentials loaded from specified file.");
				s3 = new AmazonS3Client(credentials);
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "No credentials found in file '"
						+ authenticationFile + "'.");
				return false;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"Could not access AWS credentials in file '"
								+ authenticationFile + "'.");
				return false;
			}

		}
		return true;
	}

	private boolean checkCredentialsValid(AWSCredentials credentials) {
		if (credentials.getAWSAccessKeyId().isEmpty()) {
			return false;
		} else if (credentials.getAWSSecretKey().isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean uploadFile(String fileName, String bucketName) {
		File file = new File(fileName);
		if (file.exists()) {
			PutObjectRequest req = new PutObjectRequest(bucketName, fileName,
					file);
			req.setCannedAcl(CannedAccessControlList.PublicRead);
			logger.log(Level.INFO, "Uploading File '" + fileName
					+ "' to bucket " + bucketName + "...");
			s3.putObject(req);
			logger.log(Level.INFO, "Upload finised.");
			return true;
		}
		return false;

	}
}
