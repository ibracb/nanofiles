package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	private DataInputStream dis;
	private DataOutputStream dos;

	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO: (Boletín SocketsTCP) Se crea el socket a partir de la dirección del
		 * servidor (IP, puerto). La creación exitosa del socket significa que la
		 * conexión TCP ha sido establecida.
		 */
		socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
		/*
		 * TODO: (Boletín SocketsTCP) Se crean los DataInputStream/DataOutputStream a
		 * partir de los streams de entrada/salida del socket creado. Se usarán para
		 * enviar (dos) y recibir (dis) datos del servidor.
		 */
		dis = new DataInputStream(socket.getInputStream());
	    dos = new DataOutputStream(socket.getOutputStream());
	}

	public void test() {
		/*
		 * TODO: (Boletín SocketsTCP) Enviar entero cualquiera a través del socket y
		 * después recibir otro entero, comprobando que se trata del mismo valor.
		 */
		try {
	        int valueToSend = 12345; 
	        dos.writeInt(valueToSend); 
	        //dos.flush(); 

	        int receivedValue = dis.readInt(); 

	        if (valueToSend == receivedValue) {
	            System.out.println("Test passed: received the same integer " + receivedValue);
	        } else {
	            System.err.println("Test failed: sent " + valueToSend + ", received " + receivedValue);
	        }

	    } catch (IOException e) {
	        System.err.println("Test failed due to IO exception: " + e.getMessage());
	    }
	}

	public boolean downloadFile(String targetFileNameSubstring, String localFileName) throws IOException {
	    boolean downloaded = false;
	    String expectedHash = null;

	    try {
	        // Request file information based on the substring
	        PeerMessage request = new PeerMessage(PeerMessageOps.OPCODE_FILE_INFO_REQUEST, targetFileNameSubstring);
	        request.writeMessageToOutputStream(dos);

	        PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
	        if (response.getOpcode() == PeerMessageOps.OPCODE_FILE_NOT_FOUND) {
	            System.err.println("No file matches the provided substring: " + targetFileNameSubstring);
	            return false;
	        }

	        if (response.getOpcode() != PeerMessageOps.OPCODE_FILE_INFO_RESPONSE) {
	            System.err.println("Unexpected response from server.");
	            return false;
	        }

	        int fileSize = response.getFileSize();
	        expectedHash = response.getFileHash();
	        System.out.println("Downloading file: " + response.getFileName() + " (Size: " + fileSize + " bytes)");

	        try (FileOutputStream fos = new FileOutputStream(localFileName)) {
	            long offset = 0;
	            while (!downloaded) {
	                // Request a chunk from the server
	                PeerMessage chunkRequest = new PeerMessage(PeerMessageOps.OPCODE_GET_CHUNK, offset, 4096);
	                chunkRequest.writeMessageToOutputStream(dos);

	                PeerMessage chunkResponse = PeerMessage.readMessageFromInputStream(dis);

	                if (chunkResponse.getOpcode() == PeerMessageOps.OPCODE_SEND_CHUNK) {
	                    fos.write(chunkResponse.getChunkData());
	                    offset += chunkResponse.getChunkSize();
	                } else if (chunkResponse.getOpcode() == PeerMessageOps.OPCODE_DOWNLOAD_COMPLETE) {
	                    downloaded = true;
	                } else {
	                    System.err.println("Unexpected response while downloading chunk.");
	                    return false;
	                }
	            }
	        }
	        if (downloaded) {
	            String downloadedFileHash = FileDigest.computeFileChecksumString(localFileName);
	            if (!downloadedFileHash.equals(expectedHash)) {
	                System.err.println("File integrity check failed. Hash mismatch.");
	                return false;
	            }
	            System.out.println("File downloaded successfully: " + localFileName);
	        }

	    } catch (IOException e) {
	        System.err.println("Error during file download: " + e.getMessage());
	        return false;
	    }
		socket.close();
	    return downloaded;
	}

	public void close() throws IOException {
	    if (socket != null && !socket.isClosed()) {
	        socket.close();
	    }
	    if (dis != null) {
	        dis.close();
	    }
	    if (dos != null) {
	        dos.close();
	    }
	}

	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
