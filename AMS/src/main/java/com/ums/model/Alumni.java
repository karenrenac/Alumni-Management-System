package com.ums.model;

import jakarta.persistence.*;


@Entity
public class Alumni {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int alumniId;
	private String name;
	private String username;
	private String email;
	private String password;
	private int graduationYear;
	private String universityId;
	
	
	public Alumni(int alumniId, String name, String username, String email, String password, int graduationYear,
			String universityId) {
		super();
		this.alumniId = alumniId;
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.graduationYear = graduationYear;
		this.universityId = universityId;
	}

	
	public Alumni() {
		super();
	}


	public int getAlumniId() {
		return alumniId;
	}


	public void setAlumniId(int alumniId) {
		this.alumniId = alumniId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public int getGraduationYear() {
		return graduationYear;
	}


	public void setGraduationYear(int graduationYear) {
		this.graduationYear = graduationYear;
	}


	public String getUniversityId() {
		return universityId;
	}


	public void setUniversityId(String universityId) {
		this.universityId = universityId;
	}
	
	
}
