package com.umc.footprint.src.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.footprint.config.EncryptProperties;
import com.umc.footprint.src.users.model.PostLoginReq;
import com.umc.footprint.utils.AES128;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class AESTest {

    private EncryptProperties encryptProperties;

    @Autowired
    public AESTest(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @BeforeEach
    public void setup() {
        encryptProperties.setKey("o9pqYVC9-F8_.PEzEiw!L9F6.AYj9jcfVJ*_i.ifXYnyE68kix@Q2dL6rw*bV-rpdZYwcqZG-jPF-fw3CiJyKsfZ778ks-*jnZn");
    }

    @Test
    void encryptTest() throws JsonProcessingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        ObjectMapper objectMapper = new ObjectMapper();
        PostLoginReq postLoginReq = PostLoginReq.builder()
                .userId("1231245124")
                .email("dlxortmd987@gmail.com")
                .username("이택승")
                .providerType("google")
                .build();

        String data = objectMapper.writeValueAsString(postLoginReq);
        System.out.println("data = " + data);
        String encrypt = new AES128(encryptProperties.getKey()).encrypt(data);
        System.out.println("encrypt = " + encrypt);

        String decrypt = new AES128(encryptProperties.getKey()).decrypt(data);
        System.out.println("decrypt = " + decrypt);
        PostLoginReq origin = objectMapper.readValue(decrypt, PostLoginReq.class);

        assertEquals(postLoginReq.getUserId(), origin.getUserId());
        assertEquals(postLoginReq.getEmail(), origin.getEmail());
        assertEquals(postLoginReq.getUsername(), origin.getUsername());
        assertEquals(postLoginReq.getProviderType(), origin.getProviderType());
    }
}
