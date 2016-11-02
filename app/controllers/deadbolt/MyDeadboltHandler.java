package controllers.deadbolt;

import java.lang.reflect.Method;
import java.util.List;

import models.deadbolt.RoleHolder;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import play.utils.Java;
import controllers.Application;
import controllers.Security;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.DeadboltHandler;
import controllers.deadbolt.ExternalizedRestrictionsAccessor;
import controllers.deadbolt.RestrictedResourcesHandler;
import controllers.deadbolt.Unrestricted;
import deadbolt.MyRestrictedResourcesHandler;

public class MyDeadboltHandler extends Controller implements DeadboltHandler {

	@Override
	public void beforeRoleCheck() {

		if (!checkIfUnrestricted(request)) {

			// Grafic Interface
			// Check if the user is connected
			try {
				Security.checkAccess();
			} catch (Throwable e) {
				Logger.debug("Login error...");
			}
			Security.changeLocaleIfDefined();
			notFoundIfNull(Security.currentUser(),
					"Please review your access settings or request url!");
		}
	}


	@Override
	public void onAccessFailure(String controllerClassName) {
		Logger.debug("Hit an authorisation issue when trying to access [%s]",controllerClassName);
		flash.error(Messages.get("flash.notice.access_denied"));
		Application.index();
		Deadbolt.forbidden();
	}

	@Override
	public RestrictedResourcesHandler getRestrictedResourcesHandler() {
		return new MyRestrictedResourcesHandler();
	}

	@Override
	public ExternalizedRestrictionsAccessor getExternalizedRestrictionsAccessor() {
		return null;
	}

	private boolean checkIfUnrestricted(Http.Request request) {
		List<Method> methods = Java.findAllAnnotatedMethods(
				Controller.getControllerClass(), Unrestricted.class);
		for (Method method : methods) {
			if (method.toString().contains(request.action)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public RoleHolder getRoleHolder() {
		// TODO Auto-generated method stub
		return null;
	}

}
