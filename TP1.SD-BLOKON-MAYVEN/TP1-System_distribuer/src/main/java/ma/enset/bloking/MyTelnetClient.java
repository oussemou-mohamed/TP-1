package ma.enset.bloking;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
public class MyTelnetClient {
    public static void main(String[] args) {
        try {
            Socket socket=new Socket("localhost",1234);
            InputStream is=socket.getInputStream();          //
            InputStreamReader isr=new InputStreamReader(is); //
            BufferedReader br=new BufferedReader(isr);       //
            OutputStream os=socket.getOutputStream();
            PrintWriter pw=new PrintWriter(os,true);
            /*new Thread(new Runnable() {//Runnable un interface qui a un seul fonction il sappelle interface fonctionnel
                @Override
                public void run() {
                }
            }).start();*/
            //avec les expresion lamda on peu ecrire
            new Thread(()-> {              //en utilise utilise untred pour ne pas bloker laplication ici il faire doter tache
                try {
                String request;
                while ((request=br.readLine())!=null){
                        System.out.println(request);
                    }
                }catch (IOException e) {
                        throw new RuntimeException(e);
                }
            }).start();
            //en separe la partie qui ecout a la patri qui envoi pour na pas avoiver un blocage de laplication on scanner.nextlini et en va perder les message qui arrive .en pose les message ariver dan un tred
            Scanner scanner=new Scanner(System.in);
            while (true){
                String request=scanner.nextLine();
                pw.println(request);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
