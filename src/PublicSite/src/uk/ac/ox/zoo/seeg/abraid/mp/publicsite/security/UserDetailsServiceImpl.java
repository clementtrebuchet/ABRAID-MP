package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Service to load Expert via ExpertService, as a Spring UserDetails.
 * Copyright (c) 2014 University of Oxford
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_SEEG = "ROLE_SEEG";

    private ExpertService expertService;

    public UserDetailsServiceImpl(ExpertService expertService) {
        this.expertService = expertService;
    }

    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        Expert expert = expertService.getExpertByEmail(emailAddress);
        if (expert == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return buildUserFromExpert(expert);
    }

    private User buildUserFromExpert(Expert expert) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        if (expert.isAdministrator()) {
            authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
        }
        if (expert.isSeegMember()) {
            authorities.add(new SimpleGrantedAuthority(ROLE_SEEG));
        }

        return new PublicSiteUser(
                expert.getId(),
                expert.getEmail(),
                expert.getName(),
                expert.getPassword(),
                authorities);
    }
}
