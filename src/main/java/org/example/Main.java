package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);


        System.out.print("Introduce el asunto del correo: ");
        String asunto = scanner.nextLine();

        System.out.print("Introduce el contenido del correo: ");
        String contenido = scanner.nextLine();


        System.out.println("\n¿Qué sistema operativo tienes?");
        System.out.println("1. Windows");
        System.out.println("2. Ubuntu");
        System.out.println("3. Mac");
        System.out.print("Elige una opción (1/2/3): ");
        int choice = scanner.nextInt();
        scanner.nextLine();


        String fileName = System.getProperty("user.home") + "/Documents/NotaEjercicioProceso.txt";
        File noteFile = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(noteFile))) {
            writer.write("Asunto: \n" + asunto + "\n");
            writer.write("Contenido: \n" + contenido);
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo: " + e.getMessage());
            return;
        }

        // Abrir la nota según el sistema operativo
        ProcessBuilder processBuilder = null;
        switch (choice) {
            case 1:
                processBuilder = new ProcessBuilder("notepad.exe", noteFile.getAbsolutePath());
                break;
            case 2:
                processBuilder = new ProcessBuilder("gedit", noteFile.getAbsolutePath());
                break;
            case 3:
                processBuilder = new ProcessBuilder("open", "-a", "textedit", noteFile.getAbsolutePath());
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        try {
            processBuilder.start();
            System.out.println("Se ha abierto la nota en la carpeta de Documentos");
        } catch (IOException e) {
            System.out.println("Error al abrir la nota: " + e.getMessage());
        }

        // Enviar el contenido por correo
        System.out.print("\n¿Deseas enviar esta nota por correo? (sí/no): ");
        String sendEmail = scanner.nextLine();

        if (sendEmail.equalsIgnoreCase("si") || sendEmail.equalsIgnoreCase("Si") || sendEmail.equalsIgnoreCase("sí")) {
            System.out.print("Introduce el correo del destinatario: ");
            String recipient = scanner.nextLine();

            System.out.print("Introduce tu correo del remitente: ");
            String sender = scanner.nextLine();

            System.out.print("Introduce tu contraseña del correo: ");
            String password = scanner.nextLine();

            sendEmail(asunto, contenido, recipient, sender, password);
        }

        scanner.close();
    }

    private static void sendEmail(String subject, String body, String recipient, String sender, String password) {
        // Configurar propiedades para el servidor de correo
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Crear sesión con autenticación
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            // Enviar el mensaje
            Transport.send(message);
            System.out.println("Correo enviado correctamente a " + recipient);

        } catch (MessagingException e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
