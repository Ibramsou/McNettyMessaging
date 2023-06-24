package io.github.ibramsou.netty.messaging.core.pipeline;

import io.github.ibramsou.netty.messaging.api.pipeline.PipelineFactories;
import io.github.ibramsou.netty.messaging.api.pipeline.PipelineFactory;

public class MessagingPipelineFactories implements PipelineFactories {

    @Override
    public PipelineFactory getCodecFactory() {
        return PipelineCodec::new;
    }

    @Override
    public PipelineFactory getSizerFactory() {
        return PipelineSizer::new;
    }
}
