package com.totaldocs.security;

import java.security.MessageDigest;

import org.springframework.security.crypto.password.PasswordEncoder;

public class Md5PasswordEncoder implements PasswordEncoder{

	@Override
	public String encode(CharSequence rawPassword) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(rawPassword.toString().getBytes("UTF-8"));
			
			StringBuilder sb = new StringBuilder();
			
			for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
			
			System.out.println(sb.toString());
            return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar MD5", e);
		}
	}

	@Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
	    
        return encode(rawPassword).equalsIgnoreCase(encodedPassword);
    }

}
