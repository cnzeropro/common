package org.zero.plugin.convention;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.zero.plugin.publish.PublishSiteTask;

/**
 * Site publish convention plugin - 为 root project 注册站点发布任务。
 */
public final class SitePublishConventionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().register("publishSite", PublishSiteTask.class, task -> {
            task.dependsOn(project.getAllprojects().stream()
                .map(candidate -> candidate.getTasks().matching(existingTask -> "publish".equals(existingTask.getName())))
                .toArray());
            task.getInputDirectory().set(project.getLayout().getBuildDirectory().dir("repos/local-staging"));
            task.getWorkspaceDirectory().set(project.getLayout().getBuildDirectory().dir("site-publish"));
            task.getRepositoryOwner().convention("cnzeropro");
            task.getRepositoryName().convention("repository");
            task.getBranch().convention("main");
            task.getCommitMessage().convention(project.provider(() -> "upload " + project.getName() + "-" + project.getVersion()));
            task.getMerge().convention(true);
            task.getNoJekyll().convention(true);
            task.getRemoteUrl().convention(project.getProviders().gradleProperty("sitePublish.remoteUrl"));
            task.getDryRun().convention(
                project.getProviders().gradleProperty("sitePublish.dryRun")
                    .map(Boolean::parseBoolean)
                    .orElse(false)
            );
        });
    }
}
