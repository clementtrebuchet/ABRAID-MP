package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ModelWrapperConfigurationService;

import java.util.regex.Pattern;

/**
 * Controller for the ModelWrapper Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class IndexController extends AbstractController {
    private static final Pattern USERNAME_REGEX = Pattern.compile("^[a-z0-9_-]{3,15}$");

    // Regex from http://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=^[^\\s]{6,128}$)((?=.*?\\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[^\\w\\d\\s])(?=.*?[a-z])|(?=.*?[^\\w\\d\\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[A-Z])(?=.*?[^\\w\\d\\s]))^.*$"); ///CHECKSTYLE:SUPPRESS LineLengthCheck

    private final ModelWrapperConfigurationService configurationService;

    @Autowired
    public IndexController(ModelWrapperConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Request map for the index page.
     * @param model The ftl data model.
     * @return The ftl index page name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndexPage(Model model) {
        String rPath;
        try {
            rPath = configurationService.getRExecutablePath();
        } catch (ConfigurationException e) {
            rPath = "";
        }

        model.addAttribute("run_duration", configurationService.getMaxModelRunDuration());
        model.addAttribute("r_path", rPath);

        return "index";
    }

    /**
     * Updates the modelwrapper authentication details.
     * @param username The new username.
     * @param password The new password.
     * @param passwordConfirmation Confirmation of the new password.
     * @return 204 for success, 400 for failure.
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity updateAuthenticationDetails(String username, String password, String passwordConfirmation) {
        boolean validRequest =
            !StringUtils.isEmpty(username) && USERNAME_REGEX.matcher(username).matches() &&
            !StringUtils.isEmpty(password) && PASSWORD_REGEX.matcher(password).matches() &&
            password.equals(passwordConfirmation);

        if (validRequest) {
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            configurationService.setAuthenticationDetails(username, passwordHash);

            // Respond with a 204, this is equivalent to a 200 (OK) but without any content.
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

}
