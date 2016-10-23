package com.tomalancarroll.controller;

import com.tomalancarroll.service.ContentfulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping(value="/i18n")
public class I18nController {
    @Autowired
    private ContentfulService contentfulService;

    @RequestMapping(value="/{subject}", method= RequestMethod.GET)
    @ResponseBody
    public String getTranslation(@PathVariable String subject,
                                     @RequestParam(value = "lang") String languageTag) {
        Locale locale = Locale.forLanguageTag(languageTag);

        return contentfulService.getTranslation(subject, locale);
    }

}
