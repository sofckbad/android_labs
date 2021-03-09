package com.example.lab1;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

	public static char[] alphabet_RU_L = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toCharArray();
	public static char[] alphabet_RU_U = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();
	private static final String key = "ключ".toLowerCase();

	public static String decrypt(String res) {
		return decryptVigenere(res);
	}
	public static String encrypt(String res) {
		return encryptVigenere(res);
	}
	public static String decryptLib(String res) {
		try {
			Cipher decrypt = Cipher.getInstance("AES");
			decrypt.init(Cipher.DECRYPT_MODE, new SecretKeySpec("SecretKeySpec123".getBytes(), "AES"));
			return new String(decrypt.doFinal(Base64.decode(res.getBytes("windows-1251"), Base64.DEFAULT)), "windows-1251");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String encryptLib(String res) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("SecretKeySpec123".getBytes(), "AES"));
			return new String(Base64.encode(cipher.doFinal(res.getBytes("windows-1251")), Base64.DEFAULT), "windows-1251");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	private static int search(char[] arr, char c) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == c) return i;
		}
		return -1;
	}
	public static String decryptVigenere(String res) {
		final int keyLen = key.length();
		int[] keyArr= new int[keyLen];
		for (int i = 0; i < keyLen; i++) {
			keyArr[i] = search(alphabet_RU_L, key.charAt(i));
		}
		StringBuilder decrypt = new StringBuilder();
		int buff;
		char cur;
		int plusVal;
		for (int i = 0; i < res.length(); i++) {
			cur = res.charAt(i);
			plusVal = keyArr[i%keyLen];

			if (cur <= 'я' && cur >= 'а') {
				buff = (char) ((search(alphabet_RU_L, cur) + 33 - plusVal) % 33);
				decrypt.append(alphabet_RU_L[buff]);
			}
			else if (cur <= 'Я' && cur >= 'А') {
				buff = (char) ((search(alphabet_RU_U, cur) + 33 - plusVal) % 33);
				decrypt.append(alphabet_RU_U[buff]);
			}
			else {
				buff = cur;
				decrypt.append((char) buff);
			}
		}
		return decrypt.toString();
	}
	public static String encryptVigenere(final String res) {
		final int keyLen = key.length();
		int[] keyArr= new int[keyLen];
		for (int i = 0; i < keyLen; i++) {
			keyArr[i] = search(alphabet_RU_L, key.charAt(i));
		}
		StringBuilder encrypt = new StringBuilder();
		int buff;
		char cur;
		int plusVal;
		for (int i = 0; i < res.length(); i++) {
			cur = res.charAt(i);
			plusVal = keyArr[i%keyLen];

			if (cur <= 'я' && cur >= 'а') {
				buff = (char) ((search(alphabet_RU_L, cur) + plusVal) % 33);
				encrypt.append(alphabet_RU_L[buff]);
			}
			else if (cur <= 'Я' && cur >= 'А') {
				buff = (char) ((search(alphabet_RU_U, cur) + plusVal) % 33);
				encrypt.append(alphabet_RU_U[buff]);
			}
			else {
				buff = cur;
				encrypt.append((char) buff);
			}
		}
		return encrypt.toString();
	}
	public static String decryptHuffman(String res) {
		return res;
	}
	public static String encryptHuffman(String res) {
		return res;
	}
}
