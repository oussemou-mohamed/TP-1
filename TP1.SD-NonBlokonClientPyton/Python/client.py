import socket
import threading
listen=True;
def listen_to_response(socket):
    while listen:
       data = socket.recv(1024).decode() # receive response
       print('Received from server: ' + data.strip()) # show in terminal stript(por fair trim?
def start_client():
    host = "127.0.0.1" # Host name
    port = 4444 # socket server port number
    client_socket = socket.socket() # instantiate
    client_socket.connect((host, port)) # connect to the server
    thread=threading.Thread(target=listen_to_response,args=(client_socket,))
    thread.start()
    request=""
    while request.lower().strip() != 'exit':
        request = input(" -> ")

        client_socket.send(bytes(request + "\n", "utf-8")) # send message
        #client_socket.sendall(bytes(request + "\n", "utf-8"))
    client_socket.close() # close the connection
    listen=False
if __name__ == '__main__':
    start_client()