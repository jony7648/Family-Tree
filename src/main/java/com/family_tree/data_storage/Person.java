package com.family_tree.data_storage;

import java.time.LocalDate;
import com.family_tree.algorithms.Search;

public class Person implements Search.ISearchableObject{
	public enum Gender {
		Male,
		Female,
		NonBinary,
		Other,
	}

	public enum SearchProperty {
		FirstName,
		LastName,
	}

	static final LocalDate DEFAULT_DATE = LocalDate.of(0,3,1);
	static final String DEFAULT_STR_ENTRY = "N/A";

	public static final int PROPETY_COUNT = 9;

	public static final String FIRST_NAME_JSON_PROPERTY = "first_name";
	public static final String LAST_NAME_JSON_PROPERTY = "last_name";
	public static final String GENDER_JSON_PROPERTY = "gender";
	public static final String BIRTH_DATE_JSON_PROPERTY = "birth_date";
	public static final String PASSING_DATE_JSON_PROPERTY = "passing_date";
	public static final String ORGIN_COUNTRY_JSON_PROPERTY = "orgin_country";
	public static final String ORGIN_PROVINCE_JSON_PROPERTY = "orgin_province";
	public static final String DESCRIPTION_JSON_PROPERTY = "description";
	public static final String CHILDREN_JSON_PROPERTY = "children";
	
	private String _first_name = DEFAULT_STR_ENTRY;
	private String _last_name = DEFAULT_STR_ENTRY;
	private Gender _gender = Gender.Other;
	private LocalDate _birth_date;
	private LocalDate _date_of_passing;
	private String _orgin_country = DEFAULT_STR_ENTRY;
	private	String _orgin_province = DEFAULT_STR_ENTRY;
	private String _description = DEFAULT_STR_ENTRY;

	public Person() {
		_birth_date = DEFAULT_DATE;
		_date_of_passing = DEFAULT_DATE;
	}

	@Override
	public String toString() {
		return String.format(
			"Person: %s %s\n" +
			"Gender: %s\n" +
			"Birth Date: %s\n" +
			"Date Of Passing: %s\n" +
			"Orgin Country: %s\n" +
			"Orgin Province: %s\n" +
			"Description: %s\n"
			,_first_name, _last_name, _gender, _birth_date, _date_of_passing, _orgin_country, _orgin_province, _description
		);
	}

	public void set_first_name(String first_name) {
		_first_name = first_name;
	}

	public String get_first_name() {
		return _first_name;
	}

	public void set_last_name(String last_name) {
		_last_name = last_name;	
	}

	public String get_last_name() {
		return _last_name;
	}
	
	public void set_name(String first_name, String last_name) {
		_first_name = first_name;
		_last_name = last_name;
	}

	public void set_gender(Gender gender) {
		_gender = gender;
	}

	public void set_gender(String gender) {
		_gender = Gender.valueOf(gender);
	}

	public Gender get_gender() {
		return _gender;
	}

	public void set_birth_date(int year, int month, int day) {
		_birth_date = LocalDate.of(year, month, day) ;
	}

	public void set_birth_date(String date_str) {
		if (date_str.equals(DEFAULT_STR_ENTRY)) {
			return;
		}
		
		_birth_date = LocalDate.parse(date_str);
	}

	public void set_birth_date(LocalDate date) {
		_birth_date = date;
	}

	public LocalDate get_birth_date() {
		return _birth_date;
	}

	public void set_date_of_passing(int year, int month, int day) {
		_date_of_passing = LocalDate.of(year, month, day);
	}

	public void set_date_of_passing(String date_str) {
		if (date_str.equals(DEFAULT_STR_ENTRY)) {
			return;
		}
		
		_date_of_passing = LocalDate.parse(date_str);
	}

	public void set_date_of_passing(LocalDate date) {
		_date_of_passing = date;
	}

	public LocalDate get_date_of_passing() {
		return _date_of_passing;
	}

	public void set_orgin_country(String country) {
		_orgin_country = country;
	}

	public String get_orgin_country() {
		return _orgin_country;
	}
	
	public void set_orgin_province(String province) {
		_orgin_province = province;
	}

	public String get_orgin_province() {
		return _orgin_province;
	}

	public void set_description(String description) {
		_description = description;	
	}

	public String get_description() {
		return _description;
	}

	@Override
	public String get_search_string(int property_value) {
		SearchProperty[] property_arr = SearchProperty.values();

		if (property_value >= property_arr.length) {
			return "";
		}
		
		SearchProperty property = SearchProperty.values()[property_value];
		
		switch (property) {
			case FirstName:
				return _first_name;
			case LastName:
				return _last_name;
			}	
				
		return "";
	}
}

