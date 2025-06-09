package br.com.savemed.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class CertificateA1 extends CertificateData {

	private MultipartFile pfxFile;
	private String password;

	public CertificateA1() { }

	public CertificateA1(MultipartFile pfxFile, String password) {
		super();
		this.pfxFile = pfxFile;
		this.password = password;

		setIdentify(UUID.randomUUID().toString());
	}

	public MultipartFile getPath() {
		return pfxFile;
	}

	public String getPassword() {
		return password;
	}

}
