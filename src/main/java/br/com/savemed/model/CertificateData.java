package br.com.savemed.model;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

public abstract class CertificateData {
	
	private String identify;
	private String name;
	private String CPF;
	private String Subject;
	private String Alias;
	private String Issuer;
	private String Validfrom;
	private String Validuntil;
	private Map<X509Certificate, PrivateKey> mapCertificateAndKey;

	public Certificate getCertificate() {
		final Set<Map.Entry<X509Certificate, PrivateKey>> set = mapCertificateAndKey.entrySet();
		final X509Certificate cert = (X509Certificate) set.iterator().next().getKey();

		return cert;
	}
	
	public Map<X509Certificate, PrivateKey> getMapCertificateAndKey() {
		return mapCertificateAndKey;
	}
	
	public void setMapCertificateAndKey(Map<X509Certificate, PrivateKey> mapCertificateAndKey) {
		this.mapCertificateAndKey = mapCertificateAndKey;
	}

	public PrivateKey getPrivateKey() {
		return mapCertificateAndKey.entrySet().iterator().next().getValue();
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public boolean hasIdentify() {
		return this.identify != null;
	}

	public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}
	public String getCPF() {
		return CPF;
	}

	public void setCPF(String CPF) {
		this.CPF = CPF;
	}
	public String getSubject() {
		return Subject;
	}

	public void setSubject(String Subject) {
		this.Subject = Subject;
	}
	public String getAlias() {
		return Alias;
	}

	public void setAlias(String Alias) {
		this.Alias = Alias;
	}
	public String getIssuer() {
		return Issuer;
	}

	public void setIssuer(String Issuer) {
		this.Issuer = Issuer;
	}
	public String getValidfrom() {
		return Validfrom;
	}

	public void setValidfrom(String Validfrom) {
		this.Validfrom = Validfrom;
	}
	public String getValiduntil() {
		return Validuntil;
	}

	public void setValiduntil(String Validuntil) {
		this.Validuntil = Validuntil;
	}
	
	
}
