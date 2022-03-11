package com.umc.footprint.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FilterServletOutputStream extends ServletOutputStream{
    private final DataOutputStream outputStream;

    public FilterServletOutputStream(OutputStream output){
        this.outputStream = new DataOutputStream(output);
    }

    // FilterServletOutputStream이 호출되면 write를 통해 outputStream에 Response Body의 데이터를 쌓을 수 있다.
    @Override
    public void write(int b) throws IOException{
        outputStream.write(b);
    }

    @Override
    public boolean isReady(){
        return true;
    }

    @Override
    public void setWriteListener(WriteListener listener){

    }
}
