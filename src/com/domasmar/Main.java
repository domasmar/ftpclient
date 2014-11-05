package com.domasmar;

import com.domasmar.impl.commands.Commands;
import com.domasmar.impl.core.FtpClient;

import javax.swing.*;
import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        FtpClient client = new FtpClient();
        client.setIp("127.0.0.1");
        client.setPort(21);

        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!client.isConnected()) {
            System.out.println("Neprisijungta prie serverio");
        } else {
            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            while (running) {
                System.out.print("Įveskite komandą: ");
                String command = scanner.nextLine();
                command = command.trim().toUpperCase();
                Commands commandEnum = Commands.INVALID_COMMAND;
                try {
                    commandEnum = Commands.valueOf(command);
                } catch (Exception e) {
                    // Wrong command
                    // We will switch to defaul block
                }

                switch (commandEnum) {
                    case LOGIN:
                        if (client.isLoggedIn()) {
                            System.out.println("Jus jau prisijunge");
                        } else {
                            System.out.print("Vardas: ");
                            String username = scanner.nextLine();
                            System.out.print("Slaptažodis: ");
                            String password = scanner.nextLine();
                            if (client.login(username, password)) {
                                System.out.println("Prisijungti pavyko");
                            } else {
                                System.out.println("Prisijungti nepavyko");
                            }
                        }
                        break;
                    case CD:
                        System.out.print("Direktorija: ");
                        String dir = scanner.nextLine();
                        if (client.changeWorkingDir(dir.trim())) {
                            System.out.println("OK");
                        } else {
                            System.out.println("NOT OK");
                        }
                        break;
                    case LS:
                        String[] list = client.getCurrentDirList();
                        for (int i = 0; i < list.length; i++) {
                            System.out.println(list[i].trim());
                        }
                        break;
                    case DOWNLOAD:
                        String destination = "";
                        String filename;
                        final JFileChooser fc = new JFileChooser();
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            destination = file.getAbsoluteFile().toString();
                        } else {
                            System.out.println("Nepasirinktas joks failas");
                            break;
                        }
                        System.out.print("Įveskite failo pavadinimą: ");
                        filename = scanner.nextLine();
                        client.downloadFile(filename, destination);
                        break;
                    case UPLOAD:
                        break;
                    case QUIT:
                        running = false;
                        break;
                    default:
                        System.out.println("Neatpazinta komanda");
                }
            }
        }
        client.close();

    }
}
