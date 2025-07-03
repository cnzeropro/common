package org.zero.common.core.extension.feign;

import feign.Contract;
import feign.Logger;
import feign.QueryMapEncoder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringFormEncoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.cloud.openfeign.support.AbstractFormWriter;
import org.springframework.cloud.openfeign.support.FeignEncoderProperties;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.PageableSpringEncoder;
import org.springframework.cloud.openfeign.support.PageableSpringQueryMapEncoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static feign.form.ContentType.MULTIPART;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/2
 */
public abstract class AbstractFeignConfig {
    @Autowired
    protected ObjectFactory<HttpMessageConverters> httpMessageConvertersProvider;
    @Autowired
    protected ObjectProvider<HttpMessageConverterCustomizer> httpMessageConverterCustomizerProvider;
    @Autowired(required = false)
    protected AbstractFormWriter formWriter;
    @Autowired(required = false)
    protected SpringDataWebProperties springDataWebProperties;
    @Autowired(required = false)
    protected FeignEncoderProperties feignEncoderProperties;
    @Autowired(required = false)
    private FeignClientProperties feignClientProperties;
    @Autowired(required = false)
    protected List<AnnotatedParameterProcessor> annotatedParameterProcessors = new ArrayList<>();
    @Autowired(required = false)
    protected List<FeignFormatterRegistrar> feignFormatterRegistrars = new ArrayList<>();
    @Autowired(required = false)
    protected Logger logger;

    /**
     * copy from {@link org.springframework.cloud.openfeign.FeignClientsConfiguration#feignDecoder(ObjectProvider)}
     */
    public Decoder defaultDecoder() {
        return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(httpMessageConvertersProvider, httpMessageConverterCustomizerProvider)));
    }

    /**
     * copy from {@link org.springframework.cloud.openfeign.FeignClientsConfiguration#feignEncoder(ObjectProvider, ObjectProvider)}
     */
    public Encoder defaultEncoder() {
        SpringEncoder springEncoder;
        if (Objects.nonNull(formWriter)) {
            springEncoder = new SpringEncoder(new SpringPojoFormEncoder(formWriter), httpMessageConvertersProvider, feignEncoderProperties, httpMessageConverterCustomizerProvider);
        } else {
            springEncoder = new SpringEncoder(new SpringFormEncoder(), httpMessageConvertersProvider, feignEncoderProperties, httpMessageConverterCustomizerProvider);
        }
        if (isClassPresent("org.springframework.data.domain.Pageable")) {
            PageableSpringEncoder pageableSpringEncoder = new PageableSpringEncoder(springEncoder);
            if (Objects.nonNull(springDataWebProperties)) {
                pageableSpringEncoder.setPageParameter(springDataWebProperties.getPageable().getPageParameter());
                pageableSpringEncoder.setSizeParameter(springDataWebProperties.getPageable().getSizeParameter());
                pageableSpringEncoder.setSortParameter(springDataWebProperties.getSort().getSortParameter());
            }
            return pageableSpringEncoder;
        }
        return springEncoder;
    }

    /**
     * copy from {@link org.springframework.cloud.openfeign.FeignClientsConfiguration#feignQueryMapEncoderPageable()}
     */
    public QueryMapEncoder defaultQueryMapEncoder() {
        if (isClassPresent("org.springframework.data.domain.Pageable")) {
            PageableSpringQueryMapEncoder queryMapEncoder = new PageableSpringQueryMapEncoder();
            if (Objects.nonNull(springDataWebProperties)) {
                queryMapEncoder.setPageParameter(springDataWebProperties.getPageable().getPageParameter());
                queryMapEncoder.setSizeParameter(springDataWebProperties.getPageable().getSizeParameter());
                queryMapEncoder.setSortParameter(springDataWebProperties.getSort().getSortParameter());
            }
            return queryMapEncoder;
        }
        return new QueryMapEncoder.Default();
    }

    /**
     * copy from {@link org.springframework.cloud.openfeign.FeignClientsConfiguration#feignContract(ConversionService)}
     */
    public Contract defaultContract() {
        boolean decodeSlash = Objects.isNull(feignClientProperties) || feignClientProperties.isDecodeSlash();
        return new SpringMvcContract(annotatedParameterProcessors, this.defaultConversionService(), decodeSlash);
    }

    /**
     * copy from {@link org.springframework.cloud.openfeign.FeignClientsConfiguration#feignConversionService()}
     */
    public FormattingConversionService defaultConversionService() {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        for (FeignFormatterRegistrar feignFormatterRegistrar : feignFormatterRegistrars) {
            feignFormatterRegistrar.registerFormatters(conversionService);
        }
        return conversionService;
    }

    protected boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    protected static class SpringPojoFormEncoder extends SpringFormEncoder {
        SpringPojoFormEncoder(AbstractFormWriter formWriter) {
            super();
            MultipartFormContentProcessor processor = (MultipartFormContentProcessor) getContentProcessor(MULTIPART);
            processor.addFirstWriter(formWriter);
        }
    }
}
