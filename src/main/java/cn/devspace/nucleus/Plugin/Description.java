package cn.devspace.nucleus.Plugin;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Description {

    private String name;
    private String author;
    private String version;
    private String main;
    private String description;
    private String dbName;
    private String dbType;
    private String route = null;
    private String language = null;

    public Description(String YamlString) {
        Yaml yml = new Yaml();
        this.LoadMap(yml.loadAs(YamlString, Map.class));
    }

    public Description(InputStream YamlInputStream) {
        Yaml yml = new Yaml();
        this.LoadMap(yml.loadAs(YamlInputStream, Map.class));
    }

    public void LoadMap(Map<String, Object> map) {
        if (map != null) {
            this.name = (String) map.get("Name");
            this.author = (String) map.get("Author");
            this.version = (String) map.get("Version");
            this.main = (String) map.get("Main");
            this.description = (String) map.get("Description");
            this.dbName = (String) map.get("Database");
            this.dbType = (String) map.get("DatabaseType");
            this.route = (String) map.get("Route");
            this.language = (String) map.get("Language");
        }
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMain() {
        return this.main;
    }

    public String getDbType() {
        return this.dbType;
    }


    public String getDataBase() {
        return this.dbName;
    }

    public String getRoute() {
        if (this.route == null){
            return this.getName();
        }
        return this.route;
    }

    public String getLanguage() {
        return language;
    }
}
