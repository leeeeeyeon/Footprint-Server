package com.umc.footprint.filter;

import org.apache.commons.codec.DecoderException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class ResponseBodyEncryptWrapper extends HttpServletResponseWrapper {

    // 가로챈 데이터를 가공하여 담을 final 변수
    ByteArrayOutputStream output;
    FilterServletOutputStream filterOutput;

    public ResponseBodyEncryptWrapper(HttpServletResponse response) throws IOException, DecoderException {
        super(response);
        output = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if(filterOutput == null){
            filterOutput = new FilterServletOutputStream(output);
        }
        return filterOutput;
    }

    public byte[] getDataStream(){
        return output.toByteArray();
    }

}
