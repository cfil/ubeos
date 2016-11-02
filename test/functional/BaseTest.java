//package functional;
//
//
//import helpers.FixturesHelper;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//
//import play.mvc.Http.Response;
//import play.mvc.Http.StatusCode;
//import play.test.Fixtures;
//import play.test.FunctionalTest;
//
//public class BaseTest extends FunctionalTest {
//
//	@Before
//	public void setup() {
//		Fixtures.deleteDatabase();
//		FixturesHelper.resetSequence();
////		FixturesHelper.loadAllModels();
//
////		makeLoginAsAppAdmin();
//	}
//
////	private void makeLoginAsAppAdmin() {
////		Map<String, String> loginUserParams = new HashMap<String, String>();
////		loginUserParams.put("email", "atest@xlm.pt");
////		loginUserParams.put("password", "atest");
////		Response loginResponse = POST("/login", loginUserParams);
////		assertNotNull(loginResponse);
////		assertStatus(StatusCode.FOUND, loginResponse);
////		assertEquals("/", loginResponse.headers.get("Location").value());
////	}
//
//	@Test
//	public void testIndex() {
//		Response index_response = GET("/");
//		assertNotNull(index_response);
//		assertIsOk(index_response);
//		assertContentType("text/html", index_response);
//
//		assertContentMatch("a tua vez de estar no controlo", index_response);
//		
//	}
//	
//	@Test
//	public void testLogin() {
//		Response index_response = GET("/users/create");
//		assertNotNull(index_response);
//		assertIsOk(index_response);
//		assertContentType("text/html", index_response);
//
//		assertContentMatch("Estou pronto", index_response);
//		
//	}
//
//	
//}