package model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataRepository {
    public static final Map<String, MLWorkspace> workspaces = new ConcurrentHashMap<>();
    public static final Map<String, MachineLearningModel> models = new ConcurrentHashMap<>();

    static {
        // Seed a default workspace so you can test immediately in Postman
        MLWorkspace defaultWs = new MLWorkspace("WS-VISION-01", "Computer Vision Lab", 500);
        workspaces.put(defaultWs.getId(), defaultWs);
    }
}