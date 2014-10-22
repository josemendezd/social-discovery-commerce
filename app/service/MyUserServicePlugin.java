package service;

import models.User;
import play.Application;
import play.Logger;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import com.feth.play.module.pa.service.UserServicePlugin;

public class MyUserServicePlugin extends UserServicePlugin {

	public MyUserServicePlugin(final Application app) {
		super(app);
	}

	@Override
	public Object save(final AuthUser authUser) {
		final boolean isLinked = User.existsByAuthUserIdentity(authUser);
		if (!isLinked) {
			if (authUser instanceof EmailIdentity) {
				final EmailIdentity identity = (EmailIdentity) authUser;
				String email = identity.getEmail();
				if(User.findByEmail(email)!=null){
					//the user is already registered. Should login w/ old account.
					Logger.info("email already exists");
					
					User.addLinkedAccount( User.findByEmail(email), authUser);
					
					
					return  User.findByEmail(email);
				}
				
				
			}
			if (authUser instanceof NameIdentity) {
				final NameIdentity identity = (NameIdentity) authUser;
				String name = identity.getName();
				if(User.findByUserName(name)!=null){
					//the name is used by another user should use a different name
				//	Logger.info("name already exists");
					 return User.createDifferentUserName(authUser).id;
				}
				
			}			
		
			
		 return User.create(authUser).id;
		
		} else {
			// we have this user already, so return null
			return null;
		}
		
	}

	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Caching might be a good idea here...
		// ...and dont forget to sync the cache when users get deactivated/deleted
		final User u = User.findByAuthUserIdentity(identity);
		if(u != null) {
			return u.id;
		} else {
			return null;
		}
	}

	@Override
	public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
		if (!oldUser.equals(newUser)) {
			User.merge(oldUser, newUser);
		}
		return oldUser;
	}

	@Override
	public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
		User.addLinkedAccount(oldUser, newUser);
		return newUser;
	}
	
	@Override
	public AuthUser update(final AuthUser knownUser) {
		// User logged in again, bump last login date
		User.setLastLoginDate(knownUser);
		return knownUser;
	}

}
