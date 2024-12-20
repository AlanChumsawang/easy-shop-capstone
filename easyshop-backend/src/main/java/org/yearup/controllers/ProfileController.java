package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class ProfileController {

    private final ProfileDao profileDao;
    private final UserDao userDao;
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping("")
    public Profile getProfile(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                LOGGER.log(Level.WARNING, "User not found: " + userName);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            Profile profile = profileDao.getByUserId(user.getId());
            if (profile == null) {
                LOGGER.log(Level.WARNING, "Profile not found for user ID: " + user.getId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
            }
            return profile;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting profile for user: " + principal.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("")
    public Profile updateProfile(@RequestBody Profile profile, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                LOGGER.log(Level.WARNING, "User not found: " + userName);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            profile.setUserId(user.getId());
            return profileDao.update(profile);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating profile for user: " + principal.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}