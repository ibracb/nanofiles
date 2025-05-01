package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */
	private Map<InetSocketAddress, FileInfo[]> publishedFiles;
	public static Map<InetSocketAddress, FileInfo[]> registeredServers;
	
	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar el atributo socket: Crear un socket
		 * UDP ligado al puerto especificado por el argumento directoryPort en la
		 * máquina local,
		 */
		socket = new DatagramSocket(DIRECTORY_PORT);
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar atributos que mantienen el estado del
		 * servidor de directorio: ficheros, etc.)
		 */
		publishedFiles = new HashMap<InetSocketAddress, FileInfo[]>();
		registeredServers = new HashMap<InetSocketAddress, FileInfo[]>();
	
		if (NanoFiles.testModeUDP) {
			if (socket == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}


	@SuppressWarnings("unused")
	public DatagramPacket receiveDatagram() throws IOException {
		DatagramPacket datagramReceivedFromClient = null;
		boolean datagramReceived = false;
		while (!datagramReceived) {
			/*
			 * TODO: (Boletín SocketsUDP) Crear un búfer para recibir datagramas y un
			 * datagrama asociado al búfer (datagramReceivedFromClient)
			 */
			/*
			 * TODO: (Boletín SocketsUDP) Recibimos a través del socket un datagrama
			 */
			byte[] bufferReception = new byte[DirMessage.PACKET_MAX_SIZE];
			datagramReceivedFromClient = new DatagramPacket(bufferReception, bufferReception.length);
			socket.receive(datagramReceivedFromClient);
			
			if (datagramReceivedFromClient == null) {
				System.err.println("[testMode] NFDirectoryServer.receiveDatagram: code not yet fully functional.\n"
						+ "Check that all TODOs have been correctly addressed!");
				System.exit(-1);
			} else {
				// Vemos si el mensaje debe ser ignorado (simulación de un canal no confiable)
				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println(
							"Directory ignored datagram from " + datagramReceivedFromClient.getSocketAddress());
				} else {
					datagramReceived = true;
					System.out
							.println("Directory received datagram from " + datagramReceivedFromClient.getSocketAddress()
									+ " of size " + datagramReceivedFromClient.getLength() + " bytes.");
				}
			}

		}

		return datagramReceivedFromClient;
	}

	public void runTest() throws IOException {

		System.out.println("[testMode] Directory starting...");

		System.out.println("[testMode] Attempting to receive 'ping' message...");
		DatagramPacket rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);

		System.out.println("[testMode] Attempting to receive 'ping&PROTOCOL_ID' message...");
		rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);
	}

	private void sendResponseTestMode(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín SocketsUDP) Construir un String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración.
		 */

		/*
		 * TODO: (Boletín SocketsUDP) Después, usar la cadena para comprobar que su
		 * valor es "ping"; en ese caso, enviar como respuesta un datagrama con la
		 * cadena "pingok". Si el mensaje recibido no es "ping", se informa del error y
		 * se envía "invalid" como respuesta.
		 */

		/*
		 * TODO: (Boletín Estructura-NanoFiles) Ampliar el código para que, en el caso
		 * de que la cadena recibida no sea exactamente "ping", comprobar si comienza
		 * por "ping&" (es del tipo "ping&PROTOCOL_ID", donde PROTOCOL_ID será el
		 * identificador del protocolo diseñado por el grupo de prácticas (ver
		 * NanoFiles.PROTOCOL_ID). Se debe extraer el "protocol_id" de la cadena
		 * recibida y comprobar que su valor coincide con el de NanoFiles.PROTOCOL_ID,
		 * en cuyo caso se responderá con "welcome" (en otro caso, "denied").
		 */

		String messageFromClient = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("Data received: " + messageFromClient);
		
		
		byte[] requestData;
		DatagramPacket packetToClient;
		
		if(messageFromClient.equals("ping")) {
			requestData = "pingok".getBytes();
		}
		else if(messageFromClient.startsWith("ping&")) {
			
			// Si el mensaje comienza con "ping&", comprobamos el protocolId
	        String protocolId = messageFromClient.substring(5);  // Extraemos el protocolId tras "ping&"
	        
	        if (protocolId.equals(NanoFiles.PROTOCOL_ID)) {
	            // Si el protocolId coincide, respondemos con "welcome"
	            requestData = "welcome".getBytes();
	        } else {
	            // Si el protocolo no coincide, respondemos con "denied"
	            requestData = "denied".getBytes();
	        }
		}
		else {
			System.err.println("Message received is not a ping");
			requestData = "invalid".getBytes();
		}
		InetSocketAddress clientAddress = (InetSocketAddress) pkt.getSocketAddress(); 
		packetToClient = new DatagramPacket(requestData, requestData.length, clientAddress);
		socket.send(packetToClient);
	}

	public void run() throws IOException {

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio
			DatagramPacket rcvDatagram = receiveDatagram();

			sendResponse(rcvDatagram);

		}
	}
	
	private void writeRequestFromClient(String operation, DatagramPacket datagram) {
		System.out.println("Received " + operation + " request from " + datagram.getSocketAddress());
	}
	
	private void writeResponseToClient(String operation, DatagramPacket datagram, DirMessage message) {
		System.out.println("Sent " + operation + " response to " + datagram.getSocketAddress());
		System.out.println(message.toString());
	}
	
	private void sendResponse(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín MensajesASCII) Construir String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración. Después, usar la cadena para construir un objeto
		 * DirMessage que contenga en sus atributos los valores del mensaje. A partir de
		 * este objeto, se podrá obtener los valores de los campos del mensaje mediante
		 * métodos "getter" para procesar el mensaje y consultar/modificar el estado del
		 * servidor.
		 */
		byte [] pktdata = pkt.getData();
		String pktstr = new String(pktdata, 0, pkt.getLength());
		System.out.println(pktstr);
		DirMessage dirpkt = DirMessage.fromString(pktstr);


		/*
		 * TODO: Una vez construido un objeto DirMessage con el contenido del datagrama
		 * recibido, obtener el tipo de operación solicitada por el mensaje y actuar en
		 * consecuencia, enviando uno u otro tipo de mensaje en respuesta.
		 */
		String operation = dirpkt.getOperation(); // TODO: Cambiar!
		
		/*
		 * TODO: (Boletín MensajesASCII) Construir un objeto DirMessage (msgToSend) con
		 * la respuesta a enviar al cliente, en función del tipo de mensaje recibido,
		 * leyendo/modificando según sea necesario el "estado" guardado en el servidor
		 * de directorio (atributos files, etc.). Los atributos del objeto DirMessage
		 * contendrán los valores adecuados para los diferentes campos del mensaje a
		 * enviar como respuesta (operation, etc.)
		 */
		DirMessage msgToSend=null;


		switch (operation) {
		case DirMessageOps.OPERATION_PING: {
			
			writeRequestFromClient(operation, pkt);
			
			if(dirpkt.getProtocolId().equals(NanoFiles.PROTOCOL_ID)) {
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_WELCOME);
				System.out.println("* Client uses compatible protocol " + NanoFiles.PROTOCOL_ID);
			}
			else {
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_DENIED);
				System.out.println("* Client doesn't use compatible protocol " + NanoFiles.PROTOCOL_ID);
			}
			
			/*
			 * TODO: (Boletín MensajesASCII) Comprobamos si el protocolId del mensaje del
			 * cliente coincide con el nuestro.
			 */
			/*
			 * TODO: (Boletín MensajesASCII) Construimos un mensaje de respuesta que indique
			 * el éxito/fracaso del ping (compatible, incompatible), y lo devolvemos como
			 * resultado del método.
			 */
			/*
			 * TODO: (Boletín MensajesASCII) Imprimimos por pantalla el resultado de
			 * procesar la petición recibida (éxito o fracaso) con los datos relevantes, a
			 * modo de depuración en el servidor
			 */
			writeResponseToClient(operation, pkt, msgToSend);

			break;
		}
		case DirMessageOps.OPERATION_REGISTER: {
			
			writeRequestFromClient(operation, pkt);
			
		    InetSocketAddress address = (InetSocketAddress) pkt.getSocketAddress();
		    int serverPort = dirpkt.getPort(); // Obtén el puerto del mensaje
		    FileInfo[] files = dirpkt.getFileList();
		    if (files == null || files.length == 0 || serverPort <= 0) {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_REGISTER_DENIED);
		        System.out.println("* No files to serve");
		    } else {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_REGISTER_OK);
		        InetSocketAddress serverAddress = new InetSocketAddress(address.getAddress(), serverPort);
		        registeredServers.put(serverAddress, files);
		        publishedFiles.put(serverAddress, files);
		        System.out.println("* Client " + pkt.getSocketAddress() + " serving " + files.length + " files on address " + serverAddress);
		    }
		    writeResponseToClient(operation, pkt, msgToSend);
		    break;
		}
		
		
		case DirMessageOps.OPERATION_FILELIST: {
		    
			writeRequestFromClient(operation, pkt);
			
			if (publishedFiles.isEmpty()) {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_FILELIST_EMPTY);
		        System.out.println("* No files in the directory filelist");
		    } else {
		        List<FileInfo> allFiles = new ArrayList<>();
		        for (FileInfo[] fileArray : publishedFiles.values()) {
		            if (fileArray != null) {
		                allFiles.addAll(Arrays.asList(fileArray)); // Agregar todos los archivos
		            }
		        }
		        FileInfo[] fileList = allFiles.toArray(new FileInfo[0]);
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_FILELIST_OK);
		        msgToSend.setFileList(fileList);
		        System.out.println("* Files available in the directory filelist");
		    }
			writeResponseToClient(operation, pkt, msgToSend);
		    break;
		}
		case DirMessageOps.OPERATION_SERVERS_SHARING_FILE: {
		    
			writeRequestFromClient(operation, pkt);
			
			String fileSubstring = dirpkt.getFileNameSubstring();
		    if (fileSubstring == null || fileSubstring.isEmpty()) {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_SERVERS_SHARING_FILE_EMPTY);
		        System.err.println("Invalid filename received in servers_sharing_file operation.");
		        break;
		    }

		    Set<InetSocketAddress> servers = new HashSet<>();
		    for (Map.Entry<InetSocketAddress, FileInfo[]> entry : registeredServers.entrySet()) {
		        for (FileInfo file : entry.getValue()) {
		            if (file.getFileName().contains(fileSubstring)) {
		                servers.add(entry.getKey());
		                break; // No need to check more files for this server
		            }
		        }
		    }

		    if (servers.isEmpty()) {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_SERVERS_SHARING_FILE_EMPTY);
		        System.out.println("* No servers sharing any file with that filename_substring");
		    } else {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_SERVERS_SHARING_FILE_OK, servers);
		        System.out.println("* There are " + registeredServers.size() + " servers sharing file with the filename_substring " + fileSubstring);
		    }
		    writeResponseToClient(operation, pkt, msgToSend);
		    break;
		    
		}
		case DirMessageOps.OPERATION_UNREGISTER: {
			writeRequestFromClient(operation, pkt);

			InetSocketAddress clientAddress = (InetSocketAddress) pkt.getSocketAddress();
			int serverPort = dirpkt.getPort(); // Obtener el puerto del mensaje
			InetSocketAddress serverAddress = new InetSocketAddress(clientAddress.getAddress(), serverPort);

			if (registeredServers.containsKey(serverAddress)) {
				registeredServers.remove(serverAddress);
				publishedFiles.remove(serverAddress);
				msgToSend = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_OK);
				System.out.println("* Server " + serverAddress + " unregistered successfully.");
			} else {
				msgToSend = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_DENIED);
				System.err.println("* Server " + serverAddress + " not found in the directory.");
			}

			writeResponseToClient(operation, pkt, msgToSend);
			break;
		}
		
		default:
			System.err.println("Unexpected message operation: \"" + operation + "\"");
			System.exit(-1);
		}

		/*
		 * TODO: (Boletín MensajesASCII) Convertir a String el objeto DirMessage
		 * (msgToSend) con el mensaje de respuesta a enviar, extraer los bytes en que se
		 * codifica el string y finalmente enviarlos en un datagrama
		 */
		String msgToString = msgToSend.toString();
		byte[] bytesMsg = msgToString.getBytes();
		InetSocketAddress clientAddress = (InetSocketAddress) pkt.getSocketAddress(); 
		DatagramPacket packetToClient = new DatagramPacket(bytesMsg, bytesMsg.length, clientAddress);
		socket.send(packetToClient);
	}
}
