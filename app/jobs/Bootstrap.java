package jobs;

import java.util.List;

import models.Experience;
import models.User;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import play.vfs.VirtualFile;
import controllers.Application;

@OnApplicationStart
public class Bootstrap extends Job {
	@Override
	public void doJob() {
		if(User.count() == 0 && !"test".equals(Play.id)) {
			VirtualFile appRoot = VirtualFile.open(Play.applicationPath);
			Play.javaPath.add(0, appRoot.child("conf"));
			Fixtures.loadModels("admin.yml");
		}
//		ELSE IF(USER.COUNT() == 0 && "TEST".EQUALS(PLAY.ID)) {
//			VIRTUALFILE APPROOT = VIRTUALFILE.OPEN(PLAY.APPLICATIONPATH);
//			PLAY.JAVAPATH.ADD(0, APPROOT.CHILD("CONF"));
//			FIXTURES.LOADMODELS("ADMIN.YML");
//		}
	}

}
