package org.feuyeux.ai.embabel.savant.config;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.YamlPropertySourceLoader;

import java.io.IOException;

public class YamlPropertySourceFactory implements PropertySourceFactory {
    
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        return loader.load(resource.getResource().getFilename(), resource.getResource()).get(0);
    }
}