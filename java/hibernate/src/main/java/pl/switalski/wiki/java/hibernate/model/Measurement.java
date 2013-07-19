package pl.switalski.wiki.java.hibernate.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "METEOROLOGICAL_MEASUREMENTS")
public class Measurement extends AbstractEntity {
	
	@Column(name = "ts", nullable = false)
	private Date date;
	
	@Column(name = "reading", nullable = false)
	private Double value;

	@ManyToOne
	@JoinColumn(name = "OBSERVATORY_ID")
	private Observatory observatory;

	public Measurement() {
	}
	
	public Measurement(int id, Date date, double value) {
		this.id = id;
		this.date = date;
		this.value = value;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}

	public Observatory getObservatory() {
		return observatory;
	}
	
	public void setObservatory(Observatory observatory) {
		this.observatory = observatory;
	}

}
