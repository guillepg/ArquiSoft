package myusick.model.entities;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class Publicacion {

	@Id
	@Column(name = "idpublicacion", nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idpublicacion;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", nullable = false)
	private Date fecha;

	@Column(name = "contenido", nullable = false, length = 144)
	private String contenido;
	
	@Column(name = "Publicante_UUID", nullable = false)
	private int Publicante_UUID;

	/*------GETTERS/SETTERS------*/
	
	public int getIdpublicacion() {
		return idpublicacion;
	}

	public void setIdpublicacion(int idpublicacion) {
		this.idpublicacion = idpublicacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public int getPublicante_UUID() {
		return Publicante_UUID;
	}

	public void setPublicante_UUID(int publicante_UUID) {
		Publicante_UUID = publicante_UUID;
	}
}
