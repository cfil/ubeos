package helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import play.mvc.Util;

public class TestimonialsHelper {
	
	public static final int MAX_TESTIMONIALS = 13;
	
	public static final String name_1 = "<b>Luis Pinto</b>, Pet Universal";
	public static final String test_1 = "Ubeos' team is made of extraordinary entrepreneurs. For their experience but mainly for their spirit. I know Carlos for some time now, and it has been a pleasure to discuss work and business with him. Carlos is a team member I would be privileged to work with in any project.";
	
	public static final String name_2 = "<b>Bruno Mota</b>, Bold International";
	public static final String test_2 = "Amazing TEAM! Amazing PROJECT! UBEOS brings clearly an added value to the market, from the travel agency to the final client. I definitely recommend. If you are still not using it, you should!";

	public static final String name_3 = "<b>Miguel Luís</b>";
	public static final String test_3 = "UBEOS is such an amazing project from the ground up. From the conceptual challenges to the technical ones. It's a groundbreaking turnaround of a business model in a scenario of a WIN-WIN model. I'm an UBEOS user totally believing it will be disruptive as the word is spreading among travellers and travel agencies. I totally recommend this service.";

	public static final String name_4 = "<b>Mário Mouraz</b>, Climber Hotel";
	public static final String test_4 = "UBEOS is a disruptive startup that I strongly believe will shape the way people book their travels nowadays and Carlos a visionary leader with a great execution capacity. Recommended 100%.";

	public static final String name_5 = "<b>David Mota</b>, SkyEye";
	public static final String test_5 = "UBEOS' vision is to change the way people book their trips nowadays. With a very powerful, motivated and smart team, I have no doubt the tourism sector will no more be the same with the appearance of UBEOS.";

	public static final String name_6 = "<b>Nuno Rodrigues</b>";
	public static final String test_6 = "These guys have a plan! They would not step down in the face of adversity and have managed to push things forward constantly. Every step of the way they have been capable of delivering and showing their work and evolution. You know when it's said that it's the people that matter and not just the project? These guys are it. Great guys, great project with a clear vision.";

	public static final String name_7 = "<b>David Carvalhão</b>, Edge Innovation";
	public static final String test_7 = "New companies are made of ideas, teams and implementations. The idea is solid and, in a way, I would say market tested. So no worries here. The team has shown itself not only up to the challenge and willing to travel, learn and reinvent themselves along the way. A big plus on this one. And the implementation so far seems far better than what I would expect. A project that seems to have everything to succeed! I am looking forward to watch this project mature and kick ass!! :)";

	public static final String name_8 = "<b>Francisco Andrade</b>";
	public static final String test_8 = "UBEOS will be the next game changer in the services business to cliente and complete change the way your search for holidays, it's very usefull and I'm already using it. The team envolved in this project is extremely focused and has a clear vision of what they have to do to make UBEOS a winner!";

	public static final String name_9 = "<b>Pedro Henriques</b>";
	public static final String test_9 = "UBEOS is a promising startup with very knowledgeable founders. The deep understanding of the business allied to the fast-paced ability to change accordingly with market need and feedback makes me believe they will succeed. And if for nothing else, as a consumer myself I would really appreciate that the vision behind UBEOS gains traction and leverages better online services.";

	public static final String name_10 = "<b>Tiago Tomás</b>, Uniplaces";
	public static final String test_10 = "I met UBEOS an early stage, at Startup Weekend Covilhã. From the beginning they were very committed and willing to change the paradigm of scheduling a trip . A dynamic team focused , are sure to leave a fingerprint in the world.";

	public static final String name_11 = "<b>Pedro Santos</b>, Beta-I";
	public static final String test_11 = "Carlos and Hugo have done an amazing job going from nothing to winning startupweekend I facilited to now having a full fledged product launched and working. Most startups founders that I see (and are not few) have made so much progress so fast.";
	
	public static final String name_12 = "<b>Nathalie Aguiar</b>";
	public static final String test_12 = "A young and dinamic Team with an innovator project with an amazing growth potential. They have everything to succeed!";
	
	public static final String name_13 = "<b>Rui Martins</b>";
	public static final String test_13 = "I met Carlos during our course at university. And I always believed that with so much dedication he was going to do something big. And here it is: UBEOS. It is a pleasure to follow the rapid developments of this project.";

	
	@Util
	public static HashMap getAllTestimonials(){
		
		HashMap testimonials = new HashMap();
		testimonials.put(name_1, test_1);
		testimonials.put(name_2, test_2);
		testimonials.put(name_3, test_3);
		testimonials.put(name_4, test_4);
		testimonials.put(name_5, test_5);
		testimonials.put(name_6, test_6);
		testimonials.put(name_7, test_7);
		testimonials.put(name_8, test_8);
		testimonials.put(name_9, test_9);
		testimonials.put(name_10, test_10);
		testimonials.put(name_11, test_11);
		testimonials.put(name_12, test_12);
		testimonials.put(name_13, test_13);
		
		return testimonials;
		
	}
	
	@Util
	public static HashMap get_3_Testimonials(){
		
		HashMap testimonials = new HashMap();
		testimonials = getTestimonial();
				
		return testimonials;
		
	}
	
	public static HashMap getTestimonial(){
		HashMap testimonials = new HashMap();
		
		List<Integer> out = new ArrayList<Integer>();
		out.add(0);
		
		for(int i=1;i<4;i++){
			Random random = new Random();
			int r1 = 0;
			
			while(out.contains(r1)){
				r1 = random.nextInt(13);
			}
			out.add(r1);
			switch (r1) {
			case 1:
				testimonials.put(name_1, test_1);
				break;
			case 2:
				testimonials.put(name_2, test_2);
				break;
			case 3:
				testimonials.put(name_3, test_3);
				break;
			case 4:
				testimonials.put(name_4, test_4);
				break;
			case 5:
				testimonials.put(name_5, test_5);
				break;
			case 6:
				testimonials.put(name_6, test_6);
				break;
			case 7:
				testimonials.put(name_7, test_7);
				break;
			case 8:
				testimonials.put(name_8, test_8);
				break;
			case 9:
				testimonials.put(name_9, test_9);
				break;
			case 10:
				testimonials.put(name_10, test_10);
				break;
			case 11:
				testimonials.put(name_11, test_11);
				break;
			case 12:
				testimonials.put(name_12, test_12);
				break;
			case 13:
				testimonials.put(name_13, test_13);
				break;
			}
			
		}
		

	return testimonials;
	}
	
}
