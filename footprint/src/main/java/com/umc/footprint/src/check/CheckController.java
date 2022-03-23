package com.umc.footprint.src.check;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/check")
public class CheckController {

    private final CheckService checkService;

    public CheckController(CheckService checkService){
        this.checkService = checkService;
    }


    @ResponseBody
    @PostMapping("/encrypt") // (POST) 127.0.0.1:3000/walks/check/encrypt
    public BaseResponse<String> checkEncryptWalk(@RequestBody String encryptString){
        try {

            String encryptedString = checkService.checkEncryptWalk(encryptString);

            return new BaseResponse<>(encryptedString);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @PostMapping("/decrypt") // (POST) 127.0.0.1:3000/walks/check/decrypt
    public BaseResponse<String> checkDecryptWalk(@RequestBody String decryptString) throws BaseException {

        String decryptedString = checkService.checkDecryptWalk(decryptString);

        return new BaseResponse<>(decryptedString);
    }


}
