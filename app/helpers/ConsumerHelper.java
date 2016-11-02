package helpers;

import java.util.UUID;

import models.Consumer;
import play.mvc.Util;
import utils.Hashids;

public class ConsumerHelper {

	@Util
	public static String generatePromoCode(Consumer consumer){
		
		Hashids hashids = new Hashids("user promo code");
		String uuid = hashids.encode(consumer.id, consumer.created_at.getTime());
		return uuid;
	}
	
}
