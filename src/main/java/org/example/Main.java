package org.example;

import java.io.*;
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
            writer.write("Asunto:\n" + asunto + "\n");
            writer.write("Contenido:\n" + contenido);
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo: " + e.getMessage());
            return;
        }

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

        System.out.print("\n¿Deseas enviar esta nota por correo? (sí/no): ");
        String scannerEmail = scanner.nextLine();

        if (scannerEmail.equalsIgnoreCase("si") || scannerEmail.equalsIgnoreCase("sí")) {
            System.out.print("Introduce el correo del destinatario: ");
            String recipient = scanner.nextLine();

            System.out.print("Introduce tu correo del remitente: ");
            String sender = scanner.nextLine();

            System.out.print("Introduce tu contraseña del correo: ");
            String password = scanner.nextLine();

            //actulizar del archivo
            String[] actualizarContent = leerFileContent(noteFile);
            if (actualizarContent != null) {
                String asuntoActualizado = actualizarContent[0];
                String contenidoActualizado = actualizarContent[1];
                enviarEmail(asuntoActualizado, contenidoActualizado, recipient, sender, password);
            } else {
                System.out.println("No se pudo leer el archivo actualizado para enviarlo.");
            }
        } else{
            System.out.println("Proceso acabado sin enviar el correo.");
        }

        scanner.close();
    }

    //leer el contenido actualizado del archivo
    private static String[] leerFileContent(File file) {
        String asunto = "";
        StringBuilder contenido = new StringBuilder();
        boolean hayContenido = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = reader.readLine()) != null) {

                //leer la línea después de "Asunto:"
                if (linea.startsWith("Asunto:")) {
                    asunto = reader.readLine().trim();

                //leer la línea después de "Contenido:"
                } else if (linea.startsWith("Contenido:")) {
                    hayContenido = true;

                } else if (hayContenido) {
                    contenido.append(linea).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return null;
        }
        return new String[]{asunto, contenido.toString().trim()};
    }

    //enviar el correo
    private static void enviarEmail(String subject, String body, String recipient, String sender, String password) {
        //propiedades para el servidor del correo
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        //comprobar autenticación del correo
        Session sesion = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            //crear el mensaje
            Message message = new MimeMessage(sesion);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            //enviar el mensaje
            Transport.send(message);
            System.out.println("Correo enviado correctamente a " + recipient);

        } catch (MessagingException e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
