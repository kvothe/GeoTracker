package at.jku.se.tracking.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Provides encryption mechanisms for passwords. Always use this service when working with passwords and NEVER store
 * passwords in plain text. </br></br> Adding a new user:</br> 1) Call <b>generateSalt()</b></br> 2) Call
 * <b>getEncryptedPassword()</b></br> 3) Store salt and encrypted password in database (salt doesn't have to be
 * secret)</br> </br></br> Authentication:</br> 1) Check if user exists</br> 2) Retrieve encrypted password and salt
 * from database</br> 3) Use <b>authenticate(attemptedPassword, encryptedPassword, salt)</b></br> </br></br> Change
 * password:</br> 1) Retrieve salt from database</br> 2) Call <b>getEncryptedPassword()</b> with new password</br> 3)
 * Store new encrypted password in database </br>
 * 
 * @author markus.hofmarcher
 */
public class PasswordEncryptionService {

	public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Encrypt the clear-text password using the same salt that was used to
		// encrypt the original password
		byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

		// Authentication succeeds if encrypted password that the user entered
		// is equal to the stored hash
		return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
	}

	public static byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Use PBKDF2 with SHA-1 as the hashing algorithm.
		String algorithm = "PBKDF2WithHmacSHA1";
		// SHA-1 generates 160 bit hashes
		int derivedKeyLength = 160;
		// Higher iteration count means more processing time for brute forcing
		// (choose at least 1000)
		int iterations = 20000;
		// --
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		// --
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		// --
		return f.generateSecret(spec).getEncoded();
	}

	public static byte[] generateSalt() throws NoSuchAlgorithmException {
		// VERY important to use SecureRandom instead of just Random
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		// --
		return salt;
	}
}
