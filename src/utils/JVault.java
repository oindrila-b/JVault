package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class JVault {
	
	private static final String ALGO_PBE_WITH_MD5_AND_TRIPLE_DES = "PBEWithMD5AndTripleDES";
	
	public static void encrypt(File file, String password) throws 
			NoSuchAlgorithmException, InvalidKeySpecException, 
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
			IOException, IllegalBlockSizeException, BadPaddingException {
		
		
		FileInputStream input = new FileInputStream(file);
		File tempFile = new File("temp");
		tempFile.createNewFile();
		FileOutputStream output = new FileOutputStream(tempFile);
		
		
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGO_PBE_WITH_MD5_AND_TRIPLE_DES);
		SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
		
		byte[] salt = new byte[8];
		Random random = new Random();
		random.nextBytes(salt);
		
		PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance(ALGO_PBE_WITH_MD5_AND_TRIPLE_DES);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
		output.write(salt);
		
		
		byte[] inputBytes = new byte[64];
		int bytesRead;
		
		while ((bytesRead = input.read(inputBytes)) != -1) {
			byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
			if (outputBytes != null) {
				output.write(outputBytes);
			}
		}
		
		byte[] outputBytes = cipher.doFinal();
		if (outputBytes != null) {
			output.write(outputBytes);
		}
		
		input.close();
		output.flush();
		output.close();		
		
		FileInputStream inFile = new FileInputStream(tempFile);
		FileOutputStream outFile = new FileOutputStream(file);
		
		byte[] buffer = new byte[1024];
		int length;
		
		while ((length = inFile.read(buffer)) > 0) {
			outFile.write(buffer, 0, length);
		}
		
		inFile.close();
		outFile.flush();
		outFile.close();
		tempFile.delete();
		
	}
	
	
	public static void decrypt(File file, String password) throws 
			NoSuchAlgorithmException, InvalidKeySpecException, IOException, 
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
			IllegalBlockSizeException, BadPaddingException {
		
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGO_PBE_WITH_MD5_AND_TRIPLE_DES);
		SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
		
		FileInputStream inFile = new FileInputStream(file);
		byte[] salt = new byte[8];
		inFile.read(salt);
		
		PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
		File tempFile = new File("temp");
		tempFile.createNewFile();
		Cipher cipher = Cipher.getInstance(ALGO_PBE_WITH_MD5_AND_TRIPLE_DES);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);
		FileOutputStream outFile = new FileOutputStream(tempFile);
		
		byte[] in = new byte[64];
		int read;
		
		while ((read = inFile.read(in)) != -1) {
			byte[] output = cipher.update(in, 0, read);
			if (output != null) {
				outFile.write(output);
			}
		}
		
		byte[] output = cipher.doFinal();
		if (output != null) {
			outFile.write(output);
		}
		
		inFile.close();
		outFile.flush();
		outFile.close();
		
		
		FileInputStream inputFile = new FileInputStream(tempFile);
		FileOutputStream outputFile = new FileOutputStream(file);
		
		byte[] buffer = new byte[1024];
		int length;
		
		while ((length = inputFile.read(buffer)) > 0) {
			outputFile.write(buffer, 0, length);
		}
		
		inputFile.close();
		outputFile.flush();
		outputFile.close();
		tempFile.delete();
	}
	
	
	
	
	
	

}
