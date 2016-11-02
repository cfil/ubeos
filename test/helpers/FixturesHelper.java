package helpers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Table;


import org.joda.time.DateTime;

import play.Play;
import play.db.DB;
import play.db.jpa.JPA;
import play.mvc.Http.Request;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class FixturesHelper {

	private static final String EXT = ".yml";
	private static final String TEST_FIXTURES = "test_fixtures";

	/**
	 * loads models name
	 * 
	 */
	public static void loadModels(String ... names) {

		for (String name : names) {
			Fixtures.loadModels(name + EXT);
		}
	}

	public static void loadAllModels() {
		loadModels(TEST_FIXTURES);
	}

	public static void transactionManagement() {
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();

	}

	public static void resetSequence() {
		if (Play.id.equals("test") && Play.configuration.getProperty("jpa.dialect") == null && Play.configuration.getProperty("db")==null && Play.configuration.getProperty("db")!="mem"){
			List<Class> clazz = Play.classloader. getAnnotatedClasses(Table.class);
			for (Class class1 : clazz) {
				Table annotation = (Table) class1.getAnnotation(Table.class);
				Fixtures.executeSQL("ALTER TABLE " + annotation.name() + " ALTER COLUMN id RESTART WITH 1");
			}
		}
	}

}
