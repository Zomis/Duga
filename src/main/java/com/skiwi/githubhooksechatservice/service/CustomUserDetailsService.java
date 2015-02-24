package com.skiwi.githubhooksechatservice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.UserDAO;
import com.skiwi.githubhooksechatservice.model.DugaUser;
import com.skiwi.githubhooksechatservice.model.Role;

@Service
@Transactional(readOnly=true)
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserDAO userDAO;	

	@Override
	public UserDetails loadUserByUsername(String login)
			throws UsernameNotFoundException {
		
		DugaUser domainUser = userDAO.getUser(login);
		System.out.println("Domain user: " + domainUser + " for login " + login);
		if (domainUser == null) {
			throw new UsernameNotFoundException("No user with name " + login);
		}
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(
				domainUser.getLogin(), 
				domainUser.getPassword(), 
				enabled, 
				accountNonExpired, 
				credentialsNonExpired, 
				accountNonLocked,
				getAuthorities(domainUser.getRole())
		);
		return user;
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities(Role role) {
		System.out.println("getAuthorities " + role);
		List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
		return authList;
	}
	
	public Set<String> getRoles(Role role) {
		Set<String> roles = new HashSet<String>();
		roles.add(role.getRole());
		roles.add("ROLE_" + role.getRole());
		roles.add("ROLE_USER"); // everyone is a user
		return roles;
	}
	
	public static List<GrantedAuthority> getGrantedAuthorities(Set<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}

}
