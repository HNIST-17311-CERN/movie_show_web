//package org.example.Config;
//
//import io.milvus.client.MilvusServiceClient;
//import io.milvus.param.ConnectParam;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MilvusConfig {
//
//    @Bean
//    public MilvusServiceClient milvusServiceClient()
//    {
//        return new MilvusServiceClient(
//                ConnectParam.newBuilder()
//                        .withHost("localhost")
//                        .withPort(19530)
//                        .build()
//        );
//    }
//}
