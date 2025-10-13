package com.agibank.sisagi.email;

import com.agibank.sisagi.model.Cliente;
import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.model.DebitoAutomatico;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void enviarEmailBoasVindas(Cliente cliente) {
        final String subject = "Bem-vindo ao Agi!";
        try{
            Context context = new Context();
            context.setVariable("nome", cliente.getNomeCompleto());

            String htmlBody = templateEngine.process("emails/bem-vindo", context);

            enviarEmailHtml(cliente.getEmail(), subject, htmlBody);
            log.info("Email de boas-vindas enviado com sucesso para {}!", cliente.getEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar email de boas-vindas para {}: {}", cliente.getEmail(), e.getMessage());
        }
    }

    @Async
    public void notificarDeposito(Cliente titular, BigDecimal valor, LocalDateTime dataHora) {
        final String subject = "Depósito Recebido em sua Conta";
        try {
            Context context = new Context();
            context.setVariable("nome", titular.getNomeCompleto());
            context.setVariable("valor", valor);
            context.setVariable("dataHora", dataHora);
            String htmlBody = templateEngine.process("emails/deposito-realizado", context);
            enviarEmailHtml(titular.getEmail(), subject, htmlBody);
            log.info("Email de notificação de depósito enviado para {}", titular.getEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de depósito para {}: {}", titular.getEmail(), e.getMessage());
        }
    }

    @Async
    public void notificarAgendamentoDebito(Cliente titular, DebitoAutomatico agendamento) {
        final String subject = "Débito Automático Agendado";
        try {
            Context context = new Context();
            context.setVariable("nome", titular.getNomeCompleto());
            context.setVariable("contaDestino", agendamento.getTipoServico());
            context.setVariable("frequencia", agendamento.getFrequencia().toString());
            context.setVariable("diaDoMes", agendamento.getDiaAgendado());
            String htmlBody = templateEngine.process("emails/debito-automatico-agendado", context);
            enviarEmailHtml(titular.getEmail(), subject, htmlBody);
            log.info("Email de confirmação de agendamento enviado para {}", titular.getEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de agendamento para {}: {}", titular.getEmail(), e.getMessage());
        }
    }

    @Async
    public void notificarCancelamentoConta(Cliente titular, String numeroConta) {
        final String subject = "Confirmação de Encerramento de Conta";
        try {
            Context context = new Context();
            context.setVariable("nome", titular.getNomeCompleto());
            context.setVariable("numeroConta", numeroConta);
            String htmlBody = templateEngine.process("emails/conta-cancelada", context);
            enviarEmailHtml(titular.getEmail(), subject, htmlBody);
            log.info("Email de encerramento de conta enviado para {}", titular.getEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de encerramento para {}: {}", titular.getEmail(), e.getMessage());
        }
    }

    @Async
    public void notificarSaque(Cliente titular, BigDecimal valor, LocalDateTime dataHora) {
        final String subject = "Comprovante de Saque";
        try {
            Context context = new Context();
            context.setVariable("nome", titular.getNomeCompleto());
            context.setVariable("valor", valor);
            context.setVariable("dataHora", dataHora);
            String htmlBody = templateEngine.process("emails/saque-realizado", context);
            enviarEmailHtml(titular.getEmail(), subject, htmlBody);
            log.info("Email de notificação de saque enviado para {}", titular.getEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de saque para {}: {}", titular.getEmail(), e.getMessage());
        }
    }

    @Async
    public void notificarTransferenciaEnviada(Conta contaDestino, Cliente clienteOrigem, BigDecimal valor){
        final String subject = "Comprovante de Transferência Enviada!";
        try{
            Context context = new Context();
            context.setVariable("nome", clienteOrigem.getNomeCompleto());
            context.setVariable("valor", valor);
            context.setVariable("contaDestino", contaDestino.getNumeroConta());
            context.setVariable("data", LocalDateTime.now());

            String htmlBody = templateEngine.process("emails/transferencia-enviada", context);

            enviarEmailHtml(clienteOrigem.getEmail(), subject, htmlBody);
            log.info("Emails de notificação de transferência enviados com sucesso para {}!", clienteOrigem.getEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar email de notificação de transferência enviada: {}", e.getMessage());
        }
    }

    @Async
    public void notificarTransferenciaRecebida(Conta contaOrigem, Cliente clienteDestino, BigDecimal valor){
        final String subject = "Transação recebida!";
        try{
            Context context = new Context();
            context.setVariable("nome", clienteDestino.getNomeCompleto());
            context.setVariable("valor", valor);
            context.setVariable("contaOrigem", contaOrigem.getNumeroConta());
            context.setVariable("data", LocalDateTime.now());

            String htmlBody = templateEngine.process("emails/transferencia-recebida", context);

            enviarEmailHtml(clienteDestino.getEmail(), subject, htmlBody);
            log.info("Emails de notificação de recebimento de transferência enviados com sucesso para {}!", clienteDestino.getEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar email de notificação de transferência recebida: {}", e.getMessage());
        }
    }

    private void enviarEmailHtml(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // O 'true' indica que o corpo é HTML
        mailSender.send(mimeMessage);
    }
}
