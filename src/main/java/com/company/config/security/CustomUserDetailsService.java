package com.company.config.security;


import com.company.entity.ProfileEntity;
import com.company.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ProfileEntity> optional = profileRepository.findByEmail(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println("Load by Username");
        ProfileEntity profile = optional.get();
        return new CustomUserDetails(profile);
    }


}
