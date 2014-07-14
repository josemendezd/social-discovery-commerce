/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Ebean;

//import com.google.common.collect.Lists;
//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import controllers.Application;

import play.db.ebean.Model;
import be.objectify.deadbolt.core.models.Role;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Entity
public class SecurityRole extends Model implements Role {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public String roleName;

	public static final Finder<Long, SecurityRole> find = new Finder<Long, SecurityRole>(
			Long.class, SecurityRole.class);

	@Override
	public String getName() {
		return roleName;
	}

	public static SecurityRole findByRoleName(String roleName) {
		return find.where().eq("roleName", roleName).findUnique();
	}
	
	public static Map<String,SecurityRole> GetAllRoles()
	{
		return find.findMap("roleName", String.class);
	}
	
	public static int ClearPermissions(User u)
	{
		return Ebean.createSqlUpdate("DELETE FROM users_security_role WHERE users_id=:id").setParameter("id", u.id).execute();
	}
	

	public static void setAdmin(User adminuser)
	{
		Map<String,SecurityRole> rolelist = GetAllRoles();
		SecurityRole admins= rolelist.get(Application.ADMIN_ROLE),
				moderators = rolelist.get(Application.MODERATOR_ROLE),
				users = rolelist.get(Application.USER_ROLE);
		ClearPermissions(adminuser);
		adminuser.roles= Arrays.asList(admins,moderators,users);
		adminuser.save();
	}
	
	public static void setModerator(User moderateuser)
	{
		Map<String,SecurityRole> rolelist = GetAllRoles();
		SecurityRole moderators = rolelist.get(Application.MODERATOR_ROLE),
				users = rolelist.get(Application.USER_ROLE);
		ClearPermissions(moderateuser);
		moderateuser.roles= Arrays.asList(moderators,users);
		moderateuser.save();
	}
	public static void setUser(User simpleuser)
	{
		Map<String,SecurityRole> rolelist = GetAllRoles();
		SecurityRole users = rolelist.get(Application.USER_ROLE);
		ClearPermissions(simpleuser);
		simpleuser.roles= Arrays.asList(users);
		simpleuser.save();
	}
	
	public static boolean IsAdmin(Contributor adminuser)
	{
		SecurityRole admins= findByRoleName(Application.ADMIN_ROLE);
		return adminuser.user.roles.contains(admins);		
	}
	
	public static boolean IsModerator(Contributor moderateuser)
	{
		SecurityRole moderators = findByRoleName(Application.MODERATOR_ROLE);		
		return moderateuser.user.roles.contains(moderators);
	}
	public static boolean IsUser(Contributor simpleuser)
	{
		SecurityRole users = findByRoleName(Application.USER_ROLE);
		return simpleuser.user.roles.contains(users);
	}
}
