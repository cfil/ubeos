package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import models.Activation;
import models.ActivationMotive;
import models.Avatar;
import models.Consumer;
import models.Country;
import models.Provider;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.Logger;
import play.Play;
import play.data.validation.Valid;
import play.data.validation.Validation.ValidationResult;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.libs.Crypto;
import play.libs.Crypto.HashType;
import play.libs.Images;
import play.mvc.With;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class Users extends MyController {
	
	private static final int MIN_PASSWORD_SIZE = 6;

	@Unrestricted
	public static void activate(String token){
		LoggerHelper.info_Logger( "Activating user");
		Activation activation = Activation.find("byToken", token).first();
		
		if(activation == null){
			LoggerHelper.info_Logger( "Tried to activate unexistent or disabled token : " + token);
			notFound();
		}
		
		User user = User.findById(activation.user);
		
		if(user.userRole.name.equals(UserRole.CONSUMER)){
			
			user.enable = true;
			user.save();
			
			Consumer consumer = Consumer.findByUser(user);
			TravelRequest travelRequest = TravelRequest.find("byConsumer",consumer).first();
			if( travelRequest != null ) {
				travelRequest.enable = true;
				travelRequest.save();
			}
			
		} else if (user.userRole.name.equals(UserRole.PROVIDER)){
			UbeosMailer.providerPending(user);
		}
		activation.delete();
		
		flash.success(Messages.get("views.pre.register.notice", "activation"));
		Application.index();
	}
	
	@Unrestricted
	public static void forgotPassword(){
		
		isUserLoggedIn();
		flash.remove(Security.RENDER_ERROR);
		
		Logger.debug("[Users.forgotPassword]");
		LoggerHelper.info_Logger( "Forgot Password form");
		render();
	}
	
	@Unrestricted
	public static void recoverPassword(@Valid String email){
		LoggerHelper.info_Logger( "Forgot Password form submit mail: " + email);
		
		isUserLoggedIn();
		
		validation.required(email);
		if(validation.hasErrors()){
			flash.error(Messages.get("validation.required"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			renderTemplate("Users/forgotPassword.html", email);
		}
		
		validation.email(email);
		if(validation.hasErrors()){
			flash.error(Messages.get("validation.email.format"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			renderTemplate("Users/forgotPassword.html", email);
		}
				
		User user = User.findByEmail(email);
		
		if(user!=null){
			String token = User.generateToken();
			ActivationMotive motive = ActivationMotive.findByMotive(ActivationMotive.RECOVERY);
			Activation activation = new Activation(true, token, motive);
			activation.user = User.findByEmail(user.email).id;
			activation.save();
			UbeosMailer.recoverPassword(user, activation);
		} else {
			validation.addError("email", "&{validation.email.notexist}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			flash.error(Messages.get("validation.error.notexist.mail"));
			renderTemplate("Users/forgotPassword.html", email);
		}
		
		flash.success(Messages.get("email.recover.password.sent"));
		flash.put("success", "scaffold.recover.success");	
		Security.login();
	}

	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER})
	public static void saveChangePass(Long id, String oldPassword, String passwordNew, String passwordConfirm) {
		LoggerHelper.info_Logger( "Change password form");
		
		User user = User.findById(id);
		if(user == null){
			LoggerHelper.info_Logger( "Change password for unexistent user");
			notFound();
		}
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(logged.id != user.id){
			LoggerHelper.info_Logger( "Change password for unexistent user");
			notFound();
		}
		
		
		ValidationResult oldP = validation.required(oldPassword);
		if(oldP.error != null){
			validation.addError("oldPassword", "&{validation.required}");
		}
		ValidationResult passN = validation.required(passwordNew);
		if(passN.error != null){
			validation.addError("passwordNew", "&{validation.required}");
		}
		ValidationResult passC = validation.required(passwordConfirm);
		if(passC.error != null){
			validation.addError("passwordConfirm", "&{validation.required}");
		}
		if(validation.hasErrors()){
			flash.error(Messages.get("scaffold.validation.required"));
			List<Country> countries = Country.findAll();
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			user.refresh();
			flash.put(Security.RENDER_ERROR, true);
			if(user.userRole.name.equals(UserRole.CONSUMER)){
				Consumer consumer = Consumer.findByUser(user);
				renderTemplate("Consumers/edit.html", user, consumer, countries);
			} else if(user.userRole.name.equals(UserRole.PROVIDER)){
				renderTemplate("Providers/edit.html", user);
			} else {
				Application.index();
			}
		}
		
		
		if(passwordNew.length() < MIN_PASSWORD_SIZE){
			flash.error(Messages.get("validation.user.pass.size"));
			validation.addError("passwordNew", "&{validation.user.pass.size}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			
			flash.put(Security.RENDER_ERROR, true);
			List<Country> countries = Country.findAll();
			if(user.userRole.name.equals(UserRole.CONSUMER)){
				renderTemplate("Consumers/edit.html", user,  countries);
			} else if(user.userRole.name.equals(UserRole.PROVIDER)){
				renderTemplate("Providers/edit.html", user);
			} else {
				Application.index();
			}
		}
		
		String OLD = Crypto.passwordHash(oldPassword, HashType.SHA512);
		
		if( !OLD.equals(user.hashPassword) ){
			flash.error(Messages.get("scaffold.validation.pass.invalid"));
			user.refresh();
			List<Country> countries = Country.findAll();
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			if(user.userRole.name.equals(UserRole.CONSUMER)){
				renderTemplate("Consumers/edit.html", user,  countries);
			} else if(user.userRole.name.equals(UserRole.PROVIDER)){
				renderTemplate("Providers/edit.html", user);
			} else {
				Application.index();
			}
		}
		
		if(User.hashPassword(passwordNew).equals(user.hashPassword)){
			flash.error(Messages.get("scaffold.validation.pass.invalid"));
			user.refresh();
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			List<Country> countries = Country.findAll();
			if(user.userRole.name.equals(UserRole.CONSUMER)){
				renderTemplate("Consumers/edit.html", user,  countries);
			} else if(user.userRole.name.equals(UserRole.PROVIDER)){
				renderTemplate("Providers/edit.html", user);
			} else {
				Application.index();
			}
		}
		
		validation.equals(passwordNew, passwordConfirm);
		if(validation.hasErrors()){
			flash.put(Security.RENDER_ERROR, true);
			List<Country> countries = Country.findAll();
			flash.error(Messages.get("scaffold.validation"));
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			if(user.userRole.name.equals(UserRole.CONSUMER)){
				renderTemplate("Consumers/edit.html", user,  countries);
			} else if(user.userRole.name.equals(UserRole.PROVIDER)){
				renderTemplate("Providers/edit.html", user);
			} else {
				Application.index();
			}
		}
		
		user.hashPassword = User.hashPassword(passwordNew);
		user.save();

		
		flash.success(Messages.get("scaffold.updated", "Password"));
		flash.put(Security.RENDER_ERROR, true);
		if(user.userRole.name.equals(UserRole.CONSUMER)){
			Consumer consumer = Consumer.findByUser(user);
			Consumers.edit(consumer.alias);		
		} else if(user.userRole.name.equals(UserRole.PROVIDER)){
			Providers.edit(id);		
		} else {
			Application.index();
		}
		
	}
	
	@RestrictedResource(name = {UserRole.ADMIN})
	public static void enable(Long id) {
		LoggerHelper.info_Logger("Enabling user");
		
		User user = User.findById(id);
		if(user.enable == false){
			user.enable=true;
			user.blocked=false;
			user.save();
		}
			
		Application.index();
	}
	
	@RestrictedResource(name = {UserRole.ADMIN})
	public static void disable(Long id) {
		LoggerHelper.info_Logger("Disabling user");
		
		User user = User.findById(id);
		if(user.enable == true){
			user.enable=false;
			user.blocked=true;
			user.save();
		}
			
		Application.index();
	}

	@Unrestricted
	public static void recoverPasswordForm(String token) {
		LoggerHelper.info_Logger( "recoverPasswordForm submit");
		flash.remove(Security.RENDER_ERROR);
		
		isUserLoggedIn();
		
		Activation activation = Activation.find("byToken", token).first();
		if(activation == null){
			LoggerHelper.info_Logger("Tried to change password with unexistent or disabled token : " + token );
			notFound();
		}
		
		if(!activation.enable){
			LoggerHelper.info_Logger("Tried to change password with unexistent or disabled token : " + token );
			notFound();
		}
		
		renderTemplate("Users/passwordReset.html", token);
	}
	
	
	@Unrestricted
	public static void recoverPasswordChange(String token, String password, String passwordConfirm){
		LoggerHelper.info_Logger("Recover Password submit");
		
		isUserLoggedIn();
		
		Activation activation = Activation.find("byToken", token).first();
		
		if(activation.equals(null) || !activation.enable){
			LoggerHelper.info_Logger("Tried to change password with unexistent or disabled token : " + token );
			notFound();
		}
		
		validation.required(password);
		validation.required(passwordConfirm);
		if(validation.hasErrors()){
			flash.error(Messages.get("scaffold.validation.required"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			renderTemplate("Users/passwordReset.html", token);
		}
		
		validation.equals(password, passwordConfirm);
		if(validation.hasErrors()){
			flash.error(Messages.get("validation.passwords.mismatch"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			renderTemplate("Users/passwordReset.html", token);
		}
		
		if(password.length() < MIN_PASSWORD_SIZE){
			flash.error(Messages.get("validation.user.pass.size"));
			validation.addError("passwordNew", "&{validation.user.pass.size}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			
			flash.put(Security.RENDER_ERROR, true);
			renderTemplate("Users/passwordReset.html", token);
		}
		
		User user = User.findById(activation.user);
		user.hashPassword = user.hashPassword(password);
		user.save();
		
		activation.delete();
		
		flash.success(Messages.get("scaffold.validation.pass.changed"));
		Application.index();
	}
		
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER})
	public static void Image(Long id){
		LoggerHelper.info_Logger( "Render Image...");
		User user = User.findById(id);
		Consumer consumer = Consumer.findByUser(user);
		Avatar avatar = Avatar.findByUser(user);
		if(avatar == null  && consumer!= null){
			File f = null;
			
			if(consumer != null){
					f = new File(Play.applicationPath + "/public/images/avatar_male.png");
			}
			renderBinary(f);
		} else {
			
			if( user.userRole.equals(UserRole.findByName(UserRole.PROVIDER)) ){
				Provider provider = Provider.findByUser(user);
				User owner = User.findById(provider.company.owner);
				response.setContentTypeIfNotSet(owner.avatar.avatar.type());
				renderBinary(owner.avatar.avatar.get());
			} else {
				if(avatar != null){
					response.setContentTypeIfNotSet(avatar.avatar.type());
					renderBinary(avatar.avatar.get());
				}
			}
		}
	}

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER})
	public static void updateAvatar(Long id, Blob avatar) {
		LoggerHelper.info_Logger( "Updating Image");
		User user = User.findById(id);
		
		if(user == null){
			LoggerHelper.info_Logger( "Updating Image for not logged user");	
			notFound();
		}
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(logged.id != user.id && !UserTypeHelper.isAdmin(session)){
			LoggerHelper.info_Logger( "Updating Image for not other user");
			notFound();
		}
		
		InputStream is;
		try {
			
			Image image = ImageIO.read(avatar.getFile());
			
		    if (image == null) {
		    	LoggerHelper.info_Logger("tried to upload a file that is not an image");
		        flash.error(Messages.get("images.upload.not.image", "Avatar"));
		        flash.put(Security.RENDER_ERROR, true);
		        if(user.userRole.name.equals(UserRole.CONSUMER)){
		        	Consumer consumer = Consumer.findByUser(user);
					Consumers.edit(consumer.alias);		
				} else if(user.userRole.name.equals(UserRole.PROVIDER)){
					Providers.edit(id);		
				} else {
					Application.index();
				}
		    }
			
//		    String extension = "";
//		    int i = avatar.getFile().getName().lastIndexOf('.');
//		    
//		    if (i > 0) {
//		        extension = avatar.getFile().getName().substring(i+1);
//		    }
//		    
//		    if( extension!="jpg" && extension!="jpeg" && extension!="png"){
//		    	flash.error(Messages.get("images.upload.wrong.image.format", "Avatar"));
//		    	flash.put(Security.RENDER_ERROR, true);
//		        if(user.userRole.name.equals(UserRole.CONSUMER)){
//					Consumers.edit(id);		
//				} else if(user.userRole.name.equals(UserRole.PROVIDER)){
//					Providers.edit(id);		
//				} else {
//					Users.edit(id);
//				}
//		    }
		    
			
			BufferedImage bimg = ImageIO.read(avatar.getFile());
			int h = bimg.getHeight();
			int w = bimg.getWidth();
			BufferedImage dest;
			if(h-w < 0){
				int excess = w-h;
				int margins = excess/2;
				dest = bimg.getSubimage(margins, 0, h, h);
			} else {
				int excess = h-w;
				int margins = excess/2;
				dest = bimg.getSubimage(0, margins, w, w);
			}
			File src = new File(Play.applicationPath + "/public/images/tmp/src.jpg");
			File tmp = new File(Play.applicationPath + "/public/images/tmp/tmp.jpg");
			ImageIO.write(dest, "jpg", src);
			Images.resize(src, tmp, 400, 400);
			
			is = new FileInputStream(tmp);
			Blob b = new Blob();
			b.set(is, "image/jpg");

			Avatar oldAvatar = Avatar.findByUser(user);
			if(oldAvatar != null){
				user.avatar = null;
				user.save();
				user.refresh();
				oldAvatar.delete();
			}
			
			Avatar newAvatar = new Avatar(user, b);
			newAvatar.save();
			newAvatar.refresh();
			
			user.avatar = newAvatar;
			user.save();
			
		} catch (Exception e) {
			flash.error(Messages.get("images.upload.error", "Avatar"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.error_Logger(e.getMessage());
			if(user.userRole.name.equals(UserRole.CONSUMER)){
				Consumer consumer = Consumer.findByUser(user);
				Consumers.edit(consumer.alias);		
			} else if(user.userRole.name.equals(UserRole.PROVIDER)){
				Providers.edit(id);		
			} else {
				Application.index();
			}
		} 
		flash.success(Messages.get("scaffold.updated", "Avatar"));
		
		if(user.userRole.name.equals(UserRole.CONSUMER)){
			Consumer consumer = Consumer.findByUser(user);
			Consumers.edit(consumer.alias);		
		} else if(user.userRole.name.equals(UserRole.PROVIDER)){
			Providers.edit(id);		
		} else {
			Application.index();
		}
	}

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.PROVIDER})
	public static void report(Long id){
		LoggerHelper.info_Logger( "Reporting user: " +id);
		
		User user_reported = User.findById(id);
		if(user_reported == null){
			notFound();
		}
		
		User user_logged = UserTypeHelper.getLoggedUser(session);
		if(user_logged == user_reported){
			notFound();
		}
				
		render(user_reported);
		
	}
	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.PROVIDER})
	public static void reportSave(Long id,  String message){
		LoggerHelper.info_Logger( "Saving Report for user: " +id);
		
		User user_reported = User.findById(id);
		if(user_reported == null){
			notFound();
		}
		
		User user_logged = UserTypeHelper.getLoggedUser(session);
		if(user_logged == user_reported){
			notFound();
		}
		
		if(message.length() > 300){
			validation.addError("message", "&{invalid.message}");
			flash.error(Messages.get("scaffold.validation.report.size"));
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
			
			render("@report",id, message);
		}
		
		// Get Date and Time of the error
		Date date = new Date();
		
		UbeosMailer.sendReport(user_reported, message, date, user_logged);
		
		flash.put(Security.RENDER_ERROR, true);
		flash.success(Messages.get("scaffold.report.user.success"));
		Application.index();
		
	}
	
}
