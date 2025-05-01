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
	public static final String OPERATION_FILELIST_EMPTY = "filelist_empty";
	public static final String OPERATION_REGISTER_DENIED = "register-denied";
	public static final String OPERATION_REGISTER_OK = "register_ok";
	public static final String OPERATION_DOWNLOAD = "download";
	public static final String OPERATION_SERVERS_SHARING_FILE = "servers_sharing_file";
	public static final String OPERATION_SERVERS_SHARING_FILE_OK = "servers_sharing_file_ok";
	public static final String OPERATION_SERVERS_SHARING_FILE_EMPTY = "servers_sharing_file_empty";
	public static final String OPERATION_UNREGISTER = "unregister";
	public static final String OPERATION_UNREGISTER_OK = "unregister_ok";
	public static final String OPERATION_UNREGISTER_DENIED = "unregister_denied";
	
}
