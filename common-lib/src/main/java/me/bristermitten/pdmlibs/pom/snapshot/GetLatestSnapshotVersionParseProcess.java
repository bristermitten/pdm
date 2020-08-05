package me.bristermitten.pdmlibs.pom.snapshot;

import me.bristermitten.pdmlibs.pom.ParseProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

/**
 * @author AlexL
 */
public class GetLatestSnapshotVersionParseProcess implements ParseProcess<@Nullable String>
{

    private final LatestElementParseStage latestElementParseStage = new LatestElementParseStage();
    private final SnapshotVersionParseStage snapshotVersionParseStage = new SnapshotVersionParseStage();
    private final JitpackLatestSnapshotParseStage jitpackLatestSnapshotParseStage = new JitpackLatestSnapshotParseStage();

    @NotNull
    @Override
    public @Nullable String parse(@NotNull Document document)
    {
        final String latest = latestElementParseStage.parse(document);
        if (latest != null)
        {
            return latest;
        }

        final String latestSnapshotVersion = snapshotVersionParseStage.parse(document);
        if (latestSnapshotVersion != null)
        {
            return latestSnapshotVersion;
        }
        final String jitpackSnapshot = jitpackLatestSnapshotParseStage.parse(document);
        return jitpackSnapshot;
    }
}
