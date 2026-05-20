package com.liujunming.Config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.R;
import io.milvus.param.collection.ShowCollectionsParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MilvusConfigTest {

    @Autowired
    private MilvusServiceClient milvusServiceClient;

    @Test
    public void testConnection()
    {
        System.out.println("=== 测试 Milvus 连接 ===");
        R<io.milvus.grpc.ShowCollectionsResponse> resp = milvusServiceClient.showCollections(
                ShowCollectionsParam.newBuilder().build()
        );
        if (resp.getStatus() == 0) {
            System.out.println("Milvus 连接成功！现有 Collections: " + resp.getData().getCollectionNamesList());
        } else {
            System.err.println("Milvus 连接失败: " + resp.getMessage());
        }
    }
}
