package me.bristermitten.pdmcommonlib.pom;

import java.util.List;
import java.util.Map;

public class Pom
{

    private Pom parent;
    private Map<String, String> properties;
    private String group, artifact, version, name, description, url;
    private List<Repository> repositories;
}
