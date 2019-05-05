package SQLFiller;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class ContentGenerator {

	static Random Rand = new Random();
	private static String defaultPathName = "DataFiles/";

	static ArrayList<String> maleNames = new ArrayList<String>();
	static ArrayList<String> femaleNames = new ArrayList<String>();
	static ArrayList<String> lastNames = new ArrayList<String>();
	static ArrayList<String> majors = new ArrayList<String>();
	static ArrayList<SchoolClass> classes = new ArrayList<SchoolClass>();
	static ArrayList<String> usedClassCodes = new ArrayList<>();
	static ArrayList<Integer> studentSSNs = new ArrayList<Integer>();
	static HashMap<Integer, String> nameWithId = new HashMap<>();

	public static void main(String[] args) throws IOException {
		
		final int NUM_STUDENTS = 1000;
		final int NUM_STAFF = 300;
		final int NUM_CLASSES = 500;
		final int NUM_STUDENT_CLASS_RELATIONS = 3000;
		

		fillArrayLists();
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("InputLines.txt"), "utf-8"));	
			writer.write(createStudentInputs(NUM_STUDENTS));
			((BufferedWriter) writer).newLine();
			((BufferedWriter) writer).newLine();
			writer.write(createStaffInputs(NUM_STAFF));
			((BufferedWriter) writer).newLine();
			((BufferedWriter) writer).newLine();
			writer.write(createClassesInputs(NUM_CLASSES));
			((BufferedWriter) writer).newLine();
			((BufferedWriter) writer).newLine();
			writer.write(createStaffClassRelationInputs(NUM_CLASSES));
			((BufferedWriter) writer).newLine();
			((BufferedWriter) writer).newLine();
			writer.write(createStudentClassRelationInputs(NUM_STUDENT_CLASS_RELATIONS));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public static void fillArrayLists() {
		try {
			File maleNamesFile = new File(defaultPathName + "maleNames.txt");
			File femaleNamesFile = new File(defaultPathName + "femaleNames.txt");
			File lastNamesFile = new File(defaultPathName + "lastNames.txt");
			File majorsFile = new File(defaultPathName + "majors.txt");
			File classesFile = new File(defaultPathName + "classData.txt");

			Scanner maleNamesScnr = new Scanner(new FileInputStream(maleNamesFile));
			Scanner femaleNamesScnr = new Scanner(new FileInputStream(femaleNamesFile));
			Scanner lastNamesScnr = new Scanner(new FileInputStream(lastNamesFile));
			Scanner majorsScnr = new Scanner(new FileInputStream(majorsFile));
			Scanner classesScnr = new Scanner(new FileInputStream(classesFile));

			while (maleNamesScnr.hasNext()) {
				String maleName = maleNamesScnr.next();
				maleNames.add(maleName);
			}
			while (femaleNamesScnr.hasNext()) {
				String femaleName = femaleNamesScnr.next();
				femaleNames.add(femaleName);
			}
			while (lastNamesScnr.hasNext()) {
				String lastName = lastNamesScnr.next();
				lastNames.add(lastName);
			}
			while (majorsScnr.hasNext()) {
				String major = majorsScnr.nextLine();
				majors.add(major);
			}
			while (classesScnr.hasNextLine()) {
				String fullClass = classesScnr.nextLine();
				int endOfCodeIndex = fullClass.indexOf(" ", fullClass.indexOf(" ") + 1);
				int endOfNameIndex = fullClass.lastIndexOf(" ");
				String code = fullClass.substring(0, endOfCodeIndex);
				String name = fullClass.substring(endOfCodeIndex + 1, endOfNameIndex);
				int value = Integer.parseInt(fullClass.substring(endOfNameIndex + 1));
				classes.add(new SchoolClass(code, name, value));
			}
			
			maleNamesScnr.close();
			femaleNamesScnr.close();
			lastNamesScnr.close();
			majorsScnr.close();
			classesScnr.close();

		} catch (FileNotFoundException e) {
			System.out.println("Incorrect file setup");
			System.exit(0);
		}
	}

	public static String createStudentInputs(int count) {

		String studentInsertLines = "";

		for (int i = 0; i < count; i++) {

			int SSN = setSSN();
			char sex = setSex();
			String firstName = selectNameForSex(sex);
			String lastName = setLastName();
			int credits = setCredits();
			String birthDate = setStudentBirthDate();
			String major = setMajor();

			studentInsertLines += "INSERT INTO Students VALUES(" + SSN + ", '" + firstName + "', '" + lastName + "', "
					+ credits + ", '" + birthDate + "', '" + sex + "', '" + major + "');\n";

		}
		return studentInsertLines;
	}

	public static String createStaffInputs(int count) {

		String staffInsertLines = "";
		int staffId;
		for (int i = 0; i < count; i++) {

			staffId = i + 1000;
			char sex = setSex();
			String firstName = selectNameForSex(sex);
			String lastName = setLastName();
			String birthDate = "";
			String startDate = "";
			Boolean reasonableAge = false;

			while (!reasonableAge) {
				birthDate = setStaffBirthDate();
				startDate = setStaffStartDate();
				reasonableAge = checkReasonableAge(birthDate, startDate);
			}

			String staffFullName = firstName + " " + lastName;
			nameWithId.put(staffId, staffFullName);

			staffInsertLines += "INSERT INTO Staff VALUES(" + staffId + ", '" + firstName + "', '" + lastName + "', '"
					+ sex + "', '" + birthDate + "', '" + startDate + "');\n";

		}
		return staffInsertLines;
	}
	
	public static String createClassesInputs(int count) throws ParseException {
		String classesInsertLines = "";
		for (int i = 0; i < count; i++) {
			int randIndex = Rand.nextInt(classes.size());
			SchoolClass chosenClass = classes.get(randIndex);
			usedClassCodes.add(chosenClass.classCode);
			classes.remove(randIndex);
			String days = setDays();
			String time = selectAccurateTime(days);
			
			classesInsertLines += "INSERT INTO Classes VALUES('" + chosenClass.toString() + 
					days + "', '" + time + "');\n";
		}
		System.out.println(classesInsertLines);
		return classesInsertLines;
	}
	
	public static String createStaffClassRelationInputs(int count) {
		String staffClassRelationInputs = "";
		for (int i = 0; i < count; i++) {
			int staffId = Rand.nextInt(nameWithId.size()) + 1000;
			String classId = usedClassCodes.get(Rand.nextInt(usedClassCodes.size()));
			staffClassRelationInputs += "INSERT INTO Staff_Class_Relation VALUES('" + classId + "', " + staffId + ");\n";
		}
		return staffClassRelationInputs;
	}
	
	public static String createStudentClassRelationInputs(int count) {
		String studentClassRelationInputs = "";
		for (int i = 0; i < count; i++) {
			String classId = usedClassCodes.get(Rand.nextInt(usedClassCodes.size()));
			int studentSSN = studentSSNs.get(Rand.nextInt(studentSSNs.size()));
			studentClassRelationInputs += "INSERT INTO Student_Class_Relation VALUES('" + classId + "', " + studentSSN + ");\n";
		}
		return studentClassRelationInputs;
	}

	private static String selectAccurateTime(String days) throws ParseException {
		String time = "";
		if (days.compareTo("M/W/F") == 0) {
			time = chooseHours(0);
		}
		else if (days.compareTo("M/W") == 0 || days.compareTo("T/TH") == 0) {
			time = chooseHours(1);
		}
		else {
			time = chooseHours(2);
		}
		return time;
	}

	private static String chooseHours(int length) throws ParseException {
		String startTime = chooseStartTime();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = df.parse(startTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		if (length == 0) {
			cal.add(Calendar.MINUTE, 50);
		} 
		else if (length == 1) {
			cal.add(Calendar.MINUTE, 75);
		} 
		else {
			cal.add(Calendar.MINUTE, 150);
		}
		String endTime = df.format(cal.getTime());
		return startTime + "-" + endTime;
	}
	
	private static String chooseStartTime() {
		int result = Rand.nextInt(20);
		switch(result) {
			case 0:
				return "8:00";
			case 1: 
				return "8:30";
			case 2:
				return "9:00";
			case 3: 
				return "9:30";
			case 4:
				return "10:00";
			case 5:
				return "10:30";
			case 6: 
				return "11:00";
			case 7:
				return "11:30";
			case 8: 
				return "12:00";
			case 9:
				return "12:30";
			case 10: 
				return "13:00";
			case 11:
				return "13:30";
			case 12: 
				return "14:00";
			case 13:
				return "14:30";
			case 14: 
				return "15:00";
			case 15:
				return "15:30";
			case 16: 
				return "16:00";
			case 17:
				return "16:30";
			case 18: 
				return "17:00";
			case 19:
				return "17:30";
			default: 
				return "18:00";
		}
	}
	
	private static String setDays() {
		int result = Rand.nextInt(5);
		if (result == 0) {
			return "M/W/F";
		}
		else if (result == 1) {
			return "M/W";
		}
		else if (result == 2) {
			return "T/TH";
		}
		else if (result == 3) {
			return "T";
		}
		else {
			return "TH";
		}
	}

	public static String setMaleName() {
		int index = Rand.nextInt(maleNames.size());
		return maleNames.get(index);
	}

	public static String setFemaleName() {
		int index = Rand.nextInt(femaleNames.size());
		return femaleNames.get(index);
	}

	public static String setLastName() {
		int index = Rand.nextInt(lastNames.size());
		return lastNames.get(index);
	}

	public static char setSex() {
		int result = Rand.nextInt(2);
		if (result == 0) {
			return 'M';
		} else {
			return 'F';
		}
	}

	public static int setSSN() {
		String result = String.valueOf(Rand.nextInt(1000000000));
		while (result.length() < 9) {
			result = "0" + result;
		}
		int ssn = Integer.parseInt(result);
		if (!studentSSNs.contains(ssn)) {
			studentSSNs.add(ssn);
			return ssn;
		}
		else {
			return setSSN();
		}
	}

	public static String setStudentBirthDate() {
		int year = Rand.nextInt(25) + 1975;
		int month = Rand.nextInt(12) + 1;
		int day = setAccurateDay(month);
		return (year + "-" + month + "-" + day);
	}

	public static String setMajor() {
		int index = Rand.nextInt(majors.size());
		return majors.get(index);
	}

	public static int setCredits() {
		int result = Rand.nextInt(135);
		return result;
	}

	public static String setStaffBirthDate() {
		int year = Rand.nextInt(25) + 1950;
		int month = Rand.nextInt(12) + 1;
		int day = setAccurateDay(month);
		return (year + "-" + month + "-" + day);
	}

	public static String setStaffStartDate() {
		int year = Rand.nextInt(40) + 1979;
		int month = Rand.nextInt(12) + 1;
		int day = setAccurateDay(month);
		return (year + "-" + month + "-" + day);
	}

	public static int setAccurateDay(final int month) {
		int day;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			day = Rand.nextInt(31) + 1;
		} else if (month == 2) {
			day = Rand.nextInt(28) + 1;
		} else {
			day = Rand.nextInt(30) + 1;
		}
		return day;
	}

	public static String selectNameForSex(final char sex) {
		String firstName;

		if (sex == 'M') {
			firstName = setMaleName();
		} else {
			firstName = setFemaleName();
		}
		return firstName;
	}

	public static Boolean checkReasonableAge(String birthDate, String startDate) {
		birthDate = setStaffBirthDate();
		startDate = setStaffStartDate();

		String[] birthYearString = birthDate.split("-");
		String[] startYearString = startDate.split("-");
		int birthYear = Integer.parseInt(birthYearString[0]);
		int startYear = Integer.parseInt(startYearString[0]);
		int startingAge = startYear - birthYear;

		return (startingAge > 20);
	}
	
	static class SchoolClass {
		String classCode;
		String className;
		int classValue;
		
		SchoolClass (final String cCode, final String cName, final int cValue) {
			this.classCode = cCode;
			this.className = cName;
			this.classValue = cValue;
		}
		
		@Override
		public String toString() {
			return (classCode + "', '" + className + "', " + classValue + ", '");
		}
	}
}
