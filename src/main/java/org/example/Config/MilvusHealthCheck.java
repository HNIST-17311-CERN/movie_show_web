//package org.example.Config;
//
//import io.milvus.client.MilvusServiceClient;
//import io.milvus.common.clientenum.ConsistencyLevelEnum;
//import io.milvus.grpc.DataType;
//import io.milvus.grpc.ShowCollectionsResponse;
//import io.milvus.param.IndexType;
//import io.milvus.param.MetricType;
//import io.milvus.param.RpcStatus;
//import io.milvus.param.R;
//import io.milvus.param.collection.*;
//import io.milvus.param.index.CreateIndexParam;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MilvusHealthCheck {
//
//    private static final String COLLECTION_NAME = "rag_documents";
//    private static final int EMBEDDING_DIM = 1024;
//
//    @Autowired
//    private MilvusServiceClient milvusServiceClient;
//
//    @PostConstruct
//    public void init() {
//        testConnection();
//        initCollection();
//    }
//
//    private void testConnection() {
//        System.out.println("=== 测试 Milvus 连接 ===");
//        R<ShowCollectionsResponse> resp = milvusServiceClient.showCollections(
//                ShowCollectionsParam.newBuilder().build()
//        );
//        if (resp.getStatus() == 0) {
//            System.out.println("Milvus 连接成功！现有 Collections: " + resp.getData().getCollectionNamesList());
//        } else {
//            System.err.println("Milvus 连接失败: " + resp.getMessage());
//        }
//    }
//
//    private void initCollection() {
//        // 检查是否已存在
//        R<Boolean> hasColl = milvusServiceClient.hasCollection(
//                HasCollectionParam.newBuilder().withCollectionName(COLLECTION_NAME).build()
//        );
//        if (hasColl.getData() != null && hasColl.getData()) {
//            System.out.println("Collection [" + COLLECTION_NAME + "] 已存在，跳过创建");
//            return;
//        }
//
//        // 定义字段
//        FieldType idField = FieldType.newBuilder()
//                .withName("id")
//                .withDataType(DataType.Int64)
//                .withPrimaryKey(true)
//                .withAutoID(true)
//                .build();
//
//        FieldType docIdField = FieldType.newBuilder()
//                .withName("doc_id")
//                .withDataType(DataType.VarChar)
//                .withMaxLength(128)
//                .build();
//
//        FieldType chunkIdxField = FieldType.newBuilder()
//                .withName("chunk_idx")
//                .withDataType(DataType.Int32)
//                .build();
//
//        FieldType chunkTextField = FieldType.newBuilder()
//                .withName("chunk_text")
//                .withDataType(DataType.VarChar)
//                .withMaxLength(65535)
//                .build();
//
//        FieldType embeddingField = FieldType.newBuilder()
//                .withName("embedding")
//                .withDataType(DataType.FloatVector)
//                .withDimension(EMBEDDING_DIM)
//                .build();
//
//        FieldType metadataField = FieldType.newBuilder()
//                .withName("metadata")
//                .withDataType(DataType.JSON)
//                .build();
//
//        // 创建 Collection
//        CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
//                .withCollectionName(COLLECTION_NAME)
//                .withDescription("RAG 文档知识库")
//                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
//                .addFieldType(idField)
//                .addFieldType(docIdField)
//                .addFieldType(chunkIdxField)
//                .addFieldType(chunkTextField)
//                .addFieldType(embeddingField)
//                .addFieldType(metadataField)
//                .build();
//
//        R<RpcStatus> createResp = milvusServiceClient.createCollection(createParam);
//        if (createResp.getStatus() == 0) {
//            System.out.println("Collection [" + COLLECTION_NAME + "] 创建成功");
//        } else {
//            System.err.println("Collection 创建失败: " + createResp.getMessage());
//            return;
//        }
//
//        // 为 embedding 字段建 IVF_FLAT 索引
//        CreateIndexParam indexParam = CreateIndexParam.newBuilder()
//                .withCollectionName(COLLECTION_NAME)
//                .withFieldName("embedding")
//                .withIndexType(IndexType.IVF_FLAT)
//                .withMetricType(MetricType.COSINE)
//                .withExtraParam("{\"nlist\":128}")
//                .build();
//
//        R<RpcStatus> indexResp = milvusServiceClient.createIndex(indexParam);
//        if (indexResp.getStatus() == 0) {
//            System.out.println("索引创建成功（embedding 字段, IVF_FLAT + COSINE）");
//        } else {
//            System.err.println("索引创建失败: " + indexResp.getMessage());
//        }
//
//        // 加载 Collection 到内存，准备写入/搜索
//        milvusServiceClient.loadCollection(
//                LoadCollectionParam.newBuilder().withCollectionName(COLLECTION_NAME).build()
//        );
//        System.out.println("Collection [" + COLLECTION_NAME + "] 已加载到内存");
//    }
//}
