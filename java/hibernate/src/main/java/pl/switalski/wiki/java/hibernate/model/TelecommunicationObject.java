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
@Table(name = "TELECOMMUNICATION_OBJECTS")
public class TelecommunicationObject {
	
	@Id
	@Column(name = "id", length = 6)
	private int id;
	
	@Column(name = "name", length = 100)
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "object", cascade = CascadeType.ALL)
	private List<PhoneNumber> numbers;
	
	public TelecommunicationObject() {
	}
	
	public TelecommunicationObject(int id, String name) {
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
	
	public List<PhoneNumber> getNumbers() {
		return numbers;
	}
	
	public void setNumbers(List<PhoneNumber> numbers) {
		this.numbers = numbers;
	}
	
	public void addNumber(PhoneNumber number) {
		if (this.numbers == null) {
			this.numbers = new ArrayList<>();
		}
		number.setObject(this);
		this.numbers.add(number);
	}

}
