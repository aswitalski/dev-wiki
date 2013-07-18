package pl.switalski.wiki.java.hibernate.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "METEOROLOGICAL_OBSERVATORIES")
public class Observatory {
	
	@Id
	@Column(name = "id", length = 6)
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "observatory", cascade = CascadeType.ALL)
	private List<Measurement> measurements;
	
	public Observatory() {
	}
	
	public Observatory(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Measurement> getMeasurements() {
		return measurements;
	}
	
	public void setMeasurements(List<Measurement> measurements) {
		this.measurements = measurements;
	}

	public void addMeasurement(Measurement measurement) {
		if (this.measurements == null) {
			this.measurements = new ArrayList<Measurement>();
		}
		this.measurements.add(measurement);
		measurement.setObservatory(this);
	}

}
