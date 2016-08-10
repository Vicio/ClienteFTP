import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileSystemView;


public class VentanaCliente extends JFrame implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2783686302289296600L;
	//el socket para la conexióm
	private Socket socket = null;
	//la ip del servidor
	private String IP;
	//el puerto
	private int Puerto;
	//el usuario y la contraseña
	private String Usuario;
	private String Password;
	//las dimensiones de la ventana
	private int Ancho;
	private int Alto;
	//el indicador de login
	private boolean Login;
	//la entrada de datos
	private DataInputStream entradaMensaje = null;
	//selector de archivo [no es visible y solo se usa para obtener un directorio local default]
	private JFileChooser selector;
	//lo mismo con el file system view
	private FileSystemView vista;
	//la lista de objetos del servidor
	private JList<Object> listaRemota;
	//el arreglo de los objetos del servidor
	ArrayList<String> dirsRemotos;
	//el arreglo para los objetos de la computadora local
	ArrayList<String> dirsLocales;
	//el archivo local a manipular
	private File archivoLocal;
	//el directorio actual de la computadora local
	private String dirLocal;
	//la lista de archivos locales
	private JList<Object> listaLocal;
	//los scrolls para las listas
	private JScrollPane panelScrollRemota;
	private JScrollPane panelScrollLocal;
	//el contenedor
	private Container contenedor;
	//el panel
	private JPanel panel;
	//botones
	private JButton botonSubirArchivo;
	private JButton botonDescargarArchivo;
	private JButton botonBorrarArchivo;
	private JButton botonCrearFolder;
	private JButton botonBorrarFolder;
	//inicialización de los componentes
	public void init()
	{
		//se inicializan los directorios y la lista del servidor remoto
		dirsRemotos = new ArrayList<String>();
		listaRemota = new JList<Object>(dirsRemotos.toArray());
		//se obtiene el directorio default del sistema operativo
		selector = new JFileChooser();
		vista = selector.getFileSystemView();
		archivoLocal = vista.getDefaultDirectory();
		dirLocal = archivoLocal.getAbsolutePath() + "\\";
		//se genera la lista de archivos dentro del directorio default
		dirsLocales = new ArrayList<String>();
		dirsLocales.add("DIR:..");
		for(int i = 0; i < archivoLocal.list().length; i++)
		{
			File temp = new File(archivoLocal.getAbsolutePath() + "\\" + archivoLocal.list()[i]);
			String tipoArchivo = "FILE";
			if(temp.isDirectory())
				tipoArchivo = "DIR";			
			dirsLocales.add(tipoArchivo + ":" + archivoLocal.list()[i]);
		}
		listaLocal = new JList<Object>(dirsLocales.toArray());
		//se inicializan los scrolls y se almacenan las listas dentro de ellos
		panelScrollRemota = new JScrollPane();
		panelScrollRemota.setViewportView(listaRemota);
		panelScrollLocal = new JScrollPane();
		panelScrollLocal.setViewportView(listaLocal);
		//se inicializa el contenedor
		contenedor = getContentPane();
		//se inicializa el panel y se asigna al contenedor
		panel = new JPanel();
		panel.setLayout(null);
		contenedor.add(panel);
		//se asignan las dimensiones de los scrolls y se agregan al panel
		panelScrollLocal.setBounds(Ancho/40, Alto/30, Ancho/2-Ancho/40, Alto/10*8);
		panelScrollRemota.setBounds(Ancho/2 + Ancho/20, Alto/30, Ancho/2-Ancho/40*3, Alto/10*8);
		panel.add(panelScrollRemota);
		panel.add(panelScrollLocal);
		//se crean los botones y se agregan al panel
		botonCrearFolder = new JButton("Crear folder");
		botonCrearFolder.setBounds(Ancho/40, Alto/10*8 + Alto/20, Ancho/6, Alto/20);
		panel.add(botonCrearFolder);
		botonBorrarFolder = new JButton("Borrar folder");
		botonBorrarFolder.setBounds(Ancho/40*2 + Ancho/6, Alto/10*8 + Alto/20, Ancho/6, Alto/20);
		panel.add(botonBorrarFolder);
		botonBorrarArchivo = new JButton("Borrar archivo");
		botonBorrarArchivo.setBounds(Ancho/40*3 + Ancho/6*2, Alto/10*8 + Alto/20, Ancho/6, Alto/20);
		panel.add(botonBorrarArchivo);
		botonSubirArchivo = new JButton("Subir archivo");
		botonSubirArchivo.setBounds(Ancho/40*4 + Ancho/6*3, Alto/10*8 + Alto/20, Ancho/6, Alto/20);
		panel.add(botonSubirArchivo);
		botonDescargarArchivo = new JButton("Descargar archivo");
		botonDescargarArchivo.setBounds(Ancho/40*5 + Ancho/6*4, Alto/10*8 + Alto/20, Ancho/6, Alto/20);
		panel.add(botonDescargarArchivo);
	}
	//Constructor de la clase VentanaCliente
	public VentanaCliente(String ip, int puerto, String usuario, String pass, int ancho, int alto, boolean login)
	{
		//se genera un mensaje de bienvenida con el nombre del usuario que inició sesión
		super("Bienvenido " + usuario);
		//se asignan en variables globales todos los parámetros enviados
		IP = ip;
		Puerto = puerto;
		Usuario = usuario;
		Password = pass;
		Ancho = ancho;
		Alto = alto;
		Login = login;
		// se llama el inicializador de elementos
		init();
		try
		{
			//se abre un nuevo socket con la ip y puerto del servidor
			socket = new Socket(IP, Puerto);
			entradaMensaje = new DataInputStream(socket.getInputStream());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//se cierra la ventana en caso de error
			cerrar();
		}
		//se agrega un window listener que reemplaza el cerrado de la ventana
		//por uno personalizado
		this.addWindowListener
		(
			new WindowAdapter() 
			{
		        public void windowClosing(WindowEvent ev) 
		        {
		        	cerrar();
		        }
			}
		);
		//se agrega un mouse listener a la lista
		listaLocal.addMouseListener
		(
			new MouseListener() 
			{
			
				@Override
				public void mouseReleased(MouseEvent arg0) 
				{
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) 
				{
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) 
				{
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
				}
				
				public void mouseClicked(MouseEvent arg0) 
				{
					//si se da doble click entra aquó
					if(arg0.getClickCount() == 2)
					{
						//se obtiene el elemento seleccionado
						String dir = (String) listaLocal.getSelectedValue();
						//se separa para obtener solamente el nombre
						StringTokenizer ST = new StringTokenizer(dir, ":");
						ST.nextToken();
						String nombreCarpeta = ST.nextToken();
						//si el nombre del directorio es .. entonces se obtiene el directorio padre
						if(nombreCarpeta.equals(".."))
						{
							//se separa el directorio
							StringTokenizer STDir = new StringTokenizer(dirLocal, "\\");
							int limite = STDir.countTokens();
							dirLocal = "";
							//se vuelve a escribir el directorio con excepción del último subdirectorio
							for(int i = 0; i < limite - 1; i++)
							{
								dirLocal += STDir.nextToken() + "\\";
							}
							//se obtiene el directorio absoluto
							File newdir = new File(dirLocal);
							dirLocal = newdir.getAbsolutePath() + "\\";
							//si es un directorio se entra aquí
							if(newdir.isDirectory())
							{
								//se limpia la lista
								dirsLocales.clear();
								//se agrega el directorio default para navegar al directorio padre
								dirsLocales.add("DIR:..");
								//se genera la nueva lista de archivos
								for(int i = 0; i < newdir.list().length; i++)
								{
									File temp = new File(newdir.getAbsolutePath() + "\\" + newdir.list()[i]);
									String tipoArchivo = "FILE";
									if(temp.isDirectory())
										tipoArchivo = "DIR";
									dirsLocales.add(tipoArchivo + ":" + newdir.list()[i]);
								}
								
								listaLocal.setListData(dirsLocales.toArray());
								//panelScrollLocal.setViewportView(listaLocal);
							}							
						}
						else
						{
							
							File newdir = new File(dirLocal + nombreCarpeta);
							dirLocal = newdir.getAbsolutePath() + "\\";
							//si el directorio es una carpeta entonces se llega aquí
							if(newdir.isDirectory())
							{
								//se limpia la lista
								dirsLocales.clear();
								//se agrega el directorio default para navegar al directorio padre
								dirsLocales.add("DIR:..");
								//se genera la nueva lista de archivos
								for(int i = 0; i < newdir.list().length; i++)
								{
									File temp = new File(newdir.getAbsolutePath() + "\\" + newdir.list()[i]);
									String tipoArchivo = "FILE";
									if(temp.isDirectory())
										tipoArchivo = "DIR";
									dirsLocales.add(tipoArchivo + ":" + newdir.list()[i]);
								}
								
								listaLocal.setListData(dirsLocales.toArray());
								//panelScrollLocal.setViewportView(listaLocal);
							}							
						}
					}
				}
			}
		);
		//se agrega un mouse listener a la lista		
		listaRemota.addMouseListener
		(
				new MouseListener() 
				{
					
					@Override
					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mousePressed(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseEntered(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					public void mouseClicked(MouseEvent arg0) {
						// TODO Auto-generated method stub\
						//si se da doble click se llega aquí
						if (arg0.getClickCount() == 2)
						{
							//se obtiene el valor del directorio del servidor
							String dir = (String) listaRemota.getSelectedValue();
							//se separa del tipo de archivo
							StringTokenizer ST = new StringTokenizer(dir, ":");
							ST.nextToken();
							String nombreCarpeta = ST.nextToken();
							//se envía al servidor el nombre de la carpeta para ser procesada
							if(nombreCarpeta.equals(".."))
							{
								MESSAGEOUT("CD-^-..");
							}
							else
							{
								MESSAGEOUT("CD-^-" + nombreCarpeta);
								
							}
						}
						
					}
				}
		);
		//evento del boton para crear carpetas
		botonCrearFolder.addActionListener
		(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						String resultado = JOptionPane.showInputDialog("Nombre:");
						//se envia el mensaje de creacion de carpeta al servidor
						if(resultado != null)
							MESSAGEOUT("MKDIR-^-" + resultado);
					}
				}
		);
		//evento del boton para borrar carpetas
		botonBorrarFolder.addActionListener
		(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						StringTokenizer ST = new StringTokenizer((String) listaRemota.getSelectedValue(), ":");
						ST.nextToken();
						//se envia el mensaje de borrado de carpeta al servidor
						MESSAGEOUT("RMDIR-^-" + ST.nextToken());
					}
				}
		);
		//evento del boton para borrar archivos
		botonBorrarArchivo.addActionListener
		(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						StringTokenizer ST = new StringTokenizer((String) listaRemota.getSelectedValue(), ":");
						ST.nextToken();
						//se envia el mensaje de borrado de archivo al servidor
						MESSAGEOUT("DELETE-^-" + ST.nextToken());
					}
				}
		);
		//evento del boton para subir archivos
		botonSubirArchivo.addActionListener
		(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						//se separa el nombre del tipo de archivo
						StringTokenizer ST = new StringTokenizer((String) listaLocal.getSelectedValue(), ":");
						ST.nextToken();
						String archivo = ST.nextToken();
						//se obtiene el directorio absoluto y se envían el nombre
						//y el tamaño del archivo al servidor
						File fil = new File(dirLocal + archivo);
						MESSAGEOUT("PUT-^-" + archivo + "," + fil.length());
						PUT(archivo);
					}
				}
		);
		//evento del boton para descargar archivos
		botonDescargarArchivo.addActionListener
		(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						//se separa el nombre del tipo de archivo
						StringTokenizer ST = new StringTokenizer((String) listaRemota.getSelectedValue(), ":");
						ST.nextToken();
						String archivo = ST.nextToken();
						//se envía un mensaje para avisarle al servidor que envíe el archivo
						MESSAGEOUT("GET-^-" + archivo);
					}
				}
		);
	}
	
	//Método para recibir mensajes
	private String MESSAGEIN()
	{
		String mensaje = "";
		
		try
		{
			//Se lee un mensaje del socket
			mensaje = entradaMensaje.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return mensaje;
	}
	
	//Método para enviar mensajes
	private void MESSAGEOUT(String mensaje)
	{
		try
		{
			//se asigna una salida por el socket y se envía el mensaje
			DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
			salida.writeUTF(mensaje);
		}
		catch(IOException ioe)
		{
			System.err.println("Error mensajeSalida: " + ioe.toString());
		}		
	}
	//Método para mostrar los directorios del servidor en la lista	
	private void REMOTELISTSHOW(String[] params)
	{
		dirsRemotos.add(params[0] + ":" + params[1]);
		listaRemota.setListData(dirsRemotos.toArray());
	}
	//Método para enviar el archivo al servidor
	private void PUT(String param)
	{
		try
		{
			//nombre del archivo obtenido del parámetro
			String nombreArchivo = param;
			//cantidad de bytes enviados
			int cantBytes = 0;
			//se abre un archivo
			File archivo = new File(dirLocal + nombreArchivo);
			//lector del archivo
			InputStream entrada = new FileInputStream(archivo);
			OutputStream salida = socket.getOutputStream();
			//se genera un bufer de 4k
			byte[] bufer = new byte[4096];
			//se envían datos hasta que se llega al fin del archivo
			while((cantBytes = entrada.read(bufer)) != -1)
			{
				if(cantBytes > 0)
					salida.write(bufer, 0, cantBytes);
			}
			//se cierra el lector del archivo
			entrada.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//Método para descargar archivos del servidor
	private void GET(String[] params)
	{
		try
		{
			//Se obtiene el nombre del archivo
			String nombreArchivo = params[0];
			//se obtiene el tamaño del archivo
			long longArchivoLocal = Long.parseLong(params[1]);
			//se crea otra variable para el tamaño del archivo que venga por el servidor
			long longArchivoRemoto = 0;
			//cantidad de bytes
			int cantBytes = 0;
			//bufer de 4k
			byte[] bufer = new byte[4096];
			//se crea un nueo archivo
			File archivo = new File(dirLocal + nombreArchivo);
			archivo.createNewFile();
			//se crea un stream para escribir en el archivo
			OutputStream salida = new FileOutputStream(archivo);
	
			//entrada de bytes del lado del servidor
			InputStream entrada = socket.getInputStream();
			//mientras el tamaño enviado como parámetro sea mayor que el obtenido durante
			//el envío de datos este ciclo continúa
			while(longArchivoLocal > longArchivoRemoto)
			{
				cantBytes = entrada.read(bufer);
				//se acumula el tamaño del archivo
				longArchivoRemoto += cantBytes;
				if(cantBytes < 0)
				{
				}
				//se escribe en el archivo
				if(cantBytes > 0)
					salida.write(bufer, 0, cantBytes);
			}
			//se cierra el archivo
			salida.close();
			//se carga la lista de archivos
			File newdir = new File(dirLocal);
			dirLocal = newdir.getAbsolutePath() + "\\";
			if(newdir.isDirectory())
			{
				dirsLocales.clear();
				dirsLocales.add("DIR:..");
				for(int i = 0; i < newdir.list().length; i++)
				{
					File temp = new File(newdir.getAbsolutePath() + "\\" + newdir.list()[i]);
					String tipoArchivo = "FILE";
					if(temp.isDirectory())
						tipoArchivo = "DIR";
					dirsLocales.add(tipoArchivo + ":" + newdir.list()[i]);
				}
				
				listaLocal.setListData(dirsLocales.toArray());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	//Método para cerrar la sesión y la ventana
	public void cerrar()
	{
		try
		{
			//se envía el mensaje de cierre de sesión
			MESSAGEOUT("CLOSE");
			//se cierra el socket
			socket.close();
			//se obtienen las dimensiones de la pantalla
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			int ancho = (dim.width*5)/10;
			int alto = (dim.height*5)/10;
			//se crea una nueva instancia de la ventana Login y se envían las dimensiones de la ventana
			//como parámetro
			Login login = new Login(ancho, alto);
			//se asigna la operación de cerrado
			login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//se posiciona la ventana en el centro de la pantalla
			login.setBounds((dim.width/2) - (ancho/2), (dim.height/3) - (alto/3), ancho, alto);
			//se visualiza la ventana
			login.setVisible(true);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);						
		}
	}
	//Hilo de la ventana
	public void run() 
	{
		//se comprueba si es un login
		if (Login)
			//Mensaje para acceso
			MESSAGEOUT("#" + Password + "@" + Usuario);
		else
			//mensaje para creacion de usuario
			MESSAGEOUT("^CREATE-^-" + Password + "@" + Usuario);
		for(;;)
		{
			//entrada del mensaje del servidor
			String mensaje = MESSAGEIN();
			//mensaje para limpiar la lista
			if(mensaje.charAt(0) == '^')
			{
				if(mensaje.equals("^CLEARLIST"))
				{
					dirsRemotos.clear();
					dirsRemotos.add("DIR:..");
					listaRemota.setListData(dirsRemotos.toArray());
				}
			}
			else
			{
				StringTokenizer ST = new StringTokenizer(mensaje, "-^-");
				String comando = ST.nextToken();
				//mensaje para generar la lista de archivos del servidor
				if(comando.equals("LISTLOCATION"))
				{
					//se separan el nombre y tipo de archivo
					StringTokenizer ST2 = new StringTokenizer(ST.nextToken(), ",");
					int limite = ST2.countTokens();
					String[] parametros = new String[limite];
					for(int i = 0; i < limite; i++)
					{
						parametros[i] = ST2.nextToken();
					}
					REMOTELISTSHOW(parametros);
				}
				//se obtiene la descarga del archivo
				if(comando.equals("GET"))
				{
					//se separan los parámetros
					StringTokenizer ST2 = new StringTokenizer(ST.nextToken(), ",");
					int limite = ST2.countTokens();
					String[] parametros = new String[limite];
					for(int i = 0; i < limite; i++)
					{
						parametros[i] = ST2.nextToken();
					}
					//se llama el método de descarga
					GET(parametros);
				}
			}
		}
	}
	
}
