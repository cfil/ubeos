package helpers;

import java.util.List;

import models.Consumer;
import models.MessagesRec;
import models.Proposal;
import models.Provider;
import models.RequestState;
import models.TravelRequest;
import models.User;
import play.mvc.Util;

public class MessagesHelper {

	@Util
	public static MessagesRec getOriginal(MessagesRec message){
		
		if(message.reply_to != null) {
			MessagesRec original = MessagesRec.findById(message.reply_to);
			return original;
		} else {
			return null;
		}
		
		
	}
	
	@Util
	public static int getNewMessages(User user){
		int total = 0;
		
		Consumer consumer = Consumer.findByUser(user);
		Provider provider = Provider.findByUser(user);
		
		if(provider!=null){
			List<MessagesRec> messages = MessagesRec.findByProviderAndIncomingAndIsNew(provider);
			total = messages.size();
		} else if (consumer != null){
			List<MessagesRec> messages = MessagesRec.findByConsumerAndIncomingAndIsNew(consumer);
			total = messages.size();
		}
		return total;
		
	}
}
