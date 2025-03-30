package com.example.formativa.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.formativa.models.Profile;
import com.example.formativa.models.User;
import com.example.formativa.repositories.ProfileRepository;
import com.example.formativa.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
    
    @Transactional
    public User registerNewUser(String username, String email, String password, PasswordEncoder passwordEncoder) {
        User user = new User(username, passwordEncoder.encode(password), email);
        user = userRepository.save(user);
        
        // Create profile and establish bidirectional relationship
        Profile profile = new Profile();
        profile.setUser(user);
        profileRepository.save(profile);
        
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<Profile> getProfileByUsername(String username) {
        return profileRepository.findByUserUsername(username);
    }

    @Transactional
    public void createProfile(String username, String avatarUri, String favoriteGames, boolean emailNotifications, boolean pushNotifications) {
        Profile profile = new Profile();
        profile.setUser(userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found: " + username)));
        profile.setAvatarUri(avatarUri);
        profile.setFavoriteGames(favoriteGames);
        profile.setEmailNotifications(emailNotifications);
        profile.setPushNotifications(pushNotifications);
        profileRepository.save(profile);
    }

    @Transactional
    public Profile updateProfile(String username, String avatarUri, String favoriteGames, 
                                boolean emailNotifications, boolean pushNotifications) {
        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + username));
        
        // Update the user's username
        // if (newUsername != null && !newUsername.isEmpty()) {
        //     User user = profile.getUser();
        //     user.setUsername(newUsername);
        //     userRepository.save(user);
        // }
        
        if (avatarUri != null && !avatarUri.isEmpty()) {
            profile.setAvatarUri(avatarUri);
        }
        
        profile.setFavoriteGames(favoriteGames);
        
        profile.setEmailNotifications(emailNotifications);
        profile.setPushNotifications(pushNotifications);
        
        return profileRepository.save(profile);
    }

    @Transactional
    public User updateUsername(String oldUsername, String newUsername) {
        Profile oldProfile = profileRepository.findByUserUsername(oldUsername)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + oldUsername));
        User user = userRepository.findByUsername(oldUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + oldUsername));
        user.setUsername(newUsername);
        userRepository.save(user);
        oldProfile.setUser(user);
        profileRepository.save(oldProfile);
        return user;
    }
} 