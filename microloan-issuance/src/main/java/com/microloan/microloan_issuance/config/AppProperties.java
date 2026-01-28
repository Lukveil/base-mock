package com.microloan.microloan_issuance.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.scheduler")
@Getter
@Setter
public class AppProperties {
    @Min(value = 0)
    @Max(value = 6000000)
    private long delay;
}
