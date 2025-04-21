package es.um.redes.nanoFiles.tcp.message;

import java.util.Map;
import java.util.TreeMap;

public class PeerMessageOps {

	/*
	 * TODO: (Boletín MensajesBinarios) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con un par
	 * servidor de ficheros (valores posibles del campo "operation").
	 */
	public static final byte OPCODE_INVALID_CODE = 0x00;//código inválido
	public static final byte OPCODE_FILE_NOT_FOUND = 0x01;//archivo no encontrado
	public static final byte OPCODE_GET_CHUNK = 0x02;//obtener chunk
	public static final byte OPCODE_SEND_CHUNK = 0x03;//enviar chunk
	public static final byte OPCODE_UPLOAD_FILE = 0x04;//petición de subida de archivo
	public static final byte OPCODE_UPLOAD_ACK = 0x05; //confirmación
	public static final byte OPCODE_FILE_INFO_REQUEST = 0x06;//Cliente pide metadatos del archivo
	public static final byte OPCODE_FILE_INFO_RESPONSE = 0x07;//Servidor responde con esa info 
	public static final byte OPCODE_DOWNLOAD_COMPLETE = 0x08;//Cliente indica que ya recibió todo.
	public static final byte OPCODE_ERROR_MESSAGE = 0x09;//Para errores como archivo corrupto.
	

	/*
	 * TODO: (Boletín MensajesBinarios) Definir constantes con nuevos opcodes de
	 * mensajes definidos anteriormente, añadirlos al array "valid_opcodes" y añadir
	 * su representación textual a "valid_operations_str" EN EL MISMO ORDEN.
	 */
	private static final Byte[] _valid_opcodes = { OPCODE_INVALID_CODE, 
		    OPCODE_FILE_NOT_FOUND, 
		    OPCODE_GET_CHUNK, 
		    OPCODE_SEND_CHUNK, 
		    OPCODE_UPLOAD_FILE, 
		    OPCODE_UPLOAD_ACK,
		    OPCODE_FILE_INFO_REQUEST,
		    OPCODE_FILE_INFO_RESPONSE,
		    OPCODE_DOWNLOAD_COMPLETE,
		    OPCODE_ERROR_MESSAGE
	};
	private static final String[] _valid_operations_str = {  "INVALID_OPCODE", 
		    "FILE_NOT_FOUND", 
		    "GET_CHUNK", 
		    "SEND_CHUNK", 
		    "UPLOAD_FILE", 
		    "UPLOAD_ACK",
		    "FILE_INFO_REQUEST",
		    "FILE_INFO_RESPONSE",
		    "DOWNLOAD_COMPLETE",
		    "ERROR_MESSAGE"
	};

	private static Map<String, Byte> _operation_to_opcode;
	private static Map<Byte, String> _opcode_to_operation;

	static {
		_operation_to_opcode = new TreeMap<>();
		_opcode_to_operation = new TreeMap<>();
		for (int i = 0; i < _valid_operations_str.length; ++i) {
			_operation_to_opcode.put(_valid_operations_str[i].toLowerCase(), _valid_opcodes[i]);
			_opcode_to_operation.put(_valid_opcodes[i], _valid_operations_str[i]);
		}
	}

	/**
	 * Transforma una cadena en el opcode correspondiente
	 */
	protected static byte operationToOpcode(String opStr) {
		return _operation_to_opcode.getOrDefault(opStr.toLowerCase(), OPCODE_INVALID_CODE);
	}

	/**
	 * Transforma un opcode en la cadena correspondiente
	 */
	public static String opcodeToOperation(byte opcode) {
		return _opcode_to_operation.getOrDefault(opcode, null);
	}
}
