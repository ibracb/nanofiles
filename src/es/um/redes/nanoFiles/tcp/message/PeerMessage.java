package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PeerMessage {


	/*
	 * TODO: (Boletín MensajesBinarios) Añadir atributos u otros constructores
	 * específicos para crear mensajes con otros campos, según sea necesario
	 * 
	 */

	private byte opcode;
	private long fileOffset;   //byte de inicio
	private int chunkSize;   
	private byte[] chunkData; 
	private String fileName; 
	private int fileSize;
	


	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	
	public PeerMessage(byte op, long fileOffset, int chunkSize) {
	    if (op != PeerMessageOps.OPCODE_GET_CHUNK) {
	        throw new IllegalArgumentException("Opcode incorrecto para GET_CHUNK");
	    }
	    this.opcode = op;
	    this.fileOffset = fileOffset;
	    this.chunkSize = chunkSize;
	}
	
	public PeerMessage(byte op, long fileOffset, int chunkSize, byte[] chunkData) {
	    if (op != PeerMessageOps.OPCODE_SEND_CHUNK) {
	        throw new IllegalArgumentException("Opcode incorrecto para SEND_CHUNK");
	    }
	    this.opcode = op;
	    this.fileOffset = fileOffset;
	    this.chunkSize = chunkSize;
	    this.chunkData = chunkData;
	}
	
	public PeerMessage(byte op, String fileName) {
	    if (op != PeerMessageOps.OPCODE_UPLOAD_FILE) {
	        throw new IllegalArgumentException("Opcode incorrecto para UPLOAD_FILE");
	    }
	    this.opcode = op;
	    this.fileName = fileName;
	}

	/*
	 * TODO: (Boletín MensajesBinarios) Crear métodos getter y setter para obtener
	 * los valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public byte getOpcode() {
		return opcode;
	}

	//solo hay campo offset en get chunk y send chunk
	public long getFileOffset() {
	    if (opcode != PeerMessageOps.OPCODE_GET_CHUNK && opcode != PeerMessageOps.OPCODE_SEND_CHUNK) {
	        throw new IllegalStateException("Este mensaje no contiene fileOffset");
	    }
	    return fileOffset;
	}

	public void setFileOffset(long fileOffset) {
	    if (opcode != PeerMessageOps.OPCODE_GET_CHUNK && opcode != PeerMessageOps.OPCODE_SEND_CHUNK) {
	        throw new IllegalStateException("No se puede asignar fileOffset a este tipo de mensaje");
	    }
	    this.fileOffset = fileOffset;
	}
	
	//el tamaño de chunk solo afecta al getchunk y al sendchunk
	public int getChunkSize() {
	    if (opcode != PeerMessageOps.OPCODE_GET_CHUNK && opcode != PeerMessageOps.OPCODE_SEND_CHUNK) {
	        throw new IllegalStateException("Este mensaje no contiene chunkSize");
	    }
	    return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
	    if (opcode != PeerMessageOps.OPCODE_GET_CHUNK && opcode != PeerMessageOps.OPCODE_SEND_CHUNK) {
	        throw new IllegalStateException("No se puede asignar chunkSize a este tipo de mensaje");
	    }
	    this.chunkSize = chunkSize;
	}
	
	//los bytes del fragmento solo afectan al sendchunk que es el que los envía
	public byte[] getChunkData() {
	    if (opcode != PeerMessageOps.OPCODE_SEND_CHUNK) {
	        throw new IllegalStateException("Este mensaje no contiene chunkData");
	    }
	    return chunkData;
	}

	public void setChunkData(byte[] chunkData) {
	    if (opcode != PeerMessageOps.OPCODE_SEND_CHUNK) {
	        throw new IllegalStateException("No se puede asignar chunkData a este tipo de mensaje");
	    }
	    this.chunkData = chunkData;
	}
	
	//el nombre del archivo solo afecta en la subida del archivo
	public String getFileName() {
	    if (opcode != PeerMessageOps.OPCODE_UPLOAD_FILE) {
	        throw new IllegalStateException("Este mensaje no contiene fileName");
	    }
	    return fileName;
	}

	public void setFileName(String fileName) {
	    if (opcode != PeerMessageOps.OPCODE_UPLOAD_FILE) {
	        throw new IllegalStateException("No se puede asignar fileName a este tipo de mensaje");
	    }
	    this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}


	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: (Boletín MensajesBinarios) En función del tipo de mensaje, leer del
		 * socket a través del "dis" el resto de campos para ir extrayendo con los
		 * valores y establecer los atributos del un objeto DirMessage que contendrá
		 * toda la información del mensaje, y que será devuelto como resultado. NOTA:
		 * Usar dis.readFully para leer un array de bytes, dis.readInt para leer un
		 * entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		byte opcode = dis.readByte();
		switch (opcode) {
		
		case PeerMessageOps.OPCODE_FILE_NOT_FOUND:
            return new PeerMessage(opcode);

        case PeerMessageOps.OPCODE_GET_CHUNK:
            long fileOffset = dis.readLong();
            int chunkSize = dis.readInt();
            return new PeerMessage(opcode, fileOffset, chunkSize);

        case PeerMessageOps.OPCODE_SEND_CHUNK:
            fileOffset = dis.readLong();
            chunkSize = dis.readInt();
            byte[] chunkData = new byte[chunkSize];
            dis.readFully(chunkData);
            return new PeerMessage(opcode, fileOffset, chunkSize, chunkData);

        case PeerMessageOps.OPCODE_UPLOAD_FILE:
            int fileNameLength = dis.readShort();
            byte[] fileNameBytes = new byte[fileNameLength];
            dis.readFully(fileNameBytes);
            String fileName = new String(fileNameBytes, "UTF-8");
            return new PeerMessage(opcode, fileName);

        case PeerMessageOps.OPCODE_UPLOAD_ACK:
            return new PeerMessage(opcode);

		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO (Boletín MensajesBinarios): Escribir los bytes en los que se codifica el
		 * mensaje en el socket a través del "dos", teniendo en cuenta opcode del
		 * mensaje del que se trata y los campos relevantes en cada caso. NOTA: Usar
		 * dos.write para leer un array de bytes, dos.writeInt para escribir un entero,
		 * etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {
		
		case PeerMessageOps.OPCODE_GET_CHUNK:
            dos.writeLong(fileOffset);
            dos.writeInt(chunkSize);
            break;

        case PeerMessageOps.OPCODE_SEND_CHUNK:
            dos.writeLong(fileOffset);
            dos.writeInt(chunkSize);
            dos.write(chunkData);
            break;

        case PeerMessageOps.OPCODE_UPLOAD_FILE:
            byte[] fileNameBytes = fileName.getBytes();
            dos.writeShort(fileNameBytes.length);
            dos.write(fileNameBytes);
            break;
            
        case PeerMessageOps.OPCODE_UPLOAD_ACK:
            break;
            
		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}




}
