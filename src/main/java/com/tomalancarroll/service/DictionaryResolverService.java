package com.tomalancarroll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DictionaryResolverService {
    private static final Logger logger = LoggerFactory.getLogger(DictionaryResolverService.class);

    public Resource[] resolveDictionaries() throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resolver.getResources("classpath:i18n/*/*.json");
        logger.info("Found this many dictionaries: " +
                ((resources != null) ? resources.length : "0"));

        for (Resource resource: resources){
            logger.info("Found dictionary for locale " + resource.getFile().getParentFile().getName() +
                    " and subject " + resource.getFilename());
        }

        return resources;
    }

}
