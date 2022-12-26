package pala.libs.generic.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
	public static byte[] sha256(byte... bytes) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"SHA-256 implementation not supported on this Java system; hashing could not be performed.");
		}
	}

	public static byte[] sha256(String data) {
		return sha256(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha512(byte... bytes) {
		try {
			return MessageDigest.getInstance("SHA-512").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"SHA-256 implementation not supported on this Java system; hashing could not be performed.");
		}
	}

	public static byte[] sha512(String data) {
		return sha512(data.getBytes(StandardCharsets.UTF_8));
	}
}
