package org.zero.plugin.source

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/8/12
 */
interface SourceJarExtension {
	/**
	 * 是否包含 POM 文件
	 */
	Property<Boolean> getIncludePom()
	/**
	 * 是否包含资源文件
	 */
	Property<Boolean> getIncludeResources()
	/**
	 * 分类器（如：sources）
	 */
	Property<String> getClassifier()
	/**
	 * 输出目录
	 */
	DirectoryProperty getOutputDirectory()
	/**
	 * 最终文件名（不含后缀）
	 */
	Property<String> getFinalName()
	/**
	 * 排除文件模式
	 */
	ListProperty<String> getExcludes()
}