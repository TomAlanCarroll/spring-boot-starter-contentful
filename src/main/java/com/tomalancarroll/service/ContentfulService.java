package com.tomalancarroll.service;

import com.contentful.java.cda.CDAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentfulService {
    private static final Logger logger = LoggerFactory.getLogger(ContentfulService.class);

    @Autowired
    private CDAClient contentfulDeliveryClient;

    @Autowired
    private CDAClient contentfulPreviewClient;

}
