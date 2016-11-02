package notifiers;

import java.util.Date;
import java.util.HashMap;

import models.Activation;
import models.Billing;
import models.Company;
import models.Consumer;
import models.DeleteMotive;
import models.MessagesRec;
import models.Proposal;
import models.RejectMotive;
import models.TravelRequest;
import models.User;
import play.Play;
import play.data.binding.ParamNode.RemovedNode;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Mailer;
import utils.StringUtils;

public class UbeosMailer extends Mailer {

	protected static String FROM_ADMIN = Play.configuration.getProperty("application.email_from.admin");
	protected static String FROM = Play.configuration.getProperty("application.email_from");
	protected static String SUPPORT_EMAIL = Play.configuration.getProperty("application.email_support");
	protected static String SUBJECT_PREFIX = Play.configuration.getProperty("application.email_subject_prefix") + " ";


	public static void activationConfirmation(User user, Activation activation) {
		
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.confirmation"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, activation, "teste");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void recoverPassword(User user, Activation activation) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}

		setSubject(SUBJECT_PREFIX + Messages.get("email.recover.password"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, activation, "teste");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void proposalRejected(User user, Proposal proposal, String rejeted, Long motive_id, String message) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.proposal.rejected"));
		addRecipient(user.email);
		setFrom(FROM);
		
		RejectMotive motive = RejectMotive.findById(motive_id);
		
		boolean printMessage = true;
		if(StringUtils.isNullOrEmpty(message)){
			printMessage = false;
		}
		
		send(user, proposal, rejeted, motive, message, printMessage);
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void proposalRejectedByDelete(User user, Proposal proposal, String rejeted, Long motive_id, String message) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.proposal.rejected"));
		addRecipient(user.email);
		setFrom(FROM);
		
		DeleteMotive motive = DeleteMotive.findById(motive_id);
		
		boolean printMessage = true;
		if(StringUtils.isNullOrEmpty(message)){
			printMessage = false;
		}
		
		send(user, proposal, rejeted, motive, message, printMessage);
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	
	public static void proposalAccepted(User user, Proposal proposal,Billing billing, String string) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.proposal.accepted"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, proposal, string,billing, "teste");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}

	public static void providerPending(User user) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.pending.approval"));
		addRecipient(user.email);
		addRecipient(FROM);
		setFrom(FROM);
		send(user, "pending");
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}

	public static void providerPendingAdded(User user) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
	
		setSubject(SUBJECT_PREFIX + Messages.get("email.pending.approval"));
		addRecipient(FROM);
		setFrom(FROM);
		send(user, "pending");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void paymentInstructionsPending(Billing billing) {
		String currentLang = Lang.current.get();
		

		User user = billing.proposal.request.consumer.user;
		Proposal proposal = billing.proposal;
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.reserve.pending"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, billing, proposal, "instructions");
		if(currentLang!=null){
			Lang.change(currentLang);
		}	
	}

	public static void requestProviderFeedback(Proposal proposal) {
		String currentLang = Lang.current.get();
		

		User provider = proposal.provider.user;
		User consumer = proposal.request.consumer.user;
		if(provider.lang!=null){
			Lang.change(provider.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.feedback"));
		addRecipient(provider.email);
		setFrom(FROM);
		send(provider, consumer, proposal, "pending");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}

	public static void requestConsumerFeedback(Proposal proposal) {
		String currentLang = Lang.current.get();
		

		User provider = proposal.provider.user;
		User consumer = proposal.request.consumer.user;
		if(consumer!=null){
			Lang.change(consumer.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.feedback"));
		addRecipient(consumer.email);
		setFrom(FROM);
		send(provider, consumer, proposal, "pending");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}

	public static void notifyProviderNewRequest(User user,	TravelRequest travelRequest, String string) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.new.request"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, travelRequest, "new request");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void notifyConsumerNewProposal(User user,	Proposal proposal) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		
		setSubject(SUBJECT_PREFIX + Messages.get("email.new.proposal"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, proposal, "new proposal");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void proposalBeated(User user, Proposal proposal, boolean price, boolean rating){
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		
		setSubject(SUBJECT_PREFIX + Messages.get("email.proposal.beated"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, proposal, price, rating, "beat proposal");
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void sendMessageConsumer(User user, MessagesRec message){
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		
		setSubject(SUBJECT_PREFIX + Messages.get("email.consumer.message"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, message);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}
	
	public static void sendMessageProvider(User user, MessagesRec message){
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		
		setSubject(SUBJECT_PREFIX + Messages.get("email.provider.message"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, message);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
	}

	public static void providerAdded(User user, Activation activation) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		
		setSubject(SUBJECT_PREFIX + Messages.get("email.provider.added"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, activation);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}

	public static void proposalCancelByExpired(User user, Proposal proposal, String canceled, Long motive_id) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		setSubject(SUBJECT_PREFIX + Messages.get("email.proposal.canceled"));
		addRecipient(user.email);
		setFrom(FROM);
		
		RejectMotive motive = RejectMotive.findById(motive_id);
				
		send(user, proposal, canceled, motive);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}
	
	public static void sendError(User user, String error, Date date) {
		
		setSubject(SUBJECT_PREFIX + Messages.get("ERRO"));
		addRecipient("ubeos@ubeos.com");
		setFrom(FROM);
				
		send(user, error, date);
		
		
	}

	public static void requestNoProposal(User user) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
	
		setSubject(SUBJECT_PREFIX + Messages.get("email.request.no.proposal"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}

	public static void registerNoRequest(User user) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
	
		setSubject(SUBJECT_PREFIX + Messages.get("email.register.no.request"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}
	
	public static void alertUserAcceptProposal(User user, TravelRequest travelRequest) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
	
		setSubject(SUBJECT_PREFIX + Messages.get("email.alert.user.accept"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, travelRequest);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}
	
	public static void alertUserNoProposal(User user, TravelRequest travelRequest) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
	
		setSubject(SUBJECT_PREFIX + Messages.get("email.alert.user.no.proposal"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, travelRequest);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}
	
	public static void closeRequestRejectedAllProposals(User user, TravelRequest travelRequest) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
	
		setSubject(SUBJECT_PREFIX + Messages.get("email.alert.user.no.proposal"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, travelRequest);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}

	public static void notifyConsumerEditedProposal(User user, Proposal proposal) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
		
		setSubject(SUBJECT_PREFIX + Messages.get("email.edited.proposal"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, proposal, "edited proposal");
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}

	public static void alertUserForAcceptancePeriod(User user, TravelRequest travelRequest) {
		String currentLang = Lang.current.get();
		if(user.lang!=null){
			Lang.change(user.lang);
		}
	
		setSubject(SUBJECT_PREFIX + Messages.get("email.alert.for.acceptance.period"));
		addRecipient(user.email);
		setFrom(FROM);
		send(user, travelRequest);
		
		if(currentLang!=null){
			Lang.change(currentLang);
		}
		
	}
	
	public static void sendReport(User user_reported, String message, Date date, User reporter) {
		
		setSubject(SUBJECT_PREFIX + Messages.get("REPORT"));
		addRecipient(FROM);
		setFrom(FROM);
				
		send(user_reported, message, date, reporter);
		
		
	}

	public static void sendContactUs(String name, String email, String subject,	String message, Date date) {
		
		setSubject(SUBJECT_PREFIX + Messages.get("CONTACT US")+ " - "+subject);
		addRecipient(FROM);
		setFrom(FROM);
				
		send(name, email, message, date);
		
	}
	
	public static void sendContactUsOrigin(String name, String email, String subject,	String message, Date date) {
		
		setSubject(SUBJECT_PREFIX + Messages.get("thank.you.for.contact")+ " - "+subject);
		addRecipient(email);
		setFrom(FROM);
				
		send(name, email, message, date);
		
	}

	public static void sendMailWithStats(String email,HashMap stats, long total_users, long total_requests, long total_proposals, long new_users, long new_requests, long new_proposals) {
		
		Date date = new Date();
		
		setSubject(SUBJECT_PREFIX + "UBEOS STATS");
		addRecipient(email);
		setFrom(FROM);
				
		send(stats, date, total_users, total_requests, total_proposals, new_users, new_requests, new_proposals);
		
	}

	public static void sendPromoCode(String email, Consumer consumer) {
		
		setSubject(SUBJECT_PREFIX + Messages.get("invitation.sent"));
		addRecipient(email);
		setFrom(FROM);
				
		send(email, consumer);
		
	}

	public static void sendBirthdayMail(User user) {
		
		setSubject(SUBJECT_PREFIX + Messages.get("happy.birthday"));
		addRecipient(user.email);
		setFrom(FROM);
				
		send(user);
		
	}

	public static void notifyAdminAccepted(Proposal proposal) {
		
		setSubject(SUBJECT_PREFIX + "SOLD :)" );
		addRecipient(FROM_ADMIN);
		setFrom(FROM);
				
		send(proposal);
		
	}

	public static void sendLastMinutePromoProvider(String firstname, String lastname, String email, Long phone) {
		
		Company company = Company.findByName("Ciga-M Viagens");
		
		setSubject(SUBJECT_PREFIX + Messages.get("user.interested.last.minute") );

			addRecipient("geral@ciga-m.com"); // TROCAR PARA A AGÊNCIA A TESTAR

		addRecipient(FROM_ADMIN); // Envia para UBEOS

		setFrom(FROM);
				
		send(firstname, lastname, email, phone, company);
		
	}
	
	public static void sendLastMinutePromoProvider2(String firstname, String lastname, String email, Long phone) {
		
		Company company = Company.findByName("Ciga-M Viagens");
		
		setSubject(SUBJECT_PREFIX + Messages.get("user.interested.last.minute") );

			addRecipient("geral@ciga-m.com"); // TROCAR PARA A AGÊNCIA A TESTAR

		addRecipient(FROM_ADMIN); // Envia para UBEOS

		setFrom(FROM);
				
		send(firstname, lastname, email, phone, company);
		
	}

	public static void welcomeFromFb(User user) {
		
		setSubject(SUBJECT_PREFIX + Messages.get("mail.fb.welcome") );
		addRecipient(user.email);
		setFrom(FROM);
				
		send(user);
		
	}
	
}
