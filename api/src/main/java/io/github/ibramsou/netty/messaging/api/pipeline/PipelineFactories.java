package io.github.ibramsou.netty.messaging.api.pipeline;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PipelineFactories {

    /**
     * Get the pipeline factory that is managing custom packet serialization
     * @return an implementation of {@link PipelineFactory}
     */
    PipelineFactory getCodecFactory();

    /**
     * Get the pipeline factory that is managing packet byte sizing (have to be registered before codec)
     * @return an implementation of {@link PipelineFactory}
     */
    PipelineFactory getSizerFactory();
}
