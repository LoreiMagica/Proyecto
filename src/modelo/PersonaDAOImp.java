package modelo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class PersonaDAOImp implements PersonaDAO{
	private static Connection conexion = ConexionSQL.getConexion();

	private String[] nombreColumnas;
	private Object[][] datos;	
	
	public PersonaDAOImp (String fichero) {
		//listarTodasPersonas(fichero);
	}
	
	public String[] getColumnas() {
		return nombreColumnas;
	}

	public Object[][] getContenido() {
		return datos;
	}
	
	public void cargarContenido(String fichero) {
		try {
			Document doc = ConexionXML.leerFichero(fichero);	
			NodeList nodoPersonas = doc.getElementsByTagName("record");
			
			Statement statementBorrar = null;
			statementBorrar = conexion.createStatement();
			statementBorrar.executeUpdate("drop table if exists Personas;");
			
			conexion.createStatement().execute("CREATE TABLE Personas(codigo integer primary key, nombre varchar(25) not null,"
					+ " apellido varchar(50) not null, edad integer not null);");
			
			for (int i = 0; i < nodoPersonas.getLength(); i++) {
				Node nodo = nodoPersonas.item(i);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element elemento = (Element)nodo;
					
					PreparedStatement statement = conexion.prepareStatement("INSERT INTO Personas(codigo, nombre, apellido, edad)\n" +
			                  "VALUES(?, ?, ?, ?);");
					statement.setString( 1, ConexionXML.obtenerNodoValor("id", elemento));
					statement.setString(2, ConexionXML.obtenerNodoValor("first_name", elemento));
					statement.setString(3, ConexionXML.obtenerNodoValor("last_name", elemento));
					statement.setString(4, ConexionXML.obtenerNodoValor("Age", elemento));
					statement.executeUpdate();
				}				
			}
		} catch(ParserConfigurationException | SAXException |IOException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public ArrayList<PersonaDTO> listarTodasPersonas(String fichero) {
		// TODO Auto-generated method stub
		ArrayList<PersonaDTO> listaPersonas = new ArrayList<>();
			String sql = "SELECT * FROM Personas;";
			try (Statement statement = conexion.createStatement();){
				ResultSet resulset = statement.executeQuery(sql);
				while (resulset.next()) {
					PersonaDTO persona = new PersonaDTO(resulset.getInt(1),
							resulset.getString(2),
							resulset.getString(3),
							resulset.getInt(4));
					listaPersonas.add(persona);

			int i = 0;
			datos = new Object[listaPersonas.size()][4];

			for (PersonaDTO datosPersona : listaPersonas) {
				datos[i][0] = (Object) datosPersona.getCodigo();
				datos[i][1] = (Object) datosPersona.getNombre();
				datos[i][2] = (Object) datosPersona.getApellido();
				datos[i][3] = (Object) datosPersona.getEdad();
				i++;
			}
			obtenerCabeceraBaseDatos(fichero);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return listaPersonas;
	}
	@Override
	public void obtenerCabeceraBaseDatos(String fichero) {
		Document doc;
		try {
			doc = ConexionXML.leerFichero(fichero);
			NodeList nodoPersonas = doc.getElementsByTagName("dataset");
			Node nodo = nodoPersonas.item(0);
			Element elemento = (Element)nodo;
			nombreColumnas = new String[elemento.getElementsByTagName("record").item(0).getChildNodes().getLength()];
			for (int i = 0; i < elemento.getElementsByTagName("record").item(0).getChildNodes().getLength(); i++) {
				nombreColumnas[i] = elemento.getElementsByTagName("record").item(0).getChildNodes().item(i).getNodeName();
				
			}
			
		} catch ( IOException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean insertarPersona(PersonaDTO persona, ArrayList<PersonaDTO> listaPersonas) {
		
		// TODO Auto-generated method stub
		int insertado = 0;
			String sql = "INSERT INTO Personas (codigo, nombre, apellido, edad) VALUES (?,?,?,?);";
			try (PreparedStatement psStatement = conexion.prepareStatement(sql);){
				psStatement.setInt(1, listaPersonas.size() + 1);
				psStatement.setString(2, persona.getNombre());
				psStatement.setString(3, persona.getApellido());
				psStatement.setInt(4, persona.getEdad());
				insertado = psStatement.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insertado == 1;	
		}
	
	@Override
	public boolean borrarPersona(String nombre, String apellido) {
		// TODO Auto-generated method stub
		int borrado = 0;
		String sql = "DELETE FROM Personas WHERE nombre = ? AND apellido = ?;";
		try (PreparedStatement statement = conexion.prepareStatement(sql);){
			statement.setString(1, nombre);
			statement.setString(2, apellido);
			borrado = statement.executeUpdate();
		} catch ( SQLException e) {
			e.printStackTrace();
		}
			return borrado == 1;
			
				}

	@Override
	public boolean actualizarNombrePersona(int codigo, String nombre) {
		// TODO Auto-generated method stub
		int actualizado = 0;
		String sql = "UPDATE Personas SET nombre = ? WHERE codigo = ?;";
		try (PreparedStatement statement = conexion.prepareStatement(sql);){
			statement.setString(1, nombre);
			statement.setInt(2, codigo);
			actualizado = statement.executeUpdate();
		} catch ( SQLException e) {
			e.printStackTrace();
		}
			return actualizado == 1;
	}
	
	@Override
	public boolean actualizarApellidoPersona(int codigo, String apellido) {
		// TODO Auto-generated method stub
		int actualizado = 0;
		String sql = "UPDATE Personas SET apellido = ? WHERE codigo = ?;";
		try (PreparedStatement statement = conexion.prepareStatement(sql);){
			statement.setString(1, apellido);
			statement.setInt(2, codigo);
			actualizado = statement.executeUpdate();
		} catch ( SQLException e) {
			e.printStackTrace();
		}
			return actualizado == 1;
	}
	
	@Override
	public boolean actualizarEdadPersona(int codigo, int edad) {
		// TODO Auto-generated method stub
		int actualizado = 0;
		String sql = "UPDATE Personas SET edad = ? WHERE codigo = ?;";
		try (PreparedStatement statement = conexion.prepareStatement(sql);){
			statement.setInt(1, edad);
			statement.setInt(2, codigo);
			actualizado = statement.executeUpdate();
		} catch ( SQLException e) {
			e.printStackTrace();
		}
			return actualizado == 1;
	}
}