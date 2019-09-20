package core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import enums.MessageType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import main.TicTacToe;

public class Network {

	private TicTacToe main;
	private Alert error;

	private ServerSocket server;
	private Socket conn;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public Network(TicTacToe main) {
		this.main = main;
		server = null;
		conn = null;
		in = null;
		out = null;
	}

	public boolean isHost() {
		return server != null;
	}

	public void send(Message msg) throws IOException {
		if (msg.isEmpty())
			return;
		out.writeObject(msg);
		out.flush();
	}

	public Message read() {
		Message msg = null;
		try {
			msg = (Message) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			msg = new Message(MessageType.DISCONNECT);
		}
		return msg;
	}

	public void error(String errmsg) {
		error = new Alert(AlertType.ERROR);
		error.setTitle("Networking Error");
		error.setHeaderText(errmsg);
		error.setContentText("Returning to main menu");
		error.showAndWait();
		main.restart();
	}

	public void closeAll() {
		try {
			if (in != null) {
				conn.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeConnection();
		closeServer();
	}

	public void openStreams() throws IOException {
		out = new ObjectOutputStream(new BufferedOutputStream(conn.getOutputStream()));
		out.flush();
		in = new ObjectInputStream(new BufferedInputStream(conn.getInputStream()));
	}

	public void openServer() throws IOException {
		if (server == null)
			server = new ServerSocket(TicTacToe.PORT, 2);
	}

	public void accept() throws IOException, SocketException {
		conn = server.accept();
	}

	public Socket connectToServer(String ip) throws IOException {
		conn = new Socket(ip, TicTacToe.PORT);
		return conn;
	}

	public boolean checkConnection() {
		return (conn == null) ? false : !conn.isClosed() && conn.isConnected();
	}

	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeServer() {
		try {
			if (server != null) {
				server.close();
				server = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
