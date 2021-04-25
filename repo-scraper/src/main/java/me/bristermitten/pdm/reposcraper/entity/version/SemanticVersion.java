package me.bristermitten.pdm.reposcraper.entity.version;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

class SemanticVersion implements Version<SemanticVersion> {

    private static final Comparator<SemanticVersion> COMPARATOR =
            Comparator.comparingInt(SemanticVersion::getMajor)
                    .thenComparingInt(SemanticVersion::getMinor)
                    .thenComparingInt(SemanticVersion::getPatch)
                    .thenComparing(SemanticVersion::getPatch);

    private final int major;
    private final int minor;
    private final int patch;
    private final Status status;

    SemanticVersion(int major, int minor, int patch) {
        this(major, minor, patch, Status.STABLE);
    }

    SemanticVersion(int major, int minor, int patch, Status status) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.status = status;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    @Override
    public int compareTo(@NotNull SemanticVersion o) {
        return COMPARATOR.compare(this, o);
    }

    enum Status {
        BETA,
        RC,
        STABLE
    }
}
