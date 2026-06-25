package main.Util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class ServicioCorreo {

    // Configuración del servidor emisor
    private static final String REMITENTE = "8bitsouladmin@gmail.com";
    private static final String PASSWORD_APLICACION = "dwaj alog swed jnbc  "; // Tu contraseña de aplicación de 16 caracteres de Google

    public static boolean enviarContrasena(String correoDestino, String username, String contrasenaRaw) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, PASSWORD_APLICACION);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            
            message.setSubject("⚡ [BIT SOUL // CORE] SYSTEM DE RECOVERY DE CREDENCIALES ⚡");

            String cuerpoHtml = "<html><body style='background-color:#060c18; color:#dcf5ff; font-family:monospace; padding:20px; border:2px solid #00f0ff;'>"
                    + "<h2 style='color:#00f0ff;'>[BIT SOUL // PROTOCOLO DE RESTAURACIÓN]</h2>"
                    + "<p>Saludos, Operador <strong>" + username + "</strong>.</p>"
                    + "<p>Se ha solicitado acceso al Núcleo de Red desde una terminal externa mediante este correo electrónico.</p>"
                    + "<div style='background-color:#0c121e; border:1px solid #f205cb; padding:15px; margin:20px 0;'>"
                    + "<span style='color:#f205cb;'>LLAVE DE ACCESO RECONSTITUIDA:</span><br>"
                    + "<span style='font-size:18px; letter-spacing:2px; color:#ffffff;'><strong>" + contrasenaRaw + "</strong></span>"
                    + "</div>"
                    + "<p style='color:#f205cb; font-size:11px;'>Si tú no solicitaste este código, cambia inmediatamente tus parámetros de red.</p>"
                    + "</body></html>";

            message.setContent(cuerpoHtml, "text/html; charset=utf-8");

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            System.err.println("[ERROR EN RECONEXIÓN SMTP]: " + e.getMessage());
            return false;
        }
    }

    public static boolean enviarCodigoRecuperacion(String correoDestino, String username, String codigo) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, PASSWORD_APLICACION);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            message.setSubject("⚡ [BIT SOUL // CORE] CODIGO DE RECUPERACION ⚡");

            String cuerpoHtml = "<html><body style='background-color:#060c18; color:#dcf5ff; font-family:monospace; padding:20px; border:2px solid #00f0ff;'>"
                    + "<h2 style='color:#00f0ff;'>[BIT SOUL // PROTOCOLO DE RECUPERACION]</h2>"
                    + "<p>Operador <strong>" + username + "</strong>, se ha generado un código temporal de acceso.</p>"
                    + "<div style='background-color:#0c121e; border:1px solid #f205cb; padding:15px; margin:20px 0;'>"
                    + "<span style='color:#f205cb;'>CÓDIGO DE RESTABLECIMIENTO:</span><br>"
                    + "<span style='font-size:18px; letter-spacing:2px; color:#ffffff;'><strong>" + codigo + "</strong></span>"
                    + "</div>"
                    + "<p style='color:#f205cb; font-size:11px;'>Este código expira en 15 minutos.</p>"
                    + "</body></html>";

            message.setContent(cuerpoHtml, "text/html; charset=utf-8");
            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            System.err.println("[ERROR EN RECONEXIÓN SMTP]: " + e.getMessage());
            return false;
        }
    }
}
