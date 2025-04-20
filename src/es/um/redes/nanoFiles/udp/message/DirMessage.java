package es.um.redes.nanoFiles.udp.message;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	/*
	 * TODO: (Boletín MensajesASCII) Definir de manera simbólica los nombres de
	 * todos los campos que pueden aparecer en los mensajes de este protocolo
	 * (formato campo:valor)
	 */
	private static final String FIELDNAME_PROTOCOL = "protocol";
	private static final String FIELDNAME_FILENAME = "filename";
	private static final String FIELDNAME_FILENAMESUBSTRING = "filenamesubstring";
	private static final String FIELDNAME_FILESIZE = "filesize";
	private static final String FIELDNAME_FILEHASH = "filehash";
	private static final String FIELDNAME_ADDRESS = "adress";
	private static final String FIELDNAME_SERVERPORT = "serverport";

	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/**
	 * Identificador de protocolo usado, para comprobar compatibilidad del directorio.
	 */
	private String protocolId;
	/*
	 * TODO: (Boletín MensajesASCII) Crear un atributo correspondiente a cada uno de
	 * los campos de los diferentes mensajes de este protocolo.
	 */
	private String fileName;
	private String fileSize;
	private String fileHash;
	private String fileNameSubstring;
	private List<InetSocketAddress> serverSocketAddresses;
	private FileInfo[] fileList;
	private int serverPort;
	private String files;
	
	
	
	/*
	 * TODO: (Boletín MensajesASCII) Crear diferentes constructores adecuados para
	 * construir mensajes de diferentes tipos con sus correspondientes argumentos
	 * (campos del mensaje)
	 */
	public DirMessage(String operation) {
		this.operation = operation;
		this.fileList = new FileInfo[0];
	}
	
	public DirMessage(String operation, String protocolId) {
		this.operation = operation;
		this.protocolId = protocolId;
		this.fileList = new FileInfo[0];

	}
	
	public DirMessage(String operation,int serverPort,FileInfo[] fileList) {
		this.operation = operation;
		this.serverPort = serverPort;
	    this.fileList = fileList;
	}
	public DirMessage(String operation, String fileName, String fileSize, String fileHash, int serverPort, FileInfo[] fileList) {
		 this.operation = operation;
	     this.fileName = fileName;
	     this.fileSize = fileSize;
	     this.fileHash = fileHash;
	     this.serverPort = serverPort;
	     this.fileList = fileList;
	}
	public DirMessage(String operation, String fileName, String fileSize, String fileHash) {
        this.operation = operation;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileHash = fileHash;
		this.fileList = new FileInfo[0];
    }
	
	public DirMessage(String operation, Set<InetSocketAddress> servers) {
	    this.operation = operation;
	    this.serverSocketAddresses = new ArrayList<>(servers);
	}
	
	public String getOperation() {
		return operation;
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear métodos getter y setter para obtener los
	 * valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public void setProtocolID(String protocolIdent) {
		if (!operation.equals(DirMessageOps.OPERATION_PING)) {
			throw new RuntimeException(
					"DirMessage: setProtocolId called for message of unexpected type (" + operation + ")");
		}
		this.protocolId = protocolIdent;
	}

	public String getProtocolId() {

		return protocolId;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		/*if(!operation.equals(DirMessageOps)) {
			throw new RuntimeException(
					"DirMessage: setFileName called for message of unexpected type (" + operation + ")");
		}*/
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		/*if(!operation.equals(DirMessageOps)) {
			throw new RuntimeException(
					"DirMessage: setSize called for message of unexpected type (" + operation + ")");
		}*/
		this.fileSize = fileSize;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		/*if(!operation.equals(DirMessageOps)) {
			throw new RuntimeException(
					"DirMessage: setHash called for message of unexpected type (" + operation + ")");
		}*/
		this.fileHash = fileHash;
	}

	public List<InetSocketAddress> getServerSocketAddresses() {
		return serverSocketAddresses;
	}
	public void addServerSocketAddress(InetSocketAddress serverSocketAddress) {
		this.serverSocketAddresses.add(serverSocketAddress);
	}

	public void setPort(int port) {
	    this.serverPort = port;
	}

	public int getPort() {
	    return serverPort;
	}

	public void setFileList(FileInfo[] fileList) {
	    if (fileList == null) {
	        this.fileList = new FileInfo[0]; // Asegurar que no sea null
	    } else {
	        this.fileList = Arrays.copyOf(fileList, fileList.length); // Copia profunda de la lista
	    }
	}

	public FileInfo[] getFileList() {
	    return Arrays.copyOf(fileList, fileList.length);
	}
	
	public String getFiles() {
		return files;
	}
	
	public void setFiles(String files) {
		assert (operation.equals(DirMessageOps.OPERATION_FILELIST_OK));
		this.files = files;
	}
	public String getFileNameSubstring() {
		return fileNameSubstring;
	}
	public void setFileNameSubstring(String fileNameSubstring) {
		this.fileNameSubstring = fileNameSubstring;
	}
	
	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: (Boletín MensajesASCII) Usar un bucle para parsear el mensaje línea a
		 * línea, extrayendo para cada línea el nombre del campo y el valor, usando el
		 * delimitador DELIMITER, y guardarlo en variables locales.
		 */

		// System.out.println("DirMessage read from socket:");
		// System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		// Local variables to save data during parsing
		DirMessage m = null;
		FileInfo currentFile = null; // Inicializa como null
		List<FileInfo> f = new ArrayList<>();
		List<InetSocketAddress> serverAddresses = new ArrayList<>();

		for (String line : lines) {
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
				case FIELDNAME_OPERATION: {
					assert (m == null);
					m = new DirMessage(value);
					break;
				}
				case FIELDNAME_PROTOCOL: {
					m.setProtocolID(value);
	                break;
	            }
				case FIELDNAME_SERVERPORT:{
					int aux=Integer.parseInt(value);
					m.setPort(aux);
					break;
				}
				case FIELDNAME_FILENAME: {
					if (currentFile == null) {
						currentFile = new FileInfo();
					}
					currentFile.setFileName(value);
					break;
				}
				case FIELDNAME_FILESIZE: {
					if (currentFile != null) {
						currentFile.setFileSize(Long.parseLong(value));
					}
					break;
				}
				case FIELDNAME_FILEHASH: {
					if (currentFile != null) {
						currentFile.setFileHash(value);
						f.add(currentFile); // Agrega el archivo completo a la lista
						currentFile = null; // Reinicia para el siguiente archivo
					}
					break;
				}
				case FIELDNAME_FILENAMESUBSTRING:{
					m.setFileNameSubstring(value);
					break;
				}
				case FIELDNAME_ADDRESS: {
					// Corregir el parsing de direcciones
					String[] parts = value.split(":");
					if (parts.length == 2) {
						String host = parts[0];
						try {
							int port = Integer.parseInt(parts[1]);
							InetSocketAddress address = new InetSocketAddress(host, port);
							serverAddresses.add(address);
						} catch (NumberFormatException e) {
							System.err.println("Invalid port format in address: " + value);
						}
					} else {
						System.err.println("Invalid address format: " + value);
					}
					break;
				}
				
				default:
					System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
					System.err.println("Message was:\n" + message);
					System.exit(-1);
			}
		}
		
		m.setFileList(f.toArray(new FileInfo[0])); // Establece la lista de archivos
		if (m != null) {
			m.serverSocketAddresses = serverAddresses;
		}
		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION).append(DELIMITER).append(operation).append(END_LINE);
		/*
		 * TODO: (Boletín MensajesASCII) En función de la operación del mensaje, crear
		 * una cadena la operación y concatenar el resto de campos necesarios usando los
		 * valores de los atributos del objeto.
		 */
		switch(operation) {
		case DirMessageOps.OPERATION_PING:
			sb.append(FIELDNAME_PROTOCOL + DELIMITER + protocolId + END_LINE);
			break;
		
		case DirMessageOps.OPERATION_DOWNLOAD:
			sb.append(FIELDNAME_FILENAME).append(DELIMITER).append(fileName).append(END_LINE);
			sb.append(FIELDNAME_FILESIZE).append(DELIMITER).append(fileSize).append(END_LINE);
			sb.append(FIELDNAME_FILEHASH).append(DELIMITER).append(fileHash).append(END_LINE);
			break;
		case DirMessageOps.OPERATION_REGISTER:
			sb.append(FIELDNAME_SERVERPORT).append(DELIMITER).append(serverPort).append(END_LINE);
		    for (FileInfo file : fileList) {
		        sb.append(FIELDNAME_FILENAME).append(DELIMITER).append(file.getFileName()).append(END_LINE);
		        sb.append(FIELDNAME_FILESIZE).append(DELIMITER).append(file.getFileSize()).append(END_LINE);
		        sb.append(FIELDNAME_FILEHASH).append(DELIMITER).append(file.getFileHash()).append(END_LINE);
		    }
		    break;
		case DirMessageOps.OPERATION_FILELIST_OK:
			for (FileInfo file : fileList) {
			     sb.append(FIELDNAME_FILENAME).append(DELIMITER).append(file.getFileName()).append(END_LINE);
			     sb.append(FIELDNAME_FILESIZE).append(DELIMITER).append(file.getFileSize()).append(END_LINE);
			     sb.append(FIELDNAME_FILEHASH).append(DELIMITER).append(file.getFileHash()).append(END_LINE);
			}
			
			break;
		case DirMessageOps.OPERATION_SERVERS_SHARING_FILE:
			sb.append(FIELDNAME_FILENAMESUBSTRING).append(DELIMITER).append(fileName).append(END_LINE);
			break;
		case DirMessageOps.OPERATION_SERVERS_SHARING_FILE_OK:
			for(InetSocketAddress address : serverSocketAddresses) {
				sb.append(FIELDNAME_ADDRESS).append(DELIMITER).append(address).append(END_LINE);
			}
			break;
		}
			
		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}

}
