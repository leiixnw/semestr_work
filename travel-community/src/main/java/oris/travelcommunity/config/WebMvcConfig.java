package oris.travelcommunity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import oris.travelcommunity.converters.StringToCategoryConverter;
import oris.travelcommunity.converters.StringToLocalDateConverter;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringToCategoryConverter stringToCategoryConverter;
    private final StringToLocalDateConverter stringToLocalDateConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToCategoryConverter);
        registry.addConverter(stringToLocalDateConverter);
    }
}