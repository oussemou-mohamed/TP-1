package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Serveur {
    public static void main(String[] args) throws IOException {
        Selector selector=Selector.open();
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("0.0.0.0",4444));//0.0.0.0 admet les connection pour ninport quelle adres ip
        ssc.register(selector,SelectionKey.OP_ACCEPT);                 //localhoste admmed les connection de locale

       // int validOps =ssc.validOps();  //retourn le type de levenement posible ou pue ecouter
        // ssc.register(selector,validOps);

        //*****event loup******//
        while (true){
            int channelCount = selector.select();//regard esque il a des evenement ;retourne le nomber de chanels
            if(channelCount==0) continue;//revien fair select
            Set<SelectionKey> selectionKeys = selector.selectedKeys();//retourne les demande envoyer
            Iterator<SelectionKey> iterator = selectionKeys.iterator();//cree un iterator
            while (iterator.hasNext()){//parcourir
                SelectionKey sk = iterator.next();//retourn un object de type selectionKey
                if (sk.isAcceptable()){ handleAccept(selector,sk);}//demand de connection
                else if(sk.isReadable()){ handleRead(selector,sk); }// de type requet
                iterator.remove();
            }}}
    //****************************************************************************************//
    private static void handleAccept(Selector selector,SelectionKey sk) throws IOException {
        ServerSocketChannel ssc= (ServerSocketChannel) sk.channel();//recuperer SocketChannel a partire de sk(selecteurKey)
        SocketChannel sc = ssc.accept();//accepter la connection
        
        sc.configureBlocking(false);//configurer socketChannel en mode non blokon
        sc.register(selector,SelectionKey.OP_READ);//enregistre dans le selecteur comme ca qon il a un client qui lenvoi en requet il va notifier ,OP_READ:atend des evenement de type rede
        System.out.println("#############################");
        System.out.println(String.format("New Connection from : %s", sc.getRemoteAddress()));
        System.out.println(String.format("Thread:%s",Thread.currentThread().getName()));
    }
    //****************************************************************************************//
    private static void handleRead(Selector selector,SelectionKey sk) throws IOException {
        SocketChannel sc = (SocketChannel) sk.channel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);//**//allouwer un bafer de capaciter 1024 OCTER
        int numRead = sc.read(byteBuffer);//retourn le nomber docter envoier
        if (numRead == -1) {//evenement de type ce deconnecter en recoi -1
            Socket socket = sc.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println(String.format("Connection closed by client: %s" , remoteAddr));
            sc.close();
            sk.cancel();
           // return;
        }
        String request=new String(byteBuffer.array()).trim();//convertire le messege en string .enyor les espas
        System.out.println("********************");
        System.out.println(String.format("Nouveau message : %s",request));
        System.out.println(String.format("Client : %s",sc.getRemoteAddress().toString()));
        System.out.println(String.format("Thread: %s",Thread.currentThread().getName()));

        //String response=" Bonjour : "+request;
        String response=new StringBuffer(request).reverse().toString().toUpperCase()+"\n";//renvercer et majuscul
        ByteBuffer byteBufferResponse=ByteBuffer.allocate(1024);//creé un outre byte bafer
        byteBufferResponse.put(response.getBytes());//metre la reponse dans le befer
        byteBufferResponse.flip();//bascuter le beffer en mode lectur
        sc.write(byteBufferResponse);


    }
    }
