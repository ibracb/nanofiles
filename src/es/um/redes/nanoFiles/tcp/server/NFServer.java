package es.um.redes.nanoFiles.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

public class NFServer implements Runnable {

	public static final int PORT = 0;
	private ServerSocket serverSocket = null;

	public NFServer() throws IOException {
		/*
		 * TODO: (Boletín SocketsTCP) Crear una direción de socket a partir del puerto
		 * especificado (PORT)
		 */
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(PORT));
		System.out.println("NFServer server is running on: " + serverSocket.getInetAddress() +":"+ serverSocket.getLocalPort());
		/*
		 * TODO: (Boletín SocketsTCP) Crear un socket servidor y ligarlo a la dirección
		 * de socket anterior
		 */
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación.
	 * 
	 */
	public void test() {
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"[fileServerTestMode] Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("[fileServerTestMode] NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}

		while (true) {
			/*
			 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
			 * otros peers que soliciten descargar ficheros.
			 */
			try {
				Socket clientSocket = serverSocket.accept();
			/*
			 * TODO: (Boletín SocketsTCP) Tras aceptar la conexión con un peer cliente, la
			 * comunicación con dicho cliente para servir los ficheros solicitados se debe
			 * implementar en el método serveFilesToClient, al cual hay que pasarle el
			 * socket devuelto por accept.
			 * 				 
			 */	
				serveFilesToClient(clientSocket);
			}catch(IOException e){
				System.err.println("Error while accepting client connection: " + e.getMessage());
				break;
			}
		}
	}

	/**
	 * Método que ejecuta el hilo principal del servidor en segundo plano, esperando
	 * conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
		 * otros peers que soliciten descargar ficheros
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Al establecerse la conexión con un peer, la
		 * comunicación con dicho cliente se hace en el método
		 * serveFilesToClient(socket), al cual hay que pasarle el socket devuelto por
		 * accept
		 */
		/*
		 * TODO: (Boletín TCPConcurrente) Crear un hilo nuevo de la clase
		 * NFServerThread, que llevará a cabo la comunicación con el cliente que se
		 * acaba de conectar, mientras este hilo vuelve a quedar a la escucha de
		 * conexiones de nuevos clientes (para soportar múltiples clientes). Si este
		 * hilo es el que se encarga de atender al cliente conectado, no podremos tener
		 * más de un cliente conectado a este servidor.
		 */
		
		while (true) {
            try {
                // Esperar conexiones de clientes
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress()+":"+clientSocket.getPort());

                // Crear un nuevo hilo para manejar la comunicación con el cliente
                NFServerThread serverThread = new NFServerThread(clientSocket);
                serverThread.start();  // Iniciar el hilo para la comunicación con el cliente

            } catch (IOException e) {
                System.err.println("Error while accepting client connection: " + e.getMessage());
                break;  // Si ocurre un error, salir del bucle de escucha
            }
        }
	}
	/*
	 * TODO: (Boletín SocketsTCP) Añadir métodos a esta clase para: 1) Arrancar el
	 * servidor en un hilo nuevo que se ejecutará en segundo plano 2) Detener el
	 * servidor (stopserver) 3) Obtener el puerto de escucha del servidor etc.
	 */
	
	//Método para arrancar el servidor en un hilo nuevo
	public void startServer() {
        Thread serverThread = new Thread(this);  
        serverThread.start();  
    }
	
	// Método para detener el servidor
    public void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();  
            System.out.println("Server stopped.");
        }
    }
    
    // Método para obtener el puerto asociado al socket del servidor
    public int getPort() {
		return serverSocket.getLocalPort();
	}
    
    

	/**
	 * Método de clase que implementa el extremo del servidor del protocolo de
	 * transferencia de ficheros entre pares.
	 * 
	 * @param socket El socket para la comunicación con un cliente que desea
	 *               descargar ficheros.
	 */
	public static void serveFilesToClient(Socket socket) throws IOException {
	    System.out.println("Serving files to client: " + socket.getInetAddress()+":"+socket.getPort());
	    try (DataInputStream dis = new DataInputStream(socket.getInputStream());
	         DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

	        File sharedDir = new File(NanoFiles.sharedDirname);
	        if (!sharedDir.exists() || !sharedDir.canRead()) {
	            System.err.println("Error: Shared directory is not accessible. Check permissions.");
	            new PeerMessage(PeerMessageOps.OPCODE_ERROR_MESSAGE).writeMessageToOutputStream(dos);
	            return;
	        }

	        while (true) {
	            PeerMessage msg = PeerMessage.readMessageFromInputStream(dis);

	            switch (msg.getOpcode()) {
	                case PeerMessageOps.OPCODE_FILE_INFO_REQUEST: {
	                    String requestedSubstring = msg.getFileName();
	                    File matchedFile = null;

	                    // Search for a file whose name contains the substring
	                    File[] files = sharedDir.listFiles();
	                    if (files != null) {
	                        for (File file : files) {
	                            if (file.getName().contains(requestedSubstring)) {
	                                matchedFile = file;
	                                break;
	                            }
	                        }
	                    }

	                    if (matchedFile == null) {
	                        new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND).writeMessageToOutputStream(dos);
	                        break;
	                    }

	                    int size = (int) matchedFile.length();
	                    String hash = FileDigest.computeFileChecksumString(matchedFile.getAbsolutePath());
	                    PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_INFO_RESPONSE, matchedFile.getName(), size, hash);
	                    response.writeMessageToOutputStream(dos);
	                    break;
	                }

	                case PeerMessageOps.OPCODE_GET_CHUNK: {
	                    long offset = msg.getFileOffset();
	                    int chunkSize = msg.getChunkSize();
	                    String fileName = msg.getFileName();

	                    File chunkFile = new File(sharedDir, fileName);

	                    if (!chunkFile.exists() || !chunkFile.canRead()) {
	                        new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND).writeMessageToOutputStream(dos);
	                        break;
	                    }

	                    try (RandomAccessFile raf = new RandomAccessFile(chunkFile, "rw")) {
	                        raf.seek(offset);
	                        byte[] buffer = new byte[chunkSize];
	                        int readBytes = raf.read(buffer);

	                        if (readBytes > 0) {
	                            byte[] chunkData = Arrays.copyOf(buffer, readBytes);
	                            PeerMessage chunkMsg = new PeerMessage(PeerMessageOps.OPCODE_SEND_CHUNK, offset, readBytes, chunkData);
	                            chunkMsg.writeMessageToOutputStream(dos);
	                        } else {
	                            new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_COMPLETE).writeMessageToOutputStream(dos);
	                        }
	                    } catch (IOException e) {
	                        System.err.println("Error reading file chunk: " + e.getMessage());
	                        new PeerMessage(PeerMessageOps.OPCODE_ERROR_MESSAGE).writeMessageToOutputStream(dos);
	                    }
	                    break;
	                 }

	                default:
	                    new PeerMessage(PeerMessageOps.OPCODE_ERROR_MESSAGE).writeMessageToOutputStream(dos);
	                    break;
	            }
	        }
	    } catch (IOException e) {
	        System.err.println("Error while serving client: " + e.getMessage());
	    } finally {
	        try {
	            socket.close();
	        } catch (IOException e) {
	            System.err.println("Error while closing client socket: " + e.getMessage());
	        }
	    }
	}
}




