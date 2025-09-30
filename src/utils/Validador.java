package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Validador {

    public static String lerStringNaoVazia(String mensagem, Scanner scanner) {
        String input;
        while (true) {
            System.out.print(mensagem);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Erro: A entrada não pode ser vazia. Tente novamente.");
        }
    }

    public static String lerCpf(String mensagem, Scanner scanner) {
        String cpf;
        while (true) {
            System.out.print(mensagem);
            cpf = scanner.nextLine().trim();
            if (cpf.matches("\\d{11}")) {
                return cpf;
            }
            System.out.println("Erro: CPF inválido. Deve conter exatamente 11 dígitos numéricos. Tente novamente.");
        }
    }
    
    // MÉTODO ATUALIZADO COM VALIDAÇÃO REALISTA
    public static String lerCrm(String mensagem, Scanner scanner) {
        String crm;
        while (true) {
            System.out.print(mensagem);
            crm = scanner.nextLine().trim().toUpperCase(); // Converte para maiúsculas para facilitar a validação
            // Regex: \d+      -> um ou mais dígitos
            //          [A-Z]{2} -> exatamente duas letras maiúsculas
            if (crm.matches("\\d+[A-Z]{2}")) {
                return crm;
            }
            System.out.println("Erro: Formato de CRM inválido. Exemplo: 123456SP. Tente novamente.");
        }
    }

    public static int lerInteiroPositivo(String mensagem, Scanner scanner) {
        int numero;
        while (true) {
            try {
                System.out.print(mensagem);
                numero = Integer.parseInt(scanner.nextLine());
                if (numero >= 0) {
                    return numero;
                }
                System.out.println("Erro: O número não pode ser negativo. Tente novamente.");
            } catch (NumberFormatException e) {
                System.out.println("Erro: Entrada inválida. Por favor, digite um número inteiro. Tente novamente.");
            }
        }
    }
    
    public static double lerDoublePositivo(String mensagem, Scanner scanner) {
        double numero;
        while (true) {
            try {
                System.out.print(mensagem);
                numero = Double.parseDouble(scanner.nextLine().replace(",", "."));
                if (numero >= 0) {
                    return numero;
                }
                System.out.println("Erro: O número não pode ser negativo. Tente novamente.");
            } catch (NumberFormatException e) {
                System.out.println("Erro: Entrada inválida. Por favor, digite um número. Tente novamente.");
            }
        }
    }

    public static LocalDateTime lerDataHora(String mensagem, Scanner scanner, DateTimeFormatter formatter) {
        LocalDateTime dataHora;
        while (true) {
            try {
                System.out.print(mensagem);
                dataHora = LocalDateTime.parse(scanner.nextLine(), formatter);
                return dataHora;
            } catch (DateTimeParseException e) {
                System.out.println("Erro: Formato de data e hora inválido. Use o formato dd/MM/yyyy HH:mm. Tente novamente.");
            }
        }
    }
    
    public static LocalDate lerData(String mensagem, Scanner scanner, DateTimeFormatter formatter) {
        LocalDate data;
        while (true) {
            try {
                System.out.print(mensagem);
                data = LocalDate.parse(scanner.nextLine(), formatter);
                return data;
            } catch (DateTimeParseException e) {
                System.out.println("Erro: Formato de data inválido. Use o formato dd/MM/yyyy. Tente novamente.");
            }
        }
    }
}