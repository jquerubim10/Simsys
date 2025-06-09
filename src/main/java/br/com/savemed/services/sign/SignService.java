package br.com.savemed.services.sign;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.exceptions.StoreException;
import br.com.savemed.helper.FileConversionUtil;
import br.com.savemed.model.CertificateData;
import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.file.PfxUser;
import br.com.savemed.model.file.SignedDocs;
import br.com.savemed.model.query.QueryReturn;
import br.com.savemed.repositories.SignRepository;
import br.com.savemed.repositories.UserSaveRepository;
import br.com.savemed.services.CertificateService;
import br.com.savemed.services.file.PfxUserService;
import br.com.savemed.services.file.SignedDocsService;
import br.com.savemed.util.Constants;
import br.com.savemed.util.PasswordEncryptor;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;

@Service
public class SignService  {

    private static final Logger LOGGER = Logger.getLogger(SignService.class.getName());
    private CertificateData selectedCertificate;

    private final SignRepository signRepository;
    private final UserSaveRepository userSaveRepository;
    private final SignedDocsService signedDocsService;

    @Value("${spring.datasource.driver-class-name}")
    String driver;

    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String user;

    @Value("${spring.datasource.password}")
    String password;

    @Value("${signature.path}")
    String signatureLocation;

    @Autowired
    @PersistenceContext
    EntityManager entityManager;

    PfxUserService service;

    @Autowired
    public SignService(SignRepository signRepository, PfxUserService service, UserSaveRepository userSaveRepository, SignedDocsService signedDocsService) {
        this.userSaveRepository = userSaveRepository;
        this.signRepository = signRepository;
        this.service = service;
        this.signedDocsService = signedDocsService;
    }

    public UserSave findByUserPass(String logon, String senha) {
        LOGGER.info("Buscando usuário por login and password!");

        UserSave optionalUser = signRepository.findByLogon(logon);

        if (Objects.nonNull(optionalUser) && optionalUser.getSenha().equals(PasswordEncryptor.cryptPassword(senha, true))) {
            return optionalUser;
        } else {
            throw new ResourceNotFoundException("Usuario ou senha Invalido");
        }
    }

    public SignedDocs getSignedFile(Long id) {
        return signedDocsService.getSignedDocument(id);
    }

    public SignedDocs signingSaveTo(MultipartFile file, String pin, String tableName, Long tableId, Long userId, String whereClauseColumn) throws Exception {
        LOGGER.info("[init] --- [signingSaveTo] --- [SignService] ----------  Metodo para assinatura e upload na base");
        LOGGER.info("[init] --- [signingSaveTo] --- [SignService] ----------  Buscando usuário por id!");

        UserSave optionalUser = userSaveRepository.findById(userId);

        if (!Objects.nonNull(optionalUser)) {
            throw new ResourceNotFoundException("Usuario ou senha Invalido");
        }

        LOGGER.info("[success] --- [signingSaveTo] --- [SignService] ----------  Usuário recuperado com sucesso por id!");
        LOGGER.info("[signingSaveTo] --- [SignService] ----------  Buscando pfx do usuario!");

        PfxUser user = service.getFile(optionalUser.getLogon());

        if (Objects.isNull(user)) {
            throw new ResourceNotFoundException("User not have a pfx file");
        }

        LOGGER.info("[success] --- [signingSaveTo] --- [SignService] ----------  Usuário tem pfx no sistema!");
        LOGGER.info("[signingSaveTo] --- [SignService] ----------  Iniciar assinatura!");

        MultipartFile fileConversionUtil = new FileConversionUtil(user.getData());
        return signingPdfSave(file, fileConversionUtil, pin, tableName, tableId, userId, whereClauseColumn);
    }

    /**
     * Assina um arquivo PDF e adiciona um carimbo com informações básicas
     */
    public SignedDocs signingPdfSave(MultipartFile pdfFile, MultipartFile pfxFile, String pin, String sourceTable, Long sourceTableId, Long signedUserId, String whereClauseColumn) throws Exception {
        LOGGER.info("[init] --- [signingPdfSave] --- [SignService] ----------  Gerando arquivo assinado!");

        Security.addProvider(new BouncyCastleProvider());
        int INITIAL_SIZE = 32;

        try {
            selectedCertificate = CertificateService.loadCertificateA1(pfxFile, pin);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Pin incorreto! verifique e tente novamente!!");
        }

        X509Certificate x509Cert = (X509Certificate) selectedCertificate.getCertificate();

        // Gera o PDF com a assinatura visível primeiro
        ByteArrayOutputStream baosWithVisibleSignature = addVisibleSignatureToPdf(pdfFile.getBytes(), selectedCertificate.getName(), selectedCertificate.getCPF(), "");

        // Agora assina o PDF visualmente assinado
        ByteArrayOutputStream baosSigned = getPdfAssinado(baosWithVisibleSignature.toByteArray(), x509Cert, selectedCertificate.getPrivateKey());

        String signatureHash = generateSHA256Hash(baosSigned.toByteArray());
        Path pathWrite = Paths.get(signatureLocation + signatureHash + ".pdf");

        // Salva o arquivo assinado
        if (baosSigned.size() > INITIAL_SIZE) {
            try (FileOutputStream fos = new FileOutputStream(pathWrite.toFile())) {
                LOGGER.info("[success] --- [signingPdfSave] --- [SignService] ----------  Salvo arquivo assinado!");
                fos.write(baosSigned.toByteArray());
            }
        }

        LOGGER.info("[init] --- [signingPdfSave] --- [SignService] ----------  Persistence do arquivo na base!");
        MultipartFile signedFile = new FileConversionUtil(baosSigned.toByteArray());
        SignedDocs response = this.signedDocsService.persistSignedFile(signedUserId, sourceTable, sourceTableId, signatureHash, signedFile, whereClauseColumn);

        if (Objects.isNull(response)) {
            throw new ResourceNotFoundException("Error no persistence file !!");
        }

        LOGGER.info("[success] --- [signingPdfSave] --- [SignService] ----------  Persistence do arquivo na base com sucesso!");

        LOGGER.info("[init] --- [signingPdfSave] --- [SignService] ----------  Atualizando a tabela destino para saber que o registro tem assinatura");
        String query = "UPDATE " + response.getSourceTable() +
                " SET IdSignedDocs = " + response.getId() +
                " WHERE " + response.getWhereClauseColumn() + " = " + response.getSourceTableId();

        dynamicInsert(query);
        return response;
    }

    public void signPdfUser(MultipartFile pdfFile, String pfxPassword, String userData) throws Exception {
        PfxUser user = service.getFile(userData);

        if (Objects.isNull(user)) {
            throw new ResourceNotFoundException("User not have a pfx file");
        }

        MultipartFile fileConversionUtil = new FileConversionUtil(user.getData());
        signPdf(pdfFile, fileConversionUtil, pfxPassword);
    }

    /**
     * Assina um arquivo PDF e adiciona um carimbo com informações básicas
     */
    public void signPdf(MultipartFile pdfFile, MultipartFile pfxFile, String pfxPassword) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        int INITIAL_SIZE = 32;
        byte[] bytesOfFile = null;

        selectedCertificate = CertificateService.loadCertificateA1(pfxFile, pfxPassword);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        baos = getPdfAssinado(pdfFile.getBytes(), selectedCertificate.getCertificate(), selectedCertificate.getPrivateKey());
         X509Certificate x509Cert = (X509Certificate) selectedCertificate.getCertificate();
//        String signatureHash = generateSHA256Hash(baos.toByteArray() );
//        System.out.println("HASH: " + signatureHash);
        // Gera o PDF com a assinatura visível primeiro
        ByteArrayOutputStream baosWithVisibleSignature = addVisibleSignatureToPdf(pdfFile.getBytes(), selectedCertificate.getName(), selectedCertificate.getCPF(), "");

        // Agora assina o PDF visualmente assinado
        ByteArrayOutputStream baosSigned = getPdfAssinado(baosWithVisibleSignature.toByteArray(), x509Cert, selectedCertificate.getPrivateKey());

        String signatureHash = generateSHA256Hash(baosSigned.toByteArray());
        //System.out.println("HASH: " + signatureHash);



        // Salva o arquivo assinado
        if (baosSigned.size() > INITIAL_SIZE) {
            Path pathWrite = Paths.get(signatureLocation + signatureHash + ".pdf");
            try (FileOutputStream fos = new FileOutputStream(pathWrite.toFile())) {
                fos.write(baosSigned.toByteArray());
            }
        }
//        PdfReader reader = new PdfReader(baos.toByteArray());
//        PdfStamper stamper = new PdfStamper(reader, baos);
//
//        // Adiciona a assinatura visível a cada página com o hash
//        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
//            addVisibleSignatureToPage(stamper, i, x509Cert.getSubjectX500Principal().getName(), "CPF_EXAMPLE", signatureHash);
//        }
//
//        stamper.close();
//        reader.close();

//        if (baos.size() > INITIAL_SIZE) {
//            Path pathWrite = Paths.get("C:/m2/resources/upload/signed_" + pdfFile.getOriginalFilename());
//            try (FileOutputStream fos = new FileOutputStream(pathWrite.toFile())) {
//                fos.write(baos.toByteArray());
//            }
//        }
//         if (baos.size() > INITIAL_SIZE) {
//            Path pathWrite = Paths.get("C:/m2/resources/upload/signed_" + pdfFile.getOriginalFilename());
//            try (FileOutputStream fos = new FileOutputStream(pathWrite.toFile())) {
//                fos.write(baos.toByteArray());
//            }
//        }
    }

    public ByteArrayOutputStream addVisibleSignatureToPdf(byte[] pdfBytes, String nome, String cpf, String signatureHash) throws IOException, DocumentException, NoSuchAlgorithmException {
        PdfReader reader = new PdfReader(pdfBytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);

        signatureHash = generateSHA256Hash(baos.toByteArray());

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            addVisibleSignatureToPage(stamper, i, nome, cpf, signatureHash);
        }

        stamper.close();
        reader.close();

        return baos;
    }

    private ByteArrayOutputStream getPdfAssinado(byte[] bytes, Certificate certificate, PrivateKey privateKey) throws  IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfReader reader = new PdfReader(bytes);
            // Salvar o PDF modificado
            PdfStamper stamper = null;
            Security.addProvider(new BouncyCastleProvider());

            AcroFields af = reader.getAcroFields();
            ArrayList<String> names = af.getSignatureNames();

            if (!names.isEmpty()) {
                stamper = PdfStamper.createSignature(reader, baos, '\000', null, true);
            } else {
                stamper = PdfStamper.createSignature(reader, baos, '\000');
            }

            PdfSignatureAppearance signatureAppearance = stamper.getSignatureAppearance();
            signatureAppearance.setCertificate(certificate);
            signatureAppearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);

            PdfSignature signature = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
            signature.setReason(signatureAppearance.getReason());
            signature.setLocation(signatureAppearance.getLocation());
            signature.setContact(signatureAppearance.getContact());
            signature.setDate(new PdfDate(signatureAppearance.getSignDate()));

            signatureAppearance.setCryptoDictionary(signature);

            ExternalSignature externalSignature = null;
            if (privateKey instanceof RSAPrivateKey)
                externalSignature = new PrivateKeySignature((RSAPrivateKey) privateKey, "SHA-256", "BC");
            else
                externalSignature = new PrivateKeySignature(privateKey, "SHA-256", Constants.getKeyStoreProvider());

            ExternalDigest digest = new BouncyCastleDigest();
            MakeSignature.signDetached(signatureAppearance, digest, externalSignature, new Certificate[]{certificate}, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
            stamper.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            throw new IOException( "Não foi possivel manipular o arquivo pdf",e);

        } catch (GeneralSecurityException | RuntimeException e) {
            e.printStackTrace();
            throw new IOException("Não foi possivel realizar a assinatura do pdf, verifique o certificado selecionado", e);

        } catch (StoreException e) {
            throw new RuntimeException(e);
        }
        return baos;
    }

    // Método para gerar o hash SHA-256 de um array de bytes (PDF)
    public String generateSHA256Hash(byte[] fileContent) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileContent);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void addVisibleSignatureToPage(PdfStamper stamper, int pageNumber, String nome, String cpf, String signatureHash) throws DocumentException, IOException {
        PdfContentByte over = stamper.getOverContent(pageNumber);

        //llx – lower left x lly – lower left y urx – upper right x ury – upper right y
        Rectangle signatureRect = new Rectangle(30, 30, 300, 100); // Ajuste a altura do retângulo para acomodar o hash
        over.setColorStroke(BaseColor.BLACK);
        over.rectangle(signatureRect.getLeft(), signatureRect.getBottom(), signatureRect.getWidth(), signatureRect.getHeight());
        over.stroke();


        // Ajuste da fonte para ser menor e caber no retângulo
        Font font = new Font(Font.FontFamily.HELVETICA, 6);
        // Texto da assinatura visível
        ColumnText.showTextAligned(over, Element.ALIGN_LEFT, new Phrase("Assinado digitalmente por " + nome,font), signatureRect.getLeft() + 5, signatureRect.getTop() - 15, 0);
        ColumnText.showTextAligned(over, Element.ALIGN_LEFT, new Phrase("CPF: " + cpf,font ), signatureRect.getLeft() + 5, signatureRect.getTop() - 30, 0);
        ColumnText.showTextAligned(over, Element.ALIGN_LEFT, new Phrase("Data: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()),font), signatureRect.getLeft() + 5, signatureRect.getTop() - 45, 0);
        ColumnText.showTextAligned(over, Element.ALIGN_LEFT, new Phrase("Hash: " + signatureHash,font), signatureRect.getLeft() + 5, signatureRect.getTop() - 60, 0);
        // Adicionar metadados ao PDF com o hash da assinatura
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("AssinaturaHash", signatureHash);  // Adiciona o hash aos metadados
        stamper.setMoreInfo(metadata);
    }

    // Método para criar o retângulo da assinatura
    private PDRectangle createSignatureRectangle(PDDocument doc, Rectangle2D humanRect, PDPage page) {
        float x = (float) humanRect.getX();
        float y = (float) humanRect.getY();
        float width = (float) humanRect.getWidth();
        float height = (float) humanRect.getHeight();
        PDRectangle pageRect = page.getCropBox();
        PDRectangle rect = new PDRectangle();

        switch (page.getRotation()) {
            case 90:
                rect.setLowerLeftX(pageRect.getWidth() - y - height);
                rect.setUpperRightX(pageRect.getWidth() - y);
                rect.setLowerLeftY(x);
                rect.setUpperRightY(x + width);
                break;
            case 180:
                rect.setLowerLeftX(pageRect.getWidth() - x - width);
                rect.setUpperRightX(pageRect.getWidth() - x);
                rect.setLowerLeftY(pageRect.getHeight() - y - height);
                rect.setUpperRightY(pageRect.getHeight() - y);
                break;
            case 270:
                rect.setLowerLeftX(y);
                rect.setUpperRightX(y + height);
                rect.setLowerLeftY(pageRect.getHeight() - x - width);
                rect.setUpperRightY(pageRect.getHeight() - x);
                break;
            case 0:
            default:
                rect.setLowerLeftX(x);
                rect.setUpperRightX(x + width);
                rect.setLowerLeftY(y);
                rect.setUpperRightY(y + height);
                break;
        }
        return rect;
    }

    private String formatDataAssinatura() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    // Método para criar o template visual
    public byte[] addVisualSignatureToPdf(byte[] pdfFile, String nome, String cpf, String alias) throws IOException, NoSuchAlgorithmException {
        // Carregar o PDF
        PDDocument document = Loader.loadPDF(pdfFile );
        String signatureHash = generateSHA256Hash(pdfFile ); // Gera o hash da assinatura
        // Itera sobre todas as páginas do documento e adiciona a assinatura visual no rodapé
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            PDPage page = document.getPage(i);
            PDRectangle pageRect = page.getMediaBox();

            // Define a posição do rodapé para a assinatura
            float x = 20; // Distância da borda esquerda
            float y = 20; // Distância do rodapé
            float width = 200; // Largura da assinatura
            float height = 50; // Altura da assinatura

            Rectangle2D humanRect = new Rectangle2D.Float(x, y, width, height);
            PDRectangle rect = createSignatureRectangle(document, humanRect, page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.setStrokingColor(0, 0, 0); // Cor preta para o contorno
                contentStream.setLineWidth(1f);
                contentStream.addRect(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getWidth(), rect.getHeight());
                contentStream.stroke();

                // Ajuste da fonte para ser menor e caber no retângulo
                PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                contentStream.setFont(font, 6);  // Reduzido o tamanho da fonte para 6
                contentStream.setNonStrokingColor(0, 0, 0); // Preto

                // Linha 1: "Documento assinado digitalmente"
                contentStream.beginText();
                contentStream.newLineAtOffset(rect.getLowerLeftX() + 5, rect.getLowerLeftY() + rect.getHeight() - 8);
                contentStream.showText("Documento assinado digitalmente");
                contentStream.endText();

                // Linha 2: Nome extraído do certificado
                contentStream.beginText();
                contentStream.newLineAtOffset(rect.getLowerLeftX() + 5, rect.getLowerLeftY() + rect.getHeight() - 16);
                contentStream.showText(nome);
                contentStream.endText();

                // Linha 3: CPF extraído corretamente
                contentStream.beginText();
                contentStream.newLineAtOffset(rect.getLowerLeftX() + 5, rect.getLowerLeftY() + rect.getHeight() - 24);
                contentStream.showText("CPF: " + cpf);
                contentStream.endText();

                // Linha 4: Data da assinatura
                contentStream.beginText();
                contentStream.newLineAtOffset(rect.getLowerLeftX() + 5, rect.getLowerLeftY() + rect.getHeight() - 32);
                contentStream.showText("Data: " + formatDataAssinatura());
                contentStream.endText();

                // Linha 5: Data da assinatura
                contentStream.beginText();
                contentStream.newLineAtOffset(rect.getLowerLeftX() + 5, rect.getLowerLeftY() + rect.getHeight() - 40);
                contentStream.showText("Assinatura: " + signatureHash);
                contentStream.endText();
            }
        }

        // Salvar o PDF modificado em um ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();

        return baos.toByteArray();

    }

    // Configuration properties
    Map<String, Object> settings = new HashMap<>();

    public void dynamicInsert(String query) {
        settings.put(Environment.DRIVER, driver);
        settings.put(Environment.URL, url);
        settings.put(Environment.USER, user);
        settings.put(Environment.PASS, password);
        settings.put(Environment.SHOW_SQL, true);

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            LOGGER.info("[dynamicInsert] --- [SignService] ----------  Query --- \n " + query);

            // Executa o INSERT
            session.createNativeQuery(query, QueryReturn.class).executeUpdate();

            // Comita a transação
            session.getTransaction().commit();
            HibernateUtil.shutdown();
            LOGGER.info("[success] --- [dynamicInsert] --- [SignService] ----------  Tabela destino atualizada com sucesso para tem assinatura");
        } catch (Exception e) {
            HibernateUtil.shutdown();
            LOGGER.info("[error] --- [dynamicInsert] --- [SignService] ----------  Tabela destino nao atualizada para tem assinatura");
        }
    }
}
