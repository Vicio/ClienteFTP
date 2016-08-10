import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Login extends JFrame
{

	private static final long serialVersionUID = 5324709231884325577L;
	//dimensiones de la ventana
	private int anchoVentana;
	private int altoVentana;
	//labels
	private JLabel labelIP;
	private JLabel labelPuerto;
	private JLabel labelUsuario;
	private JLabel labelPassword;
	//campos de texto
	private JTextField campoTextoIP;
	private JTextField campoTextoPuerto;
	private JTextField campoTextoUsuario;
	private JTextField campoTextoPassword;
	//el contenedor de los objetos visuales
	private Container contenedor;
	// el panel donde se colocaran los campos labels y botones
	private JPanel panel;
	//botones
	private JButton botonInicioSesion;
	private JButton botonCrearUsuario;
	private JButton botonCancelar;
	//Constructor con las dimensiones de la ventana
	public Login(int ancho, int alto)
	{
		super("Login");
		//se asignan las dimensiones a unas variables globales
		anchoVentana = ancho;
		altoVentana = alto;
		//se llama el inicializador de los elementos de la ventana
		init();
		//evento para crear la ventana de VentanaCliente como login
		botonInicioSesion.addActionListener
		(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent arg0) 
				{
					GenerarVentanaCliente(true);
				}
			}
		);
		//evento para crear la ventana de VentanaCliente como nuevo usuario
		botonCrearUsuario.addActionListener
		(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent arg0) 
				{
					try
					{
						GenerarVentanaCliente(false);						
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		);
		//evento del boton para cerrar la aplicacion
		botonCancelar.addActionListener
		(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent arg0) 
				{
					try
					{
						System.exit(0);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		);
	}
	

	public void init()
	{
		//Se inicializan los labels
		labelIP = new JLabel("IP:");
		labelPuerto = new JLabel("Puerto:");
		labelUsuario = new JLabel("Usuario:");
		labelPassword = new JLabel("Contraseña:");
		//se inicializan los campos de texto
		campoTextoIP = new JTextField();
		campoTextoPuerto = new JTextField();
		campoTextoUsuario = new JTextField();
		campoTextoPassword = new JTextField();
		//se inicializa el contenedor
		contenedor = getContentPane();
		//se inicializa el panel
		panel = new JPanel();
		//se le asigna un layout nulo
		panel.setLayout(null);
		//se inicializan los botones
		botonInicioSesion = new JButton("Login");
		botonCrearUsuario = new JButton("Crear cuenta");
		botonCancelar = new JButton("Cancelar");
		//se agrega el panel al contenedor
		contenedor.add(panel);
		//se asignan los diversos elementos al panel con
		//las diferentes coordenadas y tamaños relativos para que
		//se autoajusten a la ventana
		campoTextoIP.setBounds(anchoVentana/2 + 30, altoVentana/10, anchoVentana/3, 30);
		panel.add(campoTextoIP);
		campoTextoPuerto.setBounds(anchoVentana/2 + 30, altoVentana/10*2, anchoVentana/3, 30);
		panel.add(campoTextoPuerto);
		campoTextoUsuario.setBounds(anchoVentana/2 + 30, altoVentana/10*3, anchoVentana/3, 30);
		panel.add(campoTextoUsuario);
		campoTextoPassword.setBounds(anchoVentana/2 + 30, altoVentana/10*4, anchoVentana/3, 30);
		panel.add(campoTextoPassword);
		labelIP.setBounds(anchoVentana/4 - 18, altoVentana/10, anchoVentana/3, 30);
		panel.add(labelIP);
		labelPuerto.setBounds(anchoVentana/4 - 42, altoVentana/10*2, anchoVentana/3, 30);
		panel.add(labelPuerto);
		labelUsuario.setBounds(anchoVentana/4 - 48, altoVentana/10*3, anchoVentana/3, 30);
		panel.add(labelUsuario);
		labelPassword.setBounds(anchoVentana/4 - 66, altoVentana/10*4, anchoVentana/3, 30);
		panel.add(labelPassword);
		
		botonInicioSesion.setBounds(30, altoVentana/10*7, anchoVentana/3-30, 50);
		panel.add(botonInicioSesion);
		botonCrearUsuario.setBounds(anchoVentana/2 - (anchoVentana/3-30)/2, altoVentana/10*7, anchoVentana/3-30, 50);
		panel.add(botonCrearUsuario);
		botonCancelar.setBounds(anchoVentana - (anchoVentana/3-30) - 30, altoVentana/10*7, anchoVentana/3-30, 50);
		panel.add(botonCancelar);
	}
	//Método para crear la ventana de VentanaCliente
	private void GenerarVentanaCliente(boolean login)
	{
		try
		{
			//se obtienen las dimensiones de la pantalla
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			int ancho = (dim.width/10)*7;
			int alto = (dim.height/10)*7;
			//se genera la ventana con los parametros de:
			//la ip para la conexion
			//el puerto
			//el usuario
			//la contraseña
			//el ancho
			//el alto
			//el valor que indica si es un acceso o un nuevo usuario
			final VentanaCliente ventana = new VentanaCliente
			(
				campoTextoIP.getText(), 
				Integer.parseInt(campoTextoPuerto.getText()), 
				campoTextoUsuario.getText(), 
				campoTextoPassword.getText(),
				ancho,
				alto,
				login
			);
			//se centra la ventana
			ventana.setBounds((dim.width/2) - (ancho/2), (dim.height/3) - (alto/3), ancho, alto);
			//se asigna como operacion de cerrado que solo se oculte
			ventana.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			//se visualiza
	        ventana.setVisible(true);
	        //se genera como hilo
			Thread hilo = new Thread(ventana);
			//se inicia
			hilo.start();
			//se oculta la ventana de login
			setVisible(false);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
