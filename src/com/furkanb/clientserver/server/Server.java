/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.furkanb.clientserver.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author furkanb
 */
public class Server extends Thread {

   public static String OpSystem;

   static final int SOCKET_NUMBER = 10006;

   //Dosya ana dizini
   String directory = System.getProperty("user.home");

   Socket socket;

   PrintWriter printWriterOut;
   BufferedReader bufferedReaderIn;

   public Server(Socket socket) {
      this.socket = socket;
      start();
   }

   public static void main(String[] args) {
      OpSystem = setOpSystem();
      ServerSocket serverSocket = null;

      try {
         serverSocket = new ServerSocket(SOCKET_NUMBER);

         while (true) {
            System.out.println("Waiting clients");
            Server server = new Server(serverSocket.accept());
         }

      } catch (IOException e) {
         System.out.println(e);
         System.exit(1);
      } finally {
         try {
            serverSocket.close();

         } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
         }
      }
   }

   public void run() {
      System.out.println("Client connected");

      try {
         //Cliente giden çıktı
         printWriterOut = new PrintWriter(socket.getOutputStream(),
                 true);

         //Clientten gelen komut
         bufferedReaderIn = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));

         String input;

         while ((input = bufferedReaderIn.readLine()) != null) {
            if (input.equals("exit")) {
               disconnect();
               break;
            }

            String cikti = execCommand(input);
            printWriterOut.println(cikti);

         }
      } catch (IOException e) {
         System.out.println(e);
         System.exit(1);
      }
   }

   //Komutu çalıştır
   public String execCommand(String command) {

      StringBuilder sb = new StringBuilder();
      String line = "";
      Process process = null;

      try {
         //Komut kontrolü
         String echo = control(command);

         if (echo.equals("")) {

            //işletim sistemi kontrolü
            if (OpSystem.equals("windows")) {
               String[] cmd = new String[3];

               cmd[0] = "cmd.exe";
               cmd[1] = "/C";
               cmd[2] = command;
               process = new ProcessBuilder(cmd)
                       .redirectErrorStream(true)
                       .directory(new File(directory))
                       .start();

            } else {
               
               String[] lin = new String[] {"/bin/sh", "-c", command};
               process = new ProcessBuilder(lin)
                       .redirectErrorStream(true)
                       .directory(new File(directory))
                       .start();
            }

            process.waitFor();

            BufferedReader infoReader
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));

            BufferedReader errorReader
                    = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            while ((line = errorReader.readLine()) != null) {
               sb.append(line).append("  ");
            }
            while ((line = infoReader.readLine()) != null) {
               sb.append(line).append("  ");
            }
         } else {
            sb.append(echo);
         }
      } catch (IOException | InterruptedException e) {
         sb.append("Command not found ");
      }

      return sb.toString();

   }

   //Komut cd ile başlıyorsa ve / içermiyorsa önceki pathe ekleme yap
   // / içermiyorsa direk değiştir
   // pwd komutu gelirse bulunduğu pathi yazdır
   public String control(String command) {
      String operator = "";

      switch (OpSystem) {

         //Windows ise \\
         //Linux ise / 
         //operatore atama yap
         case "windows":
            operator = "\\";
            break;
         case "linux":
            operator = "/";
            break;
      }

      //komut cd .. ise operatore göre son dizini sil pathi seç
      //komut / veya c:\\ ile başlıyorsa tamamen pathi değiştir
      //değilse önceki pathe ekleme yap
      if (command.startsWith("cd")) {

         if (command.startsWith("..", 3)) {
            directory = directory.substring(0, directory.lastIndexOf(operator));
            return "Path changed : " + directory;

         }

         if (command.startsWith("/", 3) || command.startsWith("C:\\", 3) || command.startsWith("c:\\", 3)) {
            directory = command.substring(3, command.length());
         } else {
            directory += operator + command.substring(3, command.length());
         }

         return "Path changed : " + directory;
      } else if (command.startsWith("pwd")) {
         //pwd direk pathi yaz
         return "PWD : " + directory;
      } else {
         return "";
      }

   }

   //İşletim sistemini seç
   public static String setOpSystem() {

      String op = System.getProperty("os.name").toLowerCase();

      if (op.contains("win")) {
         return "windows";
      } else if (op.contains("linux")) {
         return "linux";
      } else {
         return "";
      }
   }

   public void disconnect() {
      try {
         printWriterOut.close();
         bufferedReaderIn.close();
         socket.close();
      } catch (Exception e) {
         System.out.println(e);
      }

   }

}
