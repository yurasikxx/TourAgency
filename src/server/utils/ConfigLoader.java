package server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private final Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл config.properties не найден!");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке конфигурационного файла", e);
        }
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Параметр " + key + " не найден в конфигурационном файле!");
        }
        return value;
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Параметр " + key + " должен быть целым числом!", e);
        }
    }

}