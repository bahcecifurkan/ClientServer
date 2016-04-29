/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.furkanb.clientserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author furkanb
 */
public class Client {

   public static void main(String[] args) throws IOException {

      Scanner sc = new Scanner(System.in);

      System.out.println(" Hostname : ");
      String hostName = sc.nextLine();

      System.out.println(" Port : ");
      int port = sc.nextInt();

      System.out.println("Connection " + hostName + ", Port " + port);

      Socket socket = new Socket(hostName, port);

      PrintWriter printWriterOut = new PrintWriter(socket.getOutputStream(), true);

      //serverdan dönen bilgi
      BufferedReader bufferedReaderIn = new BufferedReader(new InputStreamReader(
              socket.getInputStream()));

      //clienttan giden komut
      BufferedReader bufferedReaderCli = new BufferedReader(new InputStreamReader(System.in));

      System.out.print("command : ");

      String userInput;

      while ((userInput = bufferedReaderCli.readLine()) != null) {
         
         printWriterOut.println(userInput);

         System.out.println("$ : " + bufferedReaderIn.readLine());
         System.out.print("command : ");
      }

      //disconnect
      printWriterOut.close();
      bufferedReaderIn.close();
      bufferedReaderCli.close();
      socket.close();
   }

}
