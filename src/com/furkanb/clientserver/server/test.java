/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.furkanb.clientserver.server;

/**
 *
 * @author furkanb
 */
public class test {
   public static void main(String[] args) {
      String path = "/home/furkanb/test/asd/fqfq";
      
      String t = path.substring(0,path.lastIndexOf("/"));
      System.out.println(t);
   }
}
