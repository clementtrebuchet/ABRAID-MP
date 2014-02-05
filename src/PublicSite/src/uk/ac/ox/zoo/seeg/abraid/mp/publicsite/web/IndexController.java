package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class IndexController {
    private DiseaseService diseaseService;

    @Autowired
    public void setDiseaseService(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String getAll(Map<String, Object> model) {
        List<Disease> allDiseases = diseaseService.getAllDiseases();
        Collections.sort(allDiseases, new Comparator<Disease>() {
            @Override
            public int compare(Disease o1, Disease o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        model.put("diseases", allDiseases);
        return "index";
    }
}