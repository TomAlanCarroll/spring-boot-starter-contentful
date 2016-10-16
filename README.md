# Spring Boot Starter Contentful

This is a starter project for two use cases:

1. You want to create a lightweight Spring Boot webapp that is backed by Contentful's CMS API
2. You already have a Spring webapp and would like to incorporate Contentful's CMS API

You can run the project with the following commands:

```
bower install
mvn spring-boot:run \
-Dcontentful.space.id=add_your_space_id_here \
-Dcontentful.delivery.api.key=add_your_delivery_api_key_here \
-Dcontentful.preview.api.key=add_your_preview_api_key_here \
-Dcontentful.management.token=add_your_management_api_token_here
```
