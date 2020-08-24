package me.bristermitten.pdmcommonlib.pom;

import java.util.List;

public class Dependency
{

    private String group, artifact, version, scope;
    private boolean isOptional;
    private List<Exclusion> exclusions;

    public static class Exclusion
    {
        private String group, artifact;
    }
}
