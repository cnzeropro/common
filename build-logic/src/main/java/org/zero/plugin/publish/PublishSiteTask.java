package org.zero.plugin.publish;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.LocalState;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

/**
 * Publish site task - 将本地 staging 仓库内容同步到 Git 站点分支。
 */
public abstract class PublishSiteTask extends DefaultTask {
	public PublishSiteTask() {
		setGroup("publishing");
		setDescription("Publish staged site content to a Git branch.");
		getRepositoryOwner().convention("");
		getRepositoryName().convention("");
		getBranch().convention("main");
		getCommitMessage().convention(getProject().provider(() -> "upload " + getProject().getName() + "-" + getProject().getVersion()));
		getMerge().convention(true);
		getNoJekyll().convention(true);
		getDryRun().convention(false);
	}

	@InputDirectory
	public abstract DirectoryProperty getInputDirectory();

	@LocalState
	public abstract DirectoryProperty getWorkspaceDirectory();

	@Input
	@Optional
	public abstract Property<String> getRemoteUrl();

	@Input
	public abstract Property<String> getRepositoryOwner();

	@Input
	public abstract Property<String> getRepositoryName();

	@Input
	public abstract Property<String> getBranch();

	@Input
	public abstract Property<String> getCommitMessage();

	@Input
	public abstract Property<Boolean> getMerge();

	@Input
	public abstract Property<Boolean> getNoJekyll();

	@Input
	public abstract Property<Boolean> getDryRun();

	@TaskAction
	public void publishSite() {
		Path inputDirectory = getInputDirectory().get().getAsFile().toPath();
		if (!Files.isDirectory(inputDirectory)) {
			throw new GradleException("Missing staged site directory: " + inputDirectory);
		}
		if (isDirectoryEmpty(inputDirectory)) {
			throw new GradleException("Staged site directory is empty: " + inputDirectory);
		}

		Path workspaceDirectory = getWorkspaceDirectory().get().getAsFile().toPath();
		Path worktreeDirectory = workspaceDirectory.resolve("worktree");
		deleteDirectory(workspaceDirectory);
		createDirectories(workspaceDirectory);

		String remoteUrl = resolveRemoteUrl();
		String branch = getBranch().get();
		if (Boolean.TRUE.equals(getMerge().get())) {
			prepareMergedWorktree(workspaceDirectory, worktreeDirectory, remoteUrl, branch);
		} else {
			prepareFreshWorktree(worktreeDirectory, remoteUrl, branch);
		}

		configureGitIdentity(worktreeDirectory);
		syncDirectory(inputDirectory, worktreeDirectory);
		if (Boolean.TRUE.equals(getNoJekyll().get())) {
			writeBytes(worktreeDirectory.resolve(".nojekyll"), new byte[0]);
		}

		runGit(worktreeDirectory, true, "add", "--all");
		if (!hasStagedChanges(worktreeDirectory)) {
			getLogger().lifecycle("publishSite: no site changes detected.");
			return;
		}

		String status = runGit(worktreeDirectory, true, "status", "--short").trim();
		if (Boolean.TRUE.equals(getDryRun().get())) {
			getLogger().lifecycle("publishSite: dry-run enabled, skipping commit and push.");
			getLogger().lifecycle(status);
			return;
		}

		runGit(worktreeDirectory, true, "commit", "-m", getCommitMessage().get());
		if (Boolean.TRUE.equals(getMerge().get())) {
			runGit(worktreeDirectory, true, "push", "origin", "HEAD:refs/heads/" + branch);
		} else {
			runGit(worktreeDirectory, true, "push", "--force", "origin", "HEAD:refs/heads/" + branch);
		}
	}

	private String resolveRemoteUrl() {
		String configuredRemoteUrl = getRemoteUrl().getOrNull();
		if (configuredRemoteUrl != null && !configuredRemoteUrl.trim().isEmpty()) {
			return configuredRemoteUrl.trim();
		}
		String repositoryOwner = getRepositoryOwner().getOrElse("").trim();
		String repositoryName = getRepositoryName().getOrElse("").trim();
		if (repositoryOwner.isEmpty() || repositoryName.isEmpty()) {
			throw new GradleException("Missing site publish target. Configure remoteUrl or repositoryOwner/repositoryName.");
		}
		return "https://github.com/" + repositoryOwner + "/" + repositoryName + ".git";
	}

	private void prepareMergedWorktree(Path workspaceDirectory, Path worktreeDirectory, String remoteUrl, String branch) {
		if (!remoteBranchExists(workspaceDirectory, remoteUrl, branch)) {
			getLogger().lifecycle("publishSite: remote branch does not exist, creating a fresh branch.");
			prepareFreshWorktree(worktreeDirectory, remoteUrl, branch);
			return;
		}

		Path bareRepositoryDirectory = workspaceDirectory.resolve("site.git");
		runGit(workspaceDirectory, true, "init", "--bare", bareRepositoryDirectory.toString());
		runGit(bareRepositoryDirectory, true, "remote", "add", "origin", remoteUrl);
		runGit(bareRepositoryDirectory, true, "fetch", "--depth", "1", "origin", "refs/heads/" + branch);
		runGit(
			bareRepositoryDirectory,
			true,
			"worktree",
			"add",
			"--force",
			"-B",
			branch,
			worktreeDirectory.toString(),
			"FETCH_HEAD"
		);
	}

	private void prepareFreshWorktree(Path worktreeDirectory, String remoteUrl, String branch) {
		createDirectories(worktreeDirectory);
		runGit(worktreeDirectory, true, "init");
		runGit(worktreeDirectory, true, "checkout", "--orphan", branch);
		runGit(worktreeDirectory, true, "remote", "add", "origin", remoteUrl);
	}

	private boolean remoteBranchExists(Path workingDirectory, String remoteUrl, String branch) {
		GitCommandResult result = runGitResult(
			workingDirectory,
			false,
			Arrays.asList("git", "ls-remote", "--exit-code", "--heads", remoteUrl, "refs/heads/" + branch)
		);
		return result.exitCode == 0;
	}

	private void configureGitIdentity(Path workingDirectory) {
		runGit(workingDirectory, true, "config", "user.name", "Zero Build Bot");
		runGit(workingDirectory, true, "config", "user.email", "build@localhost");
	}

	private void syncDirectory(Path sourceDirectory, Path targetDirectory) {
		clearDirectory(targetDirectory);
		try {
			Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
					Path relativePath = sourceDirectory.relativize(directory);
					createDirectories(targetDirectory.resolve(relativePath));
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Path relativePath = sourceDirectory.relativize(file);
					Path targetFile = targetDirectory.resolve(relativePath);
					createDirectories(targetFile.getParent());
					Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException ex) {
			throw new GradleException("Failed to sync site directory from " + sourceDirectory + " to " + targetDirectory, ex);
		}
	}

	private void clearDirectory(Path directory) {
		if (!Files.isDirectory(directory)) {
			return;
		}
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path child : stream) {
				if (".git".equals(child.getFileName().toString())) {
					continue;
				}
				deleteDirectory(child);
			}
		} catch (IOException ex) {
			throw new GradleException("Failed to clear site worktree directory: " + directory, ex);
		}
	}

	private boolean isDirectoryEmpty(Path directory) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			return !stream.iterator().hasNext();
		} catch (IOException ex) {
			throw new GradleException("Failed to inspect directory: " + directory, ex);
		}
	}

	private void createDirectories(Path directory) {
		try {
			if (directory != null) {
				Files.createDirectories(directory);
			}
		} catch (IOException ex) {
			throw new GradleException("Failed to create directory: " + directory, ex);
		}
	}

	private void writeBytes(Path path, byte[] content) {
		try {
			createDirectories(path.getParent());
			Files.write(path, content);
		} catch (IOException ex) {
			throw new GradleException("Failed to write file: " + path, ex);
		}
	}

	private void deleteDirectory(Path path) {
		if (path == null || !Files.exists(path)) {
			return;
		}
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					makeWritable(file);
					Files.deleteIfExists(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path directory, IOException ex) throws IOException {
					if (ex != null) {
						throw ex;
					}
					makeWritable(directory);
					Files.deleteIfExists(directory);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException ex) {
			throw new GradleException("Failed to delete directory: " + path, ex);
		}
	}

	private void makeWritable(Path path) throws IOException {
		if (!Files.exists(path)) {
			return;
		}
		if (Files.getFileAttributeView(path, java.nio.file.attribute.DosFileAttributeView.class) != null) {
			Files.getFileAttributeView(path, java.nio.file.attribute.DosFileAttributeView.class).setReadOnly(false);
		}
		path.toFile().setWritable(true);
	}

	private String runGit(Path workingDirectory, boolean failOnError, String... args) {
		GitCommandResult result = runGitResult(workingDirectory, failOnError, Arrays.asList(prependGitCommand(args)));
		return result.output;
	}

	private boolean hasStagedChanges(Path workingDirectory) {
		GitCommandResult result = runGitResult(
			workingDirectory,
			false,
			Arrays.asList("git", "diff", "--cached", "--quiet", "--exit-code")
		);
		return result.exitCode == 1;
	}

	private GitCommandResult runGitResult(Path workingDirectory, boolean failOnError, List<String> command) {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		if (workingDirectory != null) {
			processBuilder.directory(workingDirectory.toFile());
		}
		processBuilder.redirectErrorStream(true);
		processBuilder.environment().put("GIT_TERMINAL_PROMPT", "0");
		try {
			Process process = processBuilder.start();
			String output = readAll(process.getInputStream());
			int exitCode = process.waitFor();
			if (failOnError && exitCode != 0) {
				throw new GradleException("Git command failed (" + String.join(" ", command) + "):\n" + output.trim());
			}
			return new GitCommandResult(exitCode, output);
		} catch (IOException ex) {
			throw new GradleException("Failed to run git command: " + String.join(" ", command), ex);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new GradleException("Interrupted while running git command: " + String.join(" ", command), ex);
		}
	}

	private String[] prependGitCommand(String[] args) {
		String[] command = new String[args.length + 1];
		command[0] = "git";
		System.arraycopy(args, 0, command, 1, args.length);
		return command;
	}

	private String readAll(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int read;
		while ((read = inputStream.read(buffer)) >= 0) {
			outputStream.write(buffer, 0, read);
		}
		return outputStream.toString("UTF-8");
	}

	private static final class GitCommandResult {
		private final int exitCode;
		private final String output;

		private GitCommandResult(int exitCode, String output) {
			this.exitCode = exitCode;
			this.output = output;
		}
	}
}
