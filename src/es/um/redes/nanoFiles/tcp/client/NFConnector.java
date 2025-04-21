package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.application.NanoFiles;
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

		if (serverAddr == null || serverAddr.getAddress() == null || serverAddr.getPort() <= 0) {
			System.err.println("* Invalid server address: " + serverAddr);
			return;
		}

		try {
			System.out.println("Attempting to connect to server: " + serverAddr);
			socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
			System.out.println("Connected to server: " + serverAddr);
		} catch (IOException e) {
			System.err.println("Error: Unable to establish connection - " + e.getMessage());
			throw e;
		}

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

	        System.out.println("Server at " + serverAddr.getAddress() + ":" + serverAddr.getPort() + " found matching filename with hash: " + expectedHash);

	        // Ensure the file is created in the shared directory
	        File localFile = new File(NanoFiles.sharedDirname, localFileName);
	        try (FileOutputStream fos = new FileOutputStream(localFile)) {
	            long offset = 0;
	            int totalChunksDownloaded = 0;
	            int totalBytesDownloaded = 0;

	            while (!downloaded) {
	                // Request a chunk from the server
	                PeerMessage chunkRequest = new PeerMessage(PeerMessageOps.OPCODE_GET_CHUNK, offset, 4096);
	                chunkRequest.writeMessageToOutputStream(dos);

	                PeerMessage chunkResponse = PeerMessage.readMessageFromInputStream(dis);

	                if (chunkResponse.getOpcode() == PeerMessageOps.OPCODE_SEND_CHUNK) {
	                    fos.write(chunkResponse.getChunkData());
	                    offset += chunkResponse.getChunkSize();
	                    totalChunksDownloaded++;
	                    totalBytesDownloaded += chunkResponse.getChunkSize();
	                } else if (chunkResponse.getOpcode() == PeerMessageOps.OPCODE_DOWNLOAD_COMPLETE) {
	                    downloaded = true;
	                    System.out.println("Download complete.");
	                } else if (chunkResponse.getOpcode() == PeerMessageOps.OPCODE_FILE_NOT_FOUND) {
	                    System.err.println("File not found on server.");
	                    return false;
	                } else {
	                    System.err.println("Unexpected response while downloading chunk.");
	                    return false;
	                }
	            }
	            System.out.println("\t" + totalBytesDownloaded + " bytes (" + totalChunksDownloaded + " chunks) from server at " + serverAddr.getAddress() + ":" + serverAddr.getPort());
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
