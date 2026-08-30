package com.family_tree.data_storage;

import java.time.LocalDate;

public class Date {
	public static final LocalDate ERROR_DATE = LocalDate.of(0,3,1);

	
	public enum Month {
		January,
		February,
		March,
		April,
		May,
		June,
		July,
		August,
		September,
		October,
		November,
		December,
	}

	public static final int DAYS_PER_MONTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

	public static boolean is_leap_year(int year) {
		return year % 4 == 0;
	}
}
