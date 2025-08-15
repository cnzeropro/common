package org.zero.plugin.source

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/8/12
 */
class SourceJarPlugin implements Plugin<Project> {
	void apply(Project project) {
		// 创建扩展
		def extension = project.extensions.create("sourceJar", SourceJarExtension)

		// 注册任务
		project.tasks.register("sourceJar", Jar) { task ->
			task.group = "Build"
			task.description = "Generates a sources JAR"

			// 动态设置分类器
			task.archiveClassifier.set(
				extension.classifier.map { it }.orElse("sources")
			)
			// 输出目录
			task.destinationDirectory.set(
				extension.outputDirectory.orElse(
					project.layout.buildDirectory.dir("libs")
				)
			)
			// 最终文件名
			task.archiveBaseName.set(
				extension.finalName.orElse(project.name)
			)

			// 获取资源集
			SourceSet sourceSet = project.extensions.getByType(JavaPluginExtension).sourceSets.getByName("main")

			// 直接添加源目录
			task.from(sourceSet.java.srcDirs)
			// 可选添加资源目录
			if (extension.includeResources.getOrElse(false)) {
				task.from(sourceSet.resources.srcDirs)
			}
			// 排除 POM 文件
			if (!extension.includePom.getOrElse(false)) {
				task.exclude("**/pom.xml")
			}
			// 添加自定义排除
			if (extension.excludes.present) {
				task.exclude(extension.excludes.get())
			}

			// 显式声明输入输出
			task.inputs.files(sourceSet.allSource)
			task.outputs.cacheIf { true }
		}
	}
}