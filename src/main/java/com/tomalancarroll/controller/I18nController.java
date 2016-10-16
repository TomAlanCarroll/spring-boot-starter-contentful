package com.tomalancarroll.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/i18n")
public class I18nController {
    @RequestMapping(value="/{subject}", method= RequestMethod.GET)
    public String getTranslation(@PathVariable String subject,
                                 @RequestParam(value = "lang") String locale) {
        // TODO: Fetch from CMS
        return "{\"twoCities\":{\"title\":\"A Tale of Two Cities\"}}";
    }

}
