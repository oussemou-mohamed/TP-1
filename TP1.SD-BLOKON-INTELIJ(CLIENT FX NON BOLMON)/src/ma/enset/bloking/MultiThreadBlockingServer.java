package ma.enset.bloking;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
                       //*************************************************//
public class MultiThreadBlockingServer extends Thread{
                        //***********les atrribue*******************//
    private List<Conversation>conversations=new ArrayList<>();
    int clientsCount;
                       //*************fonction principale***********//
    public static void main(String[] args) {
        new MultiThreadBlockingServer().start();//demare un thread on nai pa bloker pare le service de conection
        System.out.println("boujour");
    }                                              //le thred va socuper de connection client  ,et le serveur faire un autre tache
    @Override                                          //il apelle la method run

                     //***********fonction run*******************//
    public void run() {
        System.out.println("Th server is started using port=1234");
        try {
            ServerSocket serverSocket=new ServerSocket(1234);
            while (true){
                Socket socket=serverSocket.accept();
                ++clientsCount;
                //en fais pas ici inputstrim.read il vais bloker la boucle qui provoce queles outre ne peuv pas se conecter
                //donc pour chache client connecter il faux dimarer un nouvaux tred pour dimare un thred enva creer un objet dun classe que je vai creer
                Conversation conversation = new Conversation(socket, clientsCount);
                conversations.add(conversation);
                conversation.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //*********** Class Conversation*******************//
  class Conversation extends Thread{                          //classe interne
                   //***********les atrribue*******************//
        private Socket socket;
        private int clientId;
        //***********Constructeur*******************//
        public Conversation(Socket socket,int clientId){
            this.socket=socket;
            this.clientId=clientId;
        }
        @Override
        public void run() {
            try{
                InputStream is=socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);//lire un caracter strime de traitement
                BufferedReader br=new BufferedReader(isr);
                OutputStream os=socket.getOutputStream();//envovoier
                PrintWriter pw=new PrintWriter(os,true);
                //true:achaque foi con ecrit otomatiquement la recet va envoier ou client
                //cas de false  pw.println("Rsponse"); //le message nai pas envoier il atend la methode pw.fluch()
                String ip=socket.getRemoteSocketAddress().toString();
                System.out.println("new Client connection  ,ID => "+clientId+"  IP= "+ip);
                pw.println("welcom your ID is =>"+clientId);
                String request;
                while ((request=br.readLine())!=null){//le serveur reste ecout les reponse
                    System.out.println(request);
                    System.out.println("new Request => IP ="+clientId+ip+" Request: "+request);
                    List<Integer> clientsTo=new ArrayList<>();
                    String message;
                    if(request.contains("=>")){
                        String[] items = request.split("=>");
                        String clients=items[0];
                        System.out.println(clients);
                        message=items[1];
                        if(clients.contains(",")){
                            String[] clientIds = clients.split(",");
                            for(String id:clientIds){
                                clientsTo.add(Integer.parseInt(id));
                            }
                        }else {
                            clientsTo.add(Integer.parseInt(clients));

                        }
                    }else {
                        clientsTo=conversations.stream().map(c->c.clientId).collect(Collectors.toList());//?????
                        message=request;
                    }
                    brodcastMessage(message,this,clientsTo);
                }
            }catch (Exception e){
                e.getStackTrace();
            }
        }
    }
    public void brodcastMessage(String message,Conversation from,List<Integer> clients){
        for (Conversation conversation:conversations){
            if(conversation!=from && clients.contains(conversation.clientId)){
                Socket socket=conversation.socket;
                OutputStream os= null;
                try {
                    os = socket.getOutputStream();
                    PrintWriter pw=new PrintWriter(os,true);
                    pw.println(message);
                } catch (IOException e) {
                   e.printStackTrace();
                }

            }

        }
    }
}
