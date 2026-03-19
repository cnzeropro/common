package org.zero.build.metadata;

import java.io.File;

/**
 * Build metadata CLI - small Java entry point for the metadata scripts.
 */
public final class BuildMetadataCli {
    private BuildMetadataCli() {
    }

    public static void main(String[] args) {
        /*
         * argument guard - require both the subcommand and the repository root.
         */
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: BuildMetadataCli <sync|verify> <repoRoot>");
        }

        String command = args[0];
        File rootDir = new File(args[1]);
        BuildMetadataGenerator generator = new BuildMetadataGenerator(rootDir);
        /*
         * command dispatch - sync writes files, verify only compares current content.
         */
        if ("sync".equals(command)) {
            generator.sync();
            return;
        }
        if ("verify".equals(command)) {
            generator.verify();
            return;
        }

        throw new IllegalArgumentException("Unsupported command '" + command + "'");
    }
}
