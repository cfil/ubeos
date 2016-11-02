package deadbolt;

import java.util.List;
import java.util.Map;

import models.User;
import models.deadbolt.AccessResult;
import play.Logger;
import controllers.Security;
import controllers.deadbolt.RestrictedResourcesHandler;

public class MyRestrictedResourcesHandler implements RestrictedResourcesHandler {

	@Override
	public AccessResult checkAccess(List<String> resourceNames, Map<String, String> resourceParameters) {
		User currentUser = Security.currentUser();
	
		if(currentUser != null){
			String role = currentUser.userRole.name;
					
			if (resourceNames.contains(role)) {
				return AccessResult.ALLOWED;
			}
			
		} else {
			Logger.debug("Current User is null!! Please review user session and his authentication!");
		}

		return AccessResult.DENIED;
	}
}
