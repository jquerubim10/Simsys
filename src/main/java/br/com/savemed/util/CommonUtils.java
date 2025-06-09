package br.com.savemed.util;

import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.scheduler.Agendamento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CommonUtils {
    private CommonUtils() {}


    public static  String generateNewUniqueId(String originalId) {
        return originalId + "-" + UUID.randomUUID().toString().substring(0, 22);
    }
    public static List<String> sanitizePhoneNumbers(List<String> contatos) {
        return contatos.stream()
                .filter(Objects::nonNull)
                .map(c -> c.replaceAll("\\D", "")) // Remove tudo que não for dígito
                .filter(c -> !c.isEmpty()) // Remove entradas vazias
                .collect(Collectors.toList());
    }


}
