package org.zero.common.core.extension.spring.web.method.support;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.zero.common.core.util.javax.servlet.RequestUtil;

import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/3
 */
public final class JsonObjectArgumentResolvers {
	/**
	 * jackson
	 *
	 * @author Zero (cnzeropro@163.com)
	 * @since 2025/3/3
	 */
	@NoArgsConstructor
	public static class JacksonArgumentResolver implements HandlerMethodArgumentResolver {
		public JacksonArgumentResolver(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
			setObjectMapper(objectMapper);
		}

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterType().isAssignableFrom(com.fasterxml.jackson.databind.node.ObjectNode.class) && parameter.hasParameterAnnotation(JsonObjectParam.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			Map<String, String[]> parameterMap = webRequest.getParameterMap();
			Map<String, Object> simplifiedMap = RequestUtil.simplify(parameterMap, true);
			return objectMapper.valueToTree(simplifiedMap);
		}

		@Setter
		@NonNull
		protected static com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
	}

	/**
	 * fastjson
	 *
	 * @author Zero (cnzeropro@163.com)
	 * @since 2025/3/3
	 */
	@NoArgsConstructor
	public static class FastjsonArgumentResolver implements HandlerMethodArgumentResolver {
		public FastjsonArgumentResolver(com.alibaba.fastjson.serializer.SerializeConfig serializeConfig, com.alibaba.fastjson.parser.Feature... features) {
			setSerializeConfig(serializeConfig);
			setFeatures(features);
		}

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterType().isAssignableFrom(com.alibaba.fastjson.JSONObject.class) && parameter.hasParameterAnnotation(JsonObjectParam.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			Map<String, String[]> parameterMap = webRequest.getParameterMap();
			Map<String, Object> simplifiedMap = RequestUtil.simplify(parameterMap, true);
			return com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(simplifiedMap, serializeConfig), features);
		}

		/**
		 * @see com.alibaba.fastjson.JSON#DEFAULT_PARSER_FEATURE
		 */
		@Setter
		@NonNull
		protected com.alibaba.fastjson.parser.Feature[] features = {
			com.alibaba.fastjson.parser.Feature.AutoCloseSource,
			com.alibaba.fastjson.parser.Feature.InternFieldNames,
			com.alibaba.fastjson.parser.Feature.UseBigDecimal,
			com.alibaba.fastjson.parser.Feature.AllowUnQuotedFieldNames,
			com.alibaba.fastjson.parser.Feature.AllowSingleQuotes,
			com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas,
			com.alibaba.fastjson.parser.Feature.SortFeidFastMatch,
			com.alibaba.fastjson.parser.Feature.IgnoreNotMatch,
		};
		@Setter
		@NonNull
		protected com.alibaba.fastjson.serializer.SerializeConfig serializeConfig = com.alibaba.fastjson.serializer.SerializeConfig.getGlobalInstance();
	}

	/**
	 * fastjson2
	 *
	 * @author Zero (cnzeropro@163.com)
	 * @since 2025/3/3
	 */
	@NoArgsConstructor
	public static class Fastjson2ArgumentResolver implements HandlerMethodArgumentResolver {
		public Fastjson2ArgumentResolver(com.alibaba.fastjson2.JSONWriter.Feature[] serializationFeatures, com.alibaba.fastjson2.JSONReader.Feature[] deserializationFeatures) {
			setSerializationFeatures(serializationFeatures);
			setDeserializationFeatures(deserializationFeatures);
		}

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterType().isAssignableFrom(com.alibaba.fastjson2.JSONObject.class) && parameter.hasParameterAnnotation(JsonObjectParam.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			Map<String, String[]> parameterMap = webRequest.getParameterMap();
			Map<String, Object> simplifiedMap = RequestUtil.simplify(parameterMap, true);
			String jsonString = com.alibaba.fastjson2.JSONObject.toJSONString(simplifiedMap, serializationFeatures);
			return com.alibaba.fastjson2.JSONObject.parse(jsonString, deserializationFeatures);
		}

		@Setter
		@Nullable
		protected com.alibaba.fastjson2.JSONWriter.Feature[] serializationFeatures = {};

		@Setter
		@NonNull
		protected com.alibaba.fastjson2.JSONReader.Feature[] deserializationFeatures = {};
	}

	/**
	 * gson
	 *
	 * @author Zero (cnzeropro@163.com)
	 * @since 2025/3/3
	 */
	@NoArgsConstructor
	public static class GsonArgumentResolver implements HandlerMethodArgumentResolver {
		public GsonArgumentResolver(com.google.gson.Gson gson) {
			setGson(gson);
		}

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterType().isAssignableFrom(com.google.gson.JsonObject.class) && parameter.hasParameterAnnotation(JsonObjectParam.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			Map<String, String[]> parameterMap = webRequest.getParameterMap();
			Map<String, Object> simplifiedMap = RequestUtil.simplify(parameterMap, true);
			return gson.toJsonTree(simplifiedMap);
		}

		@Setter
		@NonNull
		protected com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.setLenient()
			.serializeNulls()
			.setVersion(1.0)
			.create();
	}

	/**
	 * hutool-json
	 *
	 * @author Zero (cnzeropro@163.com)
	 * @since 2025/3/3
	 */
	@NoArgsConstructor
	public static class HutoolJsonArgumentResolver implements HandlerMethodArgumentResolver {
		public HutoolJsonArgumentResolver(cn.hutool.json.JSONConfig jsonConfig) {
			setJsonConfig(jsonConfig);
		}

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterType().isAssignableFrom(cn.hutool.json.JSONObject.class) && parameter.hasParameterAnnotation(JsonObjectParam.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			Map<String, String[]> parameterMap = webRequest.getParameterMap();
			Map<String, Object> simplifiedMap = RequestUtil.simplify(parameterMap, true);
			return cn.hutool.json.JSONUtil.parseObj(simplifiedMap, jsonConfig);
		}

		@Setter
		@Nullable
		protected cn.hutool.json.JSONConfig jsonConfig = cn.hutool.json.JSONConfig.create();
	}
}
