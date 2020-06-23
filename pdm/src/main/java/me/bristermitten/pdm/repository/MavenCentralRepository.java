package me.bristermitten.pdm.repository;

public final class MavenCentralRepository extends MavenRepository
{

    public static final String MAVEN_CENTRAL_ALIAS = "maven-central";
    private static final String MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2";

    public MavenCentralRepository()
    {
        super(MAVEN_CENTRAL_URL);
    }

}
