package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.adapter.LocalDateTimeAdapter;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import servers.HttpTaskManager;

import java.time.LocalDateTime;

import static servers.KVServer.KVSERVER_URI;

//C:/Users/chetv/dev/java-kanban/resources/task.cvs

public abstract class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager(KVSERVER_URI);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
