import socket
import threading


class Client:
    def _init_(self):
        self.client_socket = None

    def listen_to_response(self, client_socket):
        while True:
            data = self.client_socket.recv(1024).decode()
            print("Received from  :" + data) if data != "" else exit("Bye")

    def client_program(self):
        host, port = "localhost", 2023
        self.client_socket = socket.socket()
        self.client_socket.connect((host, port))
        thread = threading.Thread(target=self.listen_to_response, args=(self.client_socket,)).start()
        request = ""
        while request.lower().strip() != "bye":
            try:
                request = input("-> ")
                self.client_socket.sendall(bytes(request + "\n", "utf-8"))
            except: exit("server is closed....!!")
        self.client_socket.close()


if _name_ == '_main_':
    Client().client_program()