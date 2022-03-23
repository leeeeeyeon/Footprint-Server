package com.umc.footprint.src.check;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.EncryptProperties;
import com.umc.footprint.utils.AES128;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class CheckService {

    private final EncryptProperties encryptProperties;

    public CheckService(EncryptProperties encryptProperties){
        this.encryptProperties = encryptProperties;
    }

    public String checkEncryptWalk(String encryptString) throws BaseException {
        try{
            String encryptResult = new AES128(encryptProperties.getKey()).encrypt(encryptString);

            log.info("encryptResult = {}",encryptResult );

            return encryptResult;
        } catch(Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String checkDecryptWalk(String decryptString) throws BaseException{
        try{
            System.out.println("decryptString = " + decryptString);
            String decryptResult = new AES128(encryptProperties.getKey()).decrypt(decryptString);

            System.out.println("decryptResult = " + decryptResult);

            return decryptResult;
        } catch(Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
