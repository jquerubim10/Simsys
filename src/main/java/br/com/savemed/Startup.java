package br.com.savemed;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryResult;
import br.com.savemed.model.query.QueryReturn;
import jakarta.persistence.*;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Startup {

    public static void main(String[] args) throws IOException {

        SpringApplication.run(Startup.class, args);
        //generateABase64();
    }

    public static void generateABase64() {
        //File 'mombirthday.txt' does not exist
        try {
            byte[] bytesInput = FileUtils.readFileToByteArray(new File("C:/m2/sign_joao.pfx"));


            // Encode data on your side using BASE64
            String stringRepresentation = new String(bytesInput, StandardCharsets.ISO_8859_1);
            //System.out.println("String representation: " + stringRepresentation);
            // Decode data on other side, by processing encoded data
            byte[] valueDecoded = stringRepresentation.getBytes(StandardCharsets.ISO_8859_1);
            //System.out.println("Decoded value is " + new String(valueDecoded));

            System.out.println(new String(valueDecoded));
            //generateAFile(valueDecoded);
            insert(new String(valueDecoded));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static void insert(String pfXValue) {

        try {
            String query =  "INSERT INTO PFX_USER ( USER, PFX_VALUE ) VALUES ( 'M2','" + pfXValue + "')";
            //System.out.println(query);
        } catch (Exception e) {
            HibernateUtil.shutdown();
        }
    }

    public static void generateAFile(byte[] valueDecoded) {
        String fileName = "C:/m2/file.txt";

        try (FileOutputStream fs = new FileOutputStream(fileName);
             BufferedOutputStream bs = new BufferedOutputStream(fs)) {


            bs.write(valueDecoded);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}