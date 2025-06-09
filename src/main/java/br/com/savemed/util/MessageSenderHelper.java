package br.com.savemed.util;

import br.com.savemed.dto.message.EntityReference;
import br.com.savemed.model.scheduler.Agendamento;
import br.com.savemed.model.scheduler.ConfiguracaoNotificacao;
import br.com.savemed.model.ws.SendMessageRequest;
import br.com.savemed.model.enums.CanalType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MessageSenderHelper {

    public static void applyDefaultFormatting(SendMessageRequest request, CanalType canal) {
        switch (canal) {
            case WHATSAPP -> formatPhoneNumbers(request,canal);
            case SMS -> formatPhoneNumbers(request,canal);
        }
    }
    public static String generateIdMensagem(EntityReference entityReference, CanalType canal) {
        return "AGD-" +  canal.name() + "-" + entityReference.getType() + "-" + entityReference.getId();
    }

    private static void formatPhoneNumbers(SendMessageRequest request, CanalType canal) {
        switch (canal) {
            case WHATSAPP -> request.setPhoneNumbers(
                    request.getPhoneNumbers().stream()
                            .map(num -> {
                                // Remove todos os caracteres não numéricos
                                String cleaned = num.replaceAll("[^0-9]", "");

                                // Se já contém sufixo correto, assume que já foi formatado
                                if (num.endsWith("@s.whatsapp.net")) {
                                    return num;
                                }

                                // Verifica se já tem um código de país (começa com um ou mais dígitos após o +)
                                if (cleaned.isEmpty()) {
                                    throw new IllegalArgumentException("Número de telefone vazio");
                                }

                                // Se o número já começa com código de país (ex: 351, 55, etc)
                                // apenas usa como está, sem adicionar 55
                                // Caso contrário, assume que é Brasil e adiciona 55
                                if (!cleaned.startsWith("1") &&  // Códigos de país não começam com 1
                                        !cleaned.matches("^[2-9]\\d{0,2}.*")) {  // Verifica se começa com código de país
                                    cleaned = "55" + cleaned;  // Padrão Brasil
                                }

                                // Valida tamanho mínimo (considerando código de país + número local)
                                if (cleaned.length() < 8 || cleaned.length() > 15) {
                                    throw new IllegalArgumentException("Número de telefone inválido: " + cleaned);
                                }

                                return cleaned + "@s.whatsapp.net";
                            })
                            .toList()
            );
            case SMS -> request.setPhoneNumbers(
                    request.getPhoneNumbers().stream()
                            .map(num -> num.replaceAll("[^0-9]", ""))
                            .toList()
            );
            default -> throw new IllegalArgumentException("Canal não suportado: " + canal);
        }
    }

    public static String resolveToken(String headerToken, String bodyToken) {
        return (headerToken != null) ? headerToken : bodyToken;
    }
    public static String processToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token de autenticação não fornecido.");
        }
        return Base64.getEncoder().encodeToString(token.getBytes());
    }

    public static  String extrairPrimeiroNome(String nomeCompleto) {
        return Optional.ofNullable(nomeCompleto)
                .filter(s -> !s.isEmpty())
                .map(s -> s.split("\\s+")[0])
                .orElse("");
    }
    public static String formatarDataAgendamento(Date data) {
        return LocalDateTime.ofInstant(data.toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("EEEE dd/MM 'às' HH:mm")
                        .withLocale(new Locale("pt", "BR")));
    }
    public static  String construirMensagem(ConfiguracaoNotificacao config, String paciente, String data, Agendamento agendamento) {
        return config.getMensagemPadrao()
                .replace("{PACIENTE}", paciente)
                .replace("{DATA}", data)
                .replace("{PROFISSIONAL}", String.valueOf(agendamento.getMedicoID()));
    }
}