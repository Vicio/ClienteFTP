import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;


public class Principal 
{
	public static void main(String[] args) 
	{
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
}
