/*
 *  This file is part of Bracket Properties
 *  Copyright 2014 David R. Smith
 *
 */
package asia.redact.bracket.properties;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import asia.redact.bracket.properties.mgmt.PropertiesReference;
import asia.redact.bracket.properties.mgmt.ReferenceType;
/**<pre>
 * Support for obfuscated properties. 
 * 
 * This is essentially encryption. However, because the code below is open-sourced, you should not
 * use it as a security feature, merely an obfuscation to make reading the values in place more difficult.
 * 
 * To make it into a security feature, you can add a salt file which will in essence alter the key derivation
 * to become private to your installation.
 * 
 * Just put a file called "salt.txt" into the home folder or the top of the classpath with the following contents:
 * 
 * salt=xxxx
 * 
 * where xxxx is some random string of any non-zero length. It will be added as salt. <b>Note: if you do this,
 * your obfuscated file data is no longer portable, at least not without some effort.</b>
 * </pre>
 * 
 * 
 * @author Dave
 *
 */
public final class Obfuscate {
	
	final byte [] salt = {0x23,0x42,0x10,0x37,0x08,0x77,0x55,0x19};
	final char [] password = {'t','g','A','6','Q','u','/', 'g','1','8','y','G',
			't','L','G','d','z','4','l','D','v','O','s','U','J','A','j',
			'o','9','i','z','o','X','i','g','8','X','F','E','P','f','l','I'};
	
	// should work on most java 6 or better installs
	final int KEY_LENGTH = 128;
	final int ITERATIONS = 65536;
	
	final byte [] addedSalt;

	private Obfuscate() {
		
		File file = new File(System.getProperty("user.home")+File.separator+"salt.txt");
		List<PropertiesReference> list = new ArrayList<PropertiesReference>();
		list.add(new PropertiesReference(ReferenceType.CLASSLOADED, "/salt.txt"));
		if(file.exists()) {
			list.add(new PropertiesReference(ReferenceType.EXTERNAL, file));
		}
		Properties props = Properties.Factory.loadReferences(list);
		if(props.containsKey("salt")){
			addedSalt = props.get("salt").getBytes(Charset.forName("UTF-8"));
		}else{
			addedSalt = null;
		}
	}
	
	public static final Obfuscate FACTORY = new Obfuscate();

	/**
	 * Simple main method for utility use case. 
	 * Pass in values on the command line and they will be emitted in obfuscated form
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length == 0) {
			System.err.println("Please provide an argument, the string to be obfuscated");
			return;
		}
		
		Obfuscate o = new Obfuscate();
		for(String in: args){
			String s = o.encrypt(in);
			System.out.println(s);
		}
	}
	
	private byte [] salt(){
		if(addedSalt == null) return salt;
		else {
			byte [] s = new byte[salt.length+addedSalt.length];
			System.arraycopy(salt, 0, s, 0, salt.length);
			System.arraycopy(addedSalt, 0, s, salt.length, addedSalt.length);
			return s;
		}
	}
	
	public final String decrypt(String inBase64){
		byte [] all = javax.xml.bind.DatatypeConverter.parseBase64Binary(inBase64);
		byte [] iv = new byte[16];
		System.arraycopy(all,0,iv,0,16);
		
		byte [] ciphertext = new byte[all.length-16];
		System.arraycopy(all, 16, ciphertext, 0, all.length-16);
		
		try {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password, salt(), ITERATIONS, KEY_LENGTH);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		return new String(cipher.doFinal(ciphertext), "UTF-8");
		}catch(Exception x){
			throw new RuntimeException(x);
		}
	}
	
	public final byte [] decryptToBytes(String inBase64){
		byte [] all = javax.xml.bind.DatatypeConverter.parseBase64Binary(inBase64);
		byte [] iv = new byte[16];
		System.arraycopy(all,0,iv,0,16);
		
		byte [] ciphertext = new byte[all.length-16];
		System.arraycopy(all, 16, ciphertext, 0, all.length-16);
		
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt(), ITERATIONS, KEY_LENGTH);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			return cipher.doFinal(ciphertext);
		}catch(Exception x){
			throw new RuntimeException(x);
		}
	}
	
	public final char [] decryptToChar(String inBase64){
		return decryptToChar(inBase64, Charset.forName("UTF-8"));
	}
	
	public final char [] decryptToChar(String inBase64, Charset charset){
		byte [] all = javax.xml.bind.DatatypeConverter.parseBase64Binary(inBase64);
		byte [] iv = new byte[16];
		System.arraycopy(all,0,iv,0,16);
		
		byte [] ciphertext = new byte[all.length-16];
		System.arraycopy(all, 16, ciphertext, 0, all.length-16);
		
		try {
			
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt(), ITERATIONS, KEY_LENGTH);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			byte [] result = cipher.doFinal(ciphertext);
			ByteBuffer buf = ByteBuffer.wrap(result);
			CharBuffer cBuf = charset.decode(buf);
			return cBuf.array();
			
		}catch(Exception x){
			throw new RuntimeException(x);
		}
	}
	
	public final String encrypt(char [] in, Charset charset){
		CharBuffer cBuf = CharBuffer.wrap(in);
		ByteBuffer result = charset.encode(cBuf);
		return encrypt(result.array());
	}
	
	// assume UTF-8
	public final String encrypt(char [] in){
		CharBuffer cBuf = CharBuffer.wrap(in);
		ByteBuffer result = Charset.forName("UTF-8").encode(cBuf);
		return encrypt(result.array());
	}
	
	public final String encrypt(byte [] in){
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt(), ITERATIONS, KEY_LENGTH);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			
			byte[] ciphertext = cipher.doFinal(in);
			byte[] all = new byte[iv.length+ciphertext.length];
			
			System.arraycopy(iv, 0, all, 0, iv.length);
			System.arraycopy(ciphertext, 0, all, iv.length, ciphertext.length);
			
			return javax.xml.bind.DatatypeConverter.printBase64Binary(all);
			
		}catch(Exception x){
			throw new RuntimeException(x);
		}
	}
	
	public final String encrypt(String in){
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt(), ITERATIONS, KEY_LENGTH);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			
			byte[] ciphertext = cipher.doFinal(in.getBytes("UTF-8"));
			byte[] all = new byte[iv.length+ciphertext.length];
			
			System.arraycopy(iv, 0, all, 0, iv.length);
			System.arraycopy(ciphertext, 0, all, iv.length, ciphertext.length);
			
			String body = javax.xml.bind.DatatypeConverter.printBase64Binary(all);
			
			return body;
		}catch(Exception x){
			throw new RuntimeException(x);
		}
	}
}
