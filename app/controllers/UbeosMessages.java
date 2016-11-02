package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;

import java.util.ArrayList;
import java.util.List;

import models.Consumer;
import models.MessagesRec;
import models.Proposal;
import models.Provider;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.i18n.Messages;
import play.mvc.Scope.Session;
import play.mvc.With;
import utils.StringUtils;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;
import controllers.deadbolt.Unrestricted;


@With(Deadbolt.class)
public class UbeosMessages extends MyController {

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER})
	public static void inbox() {
		LoggerHelper.info_Logger("Inbox Messages");
		
		User user = UserTypeHelper.getLoggedUser(session);
	
		List<MessagesRec> ubeos_messages = new ArrayList<MessagesRec>();
		if(UserTypeHelper.isConsumer(session)){
			Consumer consumer = Consumer.findByUser(user);
			ubeos_messages = MessagesRec.findByConsumerAndIncoming(consumer);
		} else if(UserTypeHelper.isProvider(session)){
			Provider provider = Provider.findByUser(user);
			ubeos_messages = MessagesRec.findByProviderAndIncoming(provider);
		}
		
		String activeTab = "messagesInbox";
		
		render(ubeos_messages, activeTab);
	}

	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER})
	public static void sent() {
		LoggerHelper.info_Logger("Sent Messages");
	
		User user = UserTypeHelper.getLoggedUser(session);
		
		List<MessagesRec> ubeos_messages = new ArrayList<MessagesRec>();
		if(UserTypeHelper.isConsumer(session)){
			Consumer consumer = Consumer.findByUser(user);
			ubeos_messages = MessagesRec.findByConsumerAndSent(consumer);
		} else if(UserTypeHelper.isProvider(session)){
			Provider provider = Provider.findByUser(user);
			ubeos_messages = MessagesRec.findByProviderAndSent(provider);
		}
		
		String activeTab = "messagesSent";

		renderTemplate("UbeosMessages/inbox.html",ubeos_messages, activeTab);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER})
	public static void messageToConsumer(Long id, Long proposal_id, MessagesRec message ) {
		LoggerHelper.info_Logger( "Creating message to consumer");
		
		TravelRequest travelRequest = TravelRequest.findById(id);
		isValidRequest(travelRequest);

		message = new MessagesRec();
		
		if(proposal_id != null){
			Proposal proposal = Proposal.findById(proposal_id);
			isValidProposal(proposal);
			message.proposal_id = proposal_id;
		}

		User user = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user);
		
		message.travelRequest = travelRequest;
		message.direction = MessagesRec.PROV_2_CONS;
		message.provider = provider;
		message.message = "";
		message.consumer = travelRequest.consumer;
		
		render(message);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER})
	public static void messageConsumerReplyProvider(Long message_id ) {
		LoggerHelper.info_Logger( "Consumer Reply to provider");
		
		MessagesRec old_message = MessagesRec.findById(message_id);
		
		isValidRequest(old_message.travelRequest);

		
		if(old_message.proposal_id != null){
			Proposal proposal = Proposal.findById(old_message.proposal_id);
			isValidProposal(proposal);
		}
		String message_to_reply = old_message.message;

		MessagesRec message = new MessagesRec();
		message = old_message;
		message.reply_to = old_message.id;
		message.message = "";
		message.direction = MessagesRec.CONS_2_PROV;
		renderTemplate("UbeosMessages/messageToProvider.html", message, message_to_reply);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER})
	public static void sendMessageToConsumer(Long id, MessagesRec message) {
		LoggerHelper.info_Logger( "Sending message to consumer");
		
		isValidRequest(message.travelRequest);
		
		if(message.message.length() >= 1000){
			validation.addError("message", "&{message.too.long}");
			flash.error(Messages.get("message.too.long"));
		
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
		
			render("@messageToConsumer",id, message);
		}
		
		if(utils.CommUtils.checkValidMessage(message.message)){

			message.isNew = true;
			
			if(!StringUtils.isNullOrEmpty(message.message)){
				message.message = message.message.replaceAll("\n", "<br>");
			}
			
			message.save();
			message.refresh();
			
			UbeosMailer.sendMessageConsumer(message.consumer.user, message);

			flash.success(Messages.get("views.message.sent"));
			LoggerHelper.info_Logger( "Message Sent");
		} else {
			validation.addError("message", "&{invalid.message}");
			flash.error(Messages.get("scaffold.validation.contacts"));
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
			
			render("@messageToConsumer",id, message);
		}
		
		inbox();
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER})
	public static void messageToProvider(Long id, Long proposal_id, MessagesRec message_in) {
		LoggerHelper.info_Logger( "Creating message to provider");

		TravelRequest travelRequest = TravelRequest.findById(id);
		isValidRequest(travelRequest);

		MessagesRec message = new MessagesRec();
		if(proposal_id != null){
			Proposal proposal = Proposal.findById(proposal_id);
			isValidProposal(proposal);
			message.provider = proposal.provider;
		} else {
			message.provider = message_in.provider;
		}
		
		User user = UserTypeHelper.getLoggedUser(session);
		Consumer consumer = Consumer.findByUser(user);
		
		if(consumer != travelRequest.consumer){
			LoggerHelper.info_Logger( "Tried to send message for request " + id + " when it is forbidden");
			notFound();
		}
		message.travelRequest = travelRequest;
		message.direction = MessagesRec.CONS_2_PROV;
		message.consumer = consumer;
		message.proposal_id = proposal_id;
				
		render(message);

	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER})
	public static void messageProviderReplyConsumer(Long message_id ) {
		LoggerHelper.info_Logger( "Provider Reply to Consumer");
		
		MessagesRec old_message = MessagesRec.findById(message_id);
		
		isValidRequest(old_message.travelRequest);

		
		if(old_message.proposal_id != null){
			Proposal proposal = Proposal.findById(old_message.proposal_id);
			isValidProposal(proposal);
		}
		
		String message_to_reply = old_message.message;

		MessagesRec message = new MessagesRec();
		message = old_message;
		message.message = "";
		message.reply_to = old_message.id;
		message.direction = MessagesRec.PROV_2_CONS;
		
		renderTemplate("UbeosMessages/messageToConsumer.html", message, message_to_reply);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER})
	public static void sendMessageToProvider(Long id, MessagesRec message) {
		LoggerHelper.info_Logger( "Sending message to provider");
		
		isValidRequest(message.travelRequest);
		
		if(message.message.length() >= 1000){
			validation.addError("message", "&{message.too.long}");
			flash.error(Messages.get("message.too.long"));
		
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
		
			render("@messageToProvider",id,null, message);
		}
		
		if(message.proposal_id != null){
			Proposal proposal = Proposal.findById(message.proposal_id);
			isValidProposal(proposal);
		}
		
		if(utils.CommUtils.checkValidMessage(message.message)){

			message.isNew = true;
			
			if(!StringUtils.isNullOrEmpty(message.message)){
				message.message = message.message.replaceAll("\n", "<br>");
			}
			
			message.save();
			message.refresh();

			UbeosMailer.sendMessageProvider(message.provider.user, message);
			
			flash.success(Messages.get("views.message.sent"));
			LoggerHelper.info_Logger( "Message Sent");
		} else {
			validation.addError("message", "&{invalid.message}");
			flash.error(Messages.get("scaffold.validation.contacts"));
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
			
			render("@messageToProvider",id,null, message);
		}
		
		inbox();
		
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER})
	public static void readMessage(Long id) {

		User user = UserTypeHelper.getLoggedUser(session);

		if(user == null){
			LoggerHelper.debug_Logger("exit_1");
			renderHtml("");
		}
		
		MessagesRec message = MessagesRec.findById(id);

		if(UserTypeHelper.isConsumer(session)){
		
			 if(message.direction == MessagesRec.PROV_2_CONS){
				Consumer consumer = Consumer.findByUser(user);
				if(consumer == null || message.consumer != consumer){
					LoggerHelper.debug_Logger("exit_3");
					renderHtml("");
				}
				message.isNew = false;
				message.save();
				LoggerHelper.debug_Logger("exit_4");
				renderHtml("");
			}
		} else if(UserTypeHelper.isProvider(session)){
			if(message.direction == MessagesRec.CONS_2_PROV){
				Provider provider = Provider.findByUser(user);
				if(provider == null || message.provider != provider){
					LoggerHelper.debug_Logger("exit_5");
					renderHtml("");
				}
				message.isNew = false;
				message.save();
				
			} 
		}
	}
	
}