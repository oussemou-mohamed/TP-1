import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Node;
public class Controler implements Initializable {
    @FXML
    private Button sendb;
    @FXML
    private TextField message;
    @FXML
    private HBox HB;
    @FXML
    private ListView listView;
    static Socket socket;
    String request;
    String id;
    private ObservableList<String> listModel = FXCollections.observableArrayList();
    public void envoyer(){
        String messageText = message.getText();
        try {
            OutputStream os=socket.getOutputStream();
            PrintWriter pw=new PrintWriter(os,true);
            pw.println(messageText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HB.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                sendb.fire();
            }
        });
        try {
            socket=new Socket("localhost",1234);
            InputStream is=socket.getInputStream();          //
            InputStreamReader isr=new InputStreamReader(is); //
            BufferedReader br=new BufferedReader(isr);       //
            String request1=br.readLine();
            String[] split = request1.split("=>");
            String s = split[1];
            id=s;
            listModel.addAll(request1);
            listView.setItems(listModel);
            new Thread(()-> {              //en utilise utilise untred pour ne pas bloker laplication ici il faire doter tache
                try {
                    while ((request=br.readLine())!=null){
                        //System.out.println(request);
                        //load();
                        Platform.runLater(()->{
                            listModel.add(request);
                        });
                    }
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void load(){
        listModel.add(request);
    }
}
