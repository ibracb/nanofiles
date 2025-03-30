package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: (Boletín MensajesASCII) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con el
	 * directorio (valores posibles del campo "operation").
	 */
	public static final String OPERATION_INVALID = "invalid";
	public static final String OPERATION_PING = "ping";
	public static final String OPERATION_PING_WELCOME = "ping_welcome";
	public static final String OPERATION_PING_DENIED = "ping_denied";
	public static final String OPERATION_REGISTER = "register";
	public static final String OPERATION_FILELIST = "filelist";
	public static final String OPERATION_FILELIST_OK = "filelist_ok";
	public static final String OPERATION_FILELIST_DENIED = "filelist_denied";
	public static final String OPERATION_SERVE = "serve";
	public static final String OPERATION_DOWNLOAD = "download";
	
}
