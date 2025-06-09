package br.com.savemed.helper;


import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileConversionUtil implements  MultipartFile {

    private byte[] input;

    public FileConversionUtil(byte[] input) {
        this.input = input;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getOriginalFilename() {
        return "";
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public boolean isEmpty() {
        return input == null || input.length == 0;
    }

    @Override
    public long getSize() {
        return input.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return input;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(input);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try(FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(input);
        }
    }
}
