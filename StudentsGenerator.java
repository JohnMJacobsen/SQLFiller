import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class StudentsGenerator {

	static Random Rand = new Random();

	static ArrayList<String> maleNames = new ArrayList<String>();
	static ArrayList<String> femaleNames = new ArrayList<String>();
	static ArrayList<String> lastNames = new ArrayList<String>();
	static ArrayList<String> majors = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		fillArrayLists();
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("filename.txt"), "utf-8"));
			writer.write(createStudentInputs(5000));
			((BufferedWriter) writer).newLine();
			((BufferedWriter) writer).newLine();
			writer.write(createStaffInputs(500));
			((BufferedWriter) writer).newLine();
			((BufferedWriter) writer).newLine();

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		createStaffInputs(10);
		// createClassesInput(10);
	}

	public static void fillArrayLists() {
		try {

			File maleNamesFile = new File("C:\\Users\\jmjac\\eclipse-workspace\\SQLFiller\\src\\maleNames.txt");
			File femaleNamesFile = new File("C:\\Users\\jmjac\\eclipse-workspace\\SQLFiller\\src\\femaleNames.txt");
			File lastNamesFile = new File("C:\\Users\\jmjac\\eclipse-workspace\\SQLFiller\\src\\lastNames.txt");
			File majorsFile = new File("C:\\Users\\jmjac\\eclipse-workspace\\SQLFiller\\src\\majors.txt");

			Scanner maleNamesScnr = new Scanner(new FileInputStream(maleNamesFile));
			Scanner femaleNamesScnr = new Scanner(new FileInputStream(femaleNamesFile));
			Scanner lastNamesScnr = new Scanner(new FileInputStream(lastNamesFile));
			Scanner majorsScnr = new Scanner(new FileInputStream(majorsFile));

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
			maleNamesScnr.close();
			femaleNamesScnr.close();
			lastNamesScnr.close();
			majorsScnr.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not found. I hardcoded them to my computer for testing, sorry.");
		}
	}

	public static String createStudentInputs(int count) {

		String studentInsertLines = "";

		for (int i = 0; i < count; i++) {

			String SSN = setSSN();
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
		HashMap<Integer, String> nameWithId = new HashMap<>();
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

			String staffFullName = firstName + lastName;

			nameWithId.put(staffId, staffFullName);

			staffInsertLines += "INSERT INTO Staff VALUES(" + staffId + ", '" + firstName + "', '" + lastName + "', '"
					+ birthDate + "', '" + startDate + "');\n";

		}
		return staffInsertLines;
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

	public static String setSSN() {
		String result = String.valueOf(Rand.nextInt(1000000000));
		while (result.length() < 9) {
			result = "0" + result;
		}
		return result;
	}

	public static String setStudentBirthDate() {
		int year = Rand.nextInt(25) + 1950;
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

	public static int setAccurateDay(int month) {
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

	public static String selectNameForSex(char sex) {
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
}
