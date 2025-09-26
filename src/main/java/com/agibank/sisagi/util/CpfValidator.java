package com.agibank.sisagi.util;

public class CpfValidator {
    public static boolean isValid(String cpf){
        String cpfLimpo =  cpf.replaceAll("[^0-9]","");

        if(cpfLimpo.length() != 11){
            return false;
        }
        if (cpfLimpo.matches(("(\\d)\\1{10}"))){
            return false;
        }
        try {
            // --- CÁLCULO DO PRIMEIRO DÍGITO VERIFICADOR ---
            int soma = 0;
            // Multiplica os 9 primeiros dígitos pela sequência decrescente de 10 a 2.
            for (int i = 0; i < 9; i++) {
                soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (10 - i);
            }

            int resto = soma % 11;
            int digitoVerificador1 = (resto < 2) ? 0 : (11 - resto);

            // Compara o dígito calculado com o 10º dígito do CPF.
            if (digitoVerificador1 != Integer.parseInt(String.valueOf(cpfLimpo.charAt(9)))) {
                return false;
            }

            // --- CÁLCULO DO SEGUNDO DÍGITO VERIFICADOR ---
            soma = 0;
            // Multiplica os 10 primeiros dígitos pela sequência decrescente de 11 a 2.
            for (int i = 0; i < 10; i++) {
                soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (11 - i);
            }

            resto = soma % 11;
            int digitoVerificador2 = (resto < 2) ? 0 : (11 - resto);

            // Compara o dígito calculado com o 11º dígito do CPF.
            if (digitoVerificador2 != Integer.parseInt(String.valueOf(cpfLimpo.charAt(10)))) {
                return false;
            }

        } catch (NumberFormatException e) {
            // Se o CPF não for composto por números, é inválido.
            return false;
        }

        // Se passou por todas as verificações, o CPF é válido.
        return true;
    }
}
