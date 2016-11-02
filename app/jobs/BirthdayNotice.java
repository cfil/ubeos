package jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;


//@On("cron.time.daily.seven.am")
public class BirthdayNotice extends Job {

//	@Override
//	public void doJob() throws Exception {
//		Logger.info("[ *** JOB: BIRTHDAY MAIL *** ] - Start");
//
//		Date today = new Date();
//		Calendar cal_today = Calendar.getInstance();
//		cal_today.setTime(today);
//	    int b_month = cal_today.get(Calendar.MONTH);
//	    int b_day = cal_today.get(Calendar.DAY_OF_MONTH);
//		
//		UserRole userRole = UserRole.findByName(UserRole.CONSUMER);
//		List<User> users = User.findByUserRole(userRole);
//				
//		for (User user : users) {
//			if(user.birthDate != null){
//				Calendar birth = Calendar.getInstance();
//			    birth.setTime(user.birthDate);
//
//			    int month = birth.get(Calendar.MONTH);
//			    int day = birth.get(Calendar.DAY_OF_MONTH);
//
//			    if(month==b_month && day == b_day){
//			    	Logger.info("[ *** JOB: BIRTHDAY MAIL *** ] - Sending mail to " + user.email);
//			    	UbeosMailer.sendBirthdayMail(user);			    	
//			    }
//			    
//			}
//		}
//
//		
//		Logger.info("[ *** JOB: BIRTHDAY MAIL *** ] - END");		
//	}
	
}