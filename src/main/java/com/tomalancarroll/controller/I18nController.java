package com.tomalancarroll.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/i18n")
public class I18nController {

    @RequestMapping(value="/{subject}", method= RequestMethod.GET)
    public String getTranslation(@PathVariable String subject) {
        // TODO: Fetch from CMS
        return "Requested subject: " + subject;
    }

}
