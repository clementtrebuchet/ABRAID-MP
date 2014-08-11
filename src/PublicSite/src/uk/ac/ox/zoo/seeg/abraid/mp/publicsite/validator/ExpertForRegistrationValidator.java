package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertBasic;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates the fields associated with an Expert during registration.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertForRegistrationValidator {
    private static final Logger LOGGER = Logger.getLogger(ExpertForRegistrationValidator.class);
    private static final String LOG_CAPTCHA_REJECTED = "Captcha rejected: %s";

    private static final String RECAPTCHA_THEME = "clean";

    private static final String CAPTCHA_FIELD_NAME = "Captcha";
    private static final String PASSWORD_CONFIRMATION_FIELD_NAME = "Password confirmation";

    private static final String FAILURE_INCORRECT_VALUE = "%s incorrect.";
    private static final String FAILURE_MUST_MATCH = "%s pair must match.";

    private final ExpertValidationRulesChecker rules;
    private final ReCaptcha reCaptchaService;

    @Autowired
    public ExpertForRegistrationValidator(
            ExpertValidationRulesChecker expertValidationRulesChecker,
            ReCaptcha reCaptchaService) {
        this.rules = expertValidationRulesChecker;
        this.reCaptchaService = reCaptchaService;
    }

    /**
     * Generates the HTML for a validation captcha.
     * @return the HTML for a validation captcha.
     */
    public String createValidationCaptcha() {
        return reCaptchaService.createRecaptchaHtml(null, RECAPTCHA_THEME, null);
    }

    /**
     * Validates the parts of Expert provided on the account registration page.
     * @param expert Expert to validate.
     * @return Any failure messages.
     */
    public List<String> validateBasicFields(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // Check email
        rules.checkEmail(expert.getEmail(), validationFailures);

        // Check password
        rules.checkPassword(expert.getPassword(), validationFailures);

        return validationFailures;
    }

    /**
     * Validates the parts of Expert provided on the account registration details page.
     * @param expert Expert to validate.
     * @return Any failure messages.
     */
    public List<String> validateDetailsFields(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // name
        rules.checkName(expert.getName(), validationFailures);

        // job
        rules.checkJobTitle(expert.getJobTitle(), validationFailures);

        // institution
        rules.checkInstitution(expert.getInstitution(), validationFailures);

        return validationFailures;
    }

    /**
     * Validates the transient fields provided on the account registration page that are not part of the Expert entity.
     * @param expertBasic The expert dto to validate.
     * @param request The HTTP request that to be validated against.
     * @return Any failure messages.
     */
    public List<String> validateTransientFields(JsonExpertBasic expertBasic, ServletRequest request) {
        List<String> validationFailures = new ArrayList<>();

        checkPasswordConfirmation(expertBasic, validationFailures);

        checkCaptcha(expertBasic.getCaptchaChallenge(), expertBasic.getCaptchaResponse(), request, validationFailures);

        return validationFailures;
    }

    private void checkPasswordConfirmation(JsonExpertBasic expertBasic, List<String> validationFailures) {
        if (!expertBasic.getPassword().equals(expertBasic.getPasswordConfirmation())) {
            validationFailures.add(String.format(FAILURE_MUST_MATCH, PASSWORD_CONFIRMATION_FIELD_NAME));
        }
    }

    private void checkCaptcha(
            String challenge, String response, ServletRequest request, List<String> validationFailures) {
        ReCaptchaResponse result = reCaptchaService.checkAnswer(
                request.getRemoteAddr(),
                challenge,
                response
        );

        if (!result.isValid()) {
            validationFailures.add(String.format(FAILURE_INCORRECT_VALUE, CAPTCHA_FIELD_NAME));
            LOGGER.info(String.format(LOG_CAPTCHA_REJECTED, result.getErrorMessage()));
        }
    }

}
