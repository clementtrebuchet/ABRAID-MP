package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

/**
 * Controller for the Atlas Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AtlasController extends AbstractController {

    /**
     * Return the view to display.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showPage() {
        return "atlas";
    }

    /**
     * Shows the experts page.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/experts", method = RequestMethod.GET)
    public String showExperts() {
        return "experts";
    }
}
