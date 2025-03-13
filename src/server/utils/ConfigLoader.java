package server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для загрузки конфигурационных параметров из файла config.properties.
 */
public class ConfigLoader {
    private Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        loadConfig();
    }

    /**
     * Загружает конфигурационные параметры из файла config.properties.
     */
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

    /**
     * Возвращает значение параметра по ключу.
     *
     * @param key Ключ параметра.
     * @return Значение параметра.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Возвращает значение параметра по ключу с возможностью указания значения по умолчанию.
     *
     * @param key          Ключ параметра.
     * @param defaultValue Значение по умолчанию, если параметр не найден.
     * @return Значение параметра или значение по умолчанию.
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Возвращает значение параметра как целое число.
     *
     * @param key Ключ параметра.
     * @return Значение параметра как целое число.
     * @throws NumberFormatException Если значение параметра не является числом.
     */
    public int getIntProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Параметр " + key + " не найден в конфигурационном файле!");
        }
        return Integer.parseInt(value);
    }

    /**
     * Возвращает значение параметра как целое число с возможностью указания значения по умолчанию.
     *
     * @param key          Ключ параметра.
     * @param defaultValue Значение по умолчанию, если параметр не найден.
     * @return Значение параметра как целое число или значение по умолчанию.
     * @throws NumberFormatException Если значение параметра не является числом.
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    /**
     * Возвращает значение параметра как логическое значение.
     *
     * @param key Ключ параметра.
     * @return Значение параметра как логическое значение.
     */
    public boolean getBooleanProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Параметр " + key + " не найден в конфигурационном файле!");
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Возвращает значение параметра как логическое значение с возможностью указания значения по умолчанию.
     *
     * @param key          Ключ параметра.
     * @param defaultValue Значение по умолчанию, если параметр не найден.
     * @return Значение параметра как логическое значение или значение по умолчанию.
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}