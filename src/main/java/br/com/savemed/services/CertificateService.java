package br.com.savemed.services;

import br.com.savemed.exceptions.StoreException;
import br.com.savemed.model.CertificateA1;
import br.com.savemed.model.CertificateData;
import br.com.savemed.model.CertificateFromStore;
import br.com.savemed.model.Store;
import br.com.savemed.util.Constants;
import lombok.SneakyThrows;
import org.bouncycastle.cert.CertException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.security.KeyStore;

public class CertificateService {
	
	public static List<CertificateFromStore> loadInstalledCertificates() {

		var certs = searchStoresFromSystem().stream()
				.map(Store::getInstanceCertificateFromStore)
				.collect(Collectors.toList());
		
		certs.add(0, new CertificateFromStore(null, "Selecione..."));
		
		return  certs;
	}
	
	@SneakyThrows
	public static CertificateData loadCertificateA1(MultipartFile pfxFile, String password) {
		var certificateA1 = new CertificateA1(pfxFile, password);
		// Carregar o KeyStore do arquivo PFX
		KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
		try (InputStream inputStream = pfxFile.getInputStream()) {
			keyStore.load(inputStream, password.toCharArray());
		} catch (CertificateException e) {
            throw new CertificateException("Error pin incorreto! verifique e tente novamente");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error pin incorreto! verifique e tente novamente");
        }

		// Recuperar os aliases do KeyStore
		StringBuilder aliases = new StringBuilder();
		Enumeration<String> aliasEnumeration = keyStore.aliases();
		List<String> aliasList = Collections.list(aliasEnumeration); // Converte para lista
		for (String alias : aliasList) {
			// Verifica se o alias é um certificado
				aliases.append(alias).append("\n");
				// Recupera o certificado
				X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
				certificateA1.setName(extractName(certificate));
				certificateA1.setCPF(extractCPF(certificate));
				certificateA1.setSubject(certificate.getSubjectDN().toString());
				certificateA1.setIssuer(certificate.getIssuerDN().toString());
				certificateA1.setValidfrom(certificate.getNotBefore().toString());
				certificateA1.setValiduntil(certificate.getNotAfter().toString());

				/**
				System.out.println("Alias: " + alias);
				System.out.println("Subject: " + certificate.getSubjectDN());
				System.out.println("Issuer: " + certificate.getIssuerDN());
				System.out.println("Valid from: " + certificate.getNotBefore());
				System.out.println("Valid until: " + certificate.getNotAfter());
				System.out.println("=====================================");
				 **/
		}
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(aliases.toString().trim(), password.toCharArray());
		Certificate[] chain = keyStore.getCertificateChain(aliases.toString().trim());

		if (privateKey == null || chain == null) {
			throw new Exception("Chave privada ou cadeia de certificados não encontrada.");
		}

		certificateA1.setMapCertificateAndKey(getX509CertificateFromFile(keyStore, password));

		return certificateA1;
		
	}
	
	public static Set<Store> searchStoresFromSystem() {
		
		Set<Store> stores = new HashSet<Store>();
		
		try {
			KeyStore keyStore = getKeyStoreFromSystem();
			keyStore.load(null, null);
			
			for (final Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();) 
				stores.add(new Store(e.nextElement()));
			
		} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao carregar os certificados" + "Não foi possível acessar a repositório de certificados do Windows.");
		} catch (StoreException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao carregar os certificados", e );
		}
		
		return stores;
	}
	
	public static Map<X509Certificate, PrivateKey> getX509CertificateFromStore(CertificateFromStore selectedCertificate) {
		var certificateKeyMap = new HashMap<X509Certificate, PrivateKey>();
		 
		try {
			KeyStore keyStore = getKeyStoreFromSystem();
			keyStore.load(null, null);
			
			final PrivateKey privateKey = (PrivateKey) keyStore.getKey(selectedCertificate.getName(), null);
			final X509Certificate certificate = (X509Certificate) keyStore.getCertificate(selectedCertificate.getName());

			certificateKeyMap.put(certificate, privateKey);

		} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao carregar o certificado x509: Não foi possível acessar o repositório de certificados do Windows.", e);
		} catch (StoreException e) {
			throw new RuntimeException("Erro ao carregar o certificado x509: " + e.getMessage(), e);
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao carregar o certificado x509: Não foi possível carregar a chave do certificado.", e);
		}
		
		return certificateKeyMap;
	}

	private static Map<X509Certificate, PrivateKey> getX509CertificateFromFile(KeyStore keyStore, String password) {
		Map<X509Certificate, PrivateKey> certificateKeyMap = new HashMap<X509Certificate, PrivateKey>();

		try {
			
			X509Certificate certificate = null;
			PrivateKey privateKey = null;
			String alias;
			
			for (Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();) {
				alias = e.nextElement();
				certificate = (X509Certificate) keyStore.getCertificate(alias);
				privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
				certificateKeyMap.put(certificate, privateKey);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao carregar o certificado A1" + "Não foi possível carregar o certificado, verifique a senha ou permissão de leitura.");
		}
		
		return certificateKeyMap;
	}
	

	private static KeyStore getKeyStoreFromSystem() throws KeyStoreException, NoSuchProviderException, StoreException {
		
		var isWindows = Constants.SYSTEM_NAME.indexOf(Constants.WINDOWS) >= 0;
		var isMacOs = Constants.SYSTEM_NAME.indexOf(Constants.MAC) >= 0;
		
		KeyStore keyStore = null;
		
		if (isWindows)
			keyStore = KeyStore.getInstance(Constants.WINDOWS_KEY_STORE_TYPE, Constants.WINDOWS_KEY_STORE_PROVIDER);
		else if (isMacOs)
			keyStore = KeyStore.getInstance(Constants.MAC_KEY_STORE_TYPE, Constants.MAC_KEY_STORE_PROVIDER);
		
		if (keyStore == null) 
			throw new StoreException("Sistema operacional" + Constants.SYSTEM_NAME + " não suportado!");

		return keyStore;
	}
	// Método para extrair o CPF do certificado
	private static String extractCPF(X509Certificate cert) {
		// Extrai o nome do sujeito no formato "CN=JOAO BATISTA QUERUBIM FILHO:07464617630"
		String subject = cert.getSubjectX500Principal().getName();

		// Procura pelo prefixo "CN=" e captura o CPF subsequente ao ":"
		String cpf = null;
		String[] subjectArray = subject.split(",");
		for (String field : subjectArray) {
			if (field.trim().startsWith("CN=")) {
				String cnField = field.trim().substring(3);  // Remove o "CN="
				int colonIndex = cnField.indexOf(':');
				if (colonIndex != -1 && colonIndex + 1 < cnField.length()) {
					cpf = cnField.substring(colonIndex + 1);  // Captura o valor após ":"
				}
				break;
			}
		}

		if (cpf == null) {
			// CPF não encontrado
			return "000.000.000-00"; // Valor padrão fictício
		}

		return cpf;
	}

	private static String extractName(X509Certificate cert) {
		String subject = cert.getSubjectX500Principal().getName();

		// Extrai apenas o CN (Common Name)
		for (String part : subject.split(",")) {
			if (part.trim().startsWith("CN=")) {
				String cn = part.trim().substring(3);  // Extrai o valor do CN
				// Verifica se existe um ':' e, se sim, retorna apenas a parte antes dele
				int colonIndex = cn.indexOf(':');
				if (colonIndex != -1) {
					return cn.substring(0, colonIndex);  // Retorna a parte antes de ':'
				}
				return cn;  // Retorna o CN completo se não houver ':'
			}
		}
		return "Nome não encontrado";
	}
	
	
}
