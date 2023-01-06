package me.bartosz1.monitoring.providers.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.bartosz1.monitoring.models.Incident;
import me.bartosz1.monitoring.models.Monitor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class GenericWebhookNotificationProvider extends NotificationProvider {

    private static final String TEST_NOTIFICATION = "{\"event\": \"test-notification\"}";

    @Override
    public void sendNotification(Monitor monitor, Incident incident, String args) {
        if (args.startsWith("http://") || args.startsWith("https://")) {
            HashMap<String, Object> bodyContent = new HashMap<>();
            bodyContent.put("event", "status-change");
            bodyContent.put("status", monitor.getLastStatus().name().toLowerCase());
            bodyContent.put("monitor", monitor);
            bodyContent.put("incident", incident);
            try {
                RequestBody requestBody = RequestBody.create(super.getObjectMapper().writeValueAsString(bodyContent), MediaType.parse("application/json"));
                Request req = new Request.Builder().post(requestBody).url(args).build();
                super.getHttpClient().newCall(req).enqueue(BLANK_CALLBACK);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendTestNotification(String args) {
        if (args.startsWith("http://") || args.startsWith("https://")) {
            try {
                RequestBody requestBody = RequestBody.create(super.getObjectMapper().writeValueAsString(TEST_NOTIFICATION), MediaType.parse("application/json"));
                Request req = new Request.Builder().post(requestBody).url(args).build();
                super.getHttpClient().newCall(req).enqueue(BLANK_CALLBACK);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
