package com.example.test;

import com.example.encryption.SecureEncryption;

public class Test {

    public static void main(String[] args) {
        SecureEncryption encr = new SecureEncryption(1090L);
        System.out.println(encr.decrypt(encr.encrypt("hello")));
    }
}
