package pl.switalski.wiki.java.hibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PHONE_NUMBERS")
public class PhoneNumber {
	
	@Id
	@Column(name = "id", nullable = false, length = 6)
	private int id;
	
	@Column(name = "VALUE", length = 6)
	private int value;
	
	@ManyToOne
	@JoinColumn(name = "OBJECT_ID")
	private TelecommunicationObject object;

	public PhoneNumber() {
	}
	
	public PhoneNumber(int id, int value) {
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public TelecommunicationObject getObject() {
		return object;
	}
	
	public void setObject(TelecommunicationObject object) {
		this.object = object;
	}

}
