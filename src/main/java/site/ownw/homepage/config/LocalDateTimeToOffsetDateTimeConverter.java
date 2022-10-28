package site.ownw.homepage.config;

import static site.ownw.homepage.common.Instance.ZONE_ID;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class LocalDateTimeToOffsetDateTimeConverter
        implements Converter<LocalDateTime, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(LocalDateTime source) {
        return source.atZone(ZONE_ID).toOffsetDateTime();
    }
}
