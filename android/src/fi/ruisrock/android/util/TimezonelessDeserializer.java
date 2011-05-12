package fi.ruisrock.android.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class TimezonelessDeserializer extends JsonDeserializer<Date> {
	
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        String date = jsonParser.getText();
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	

}
