//package org.example.AI;
//
//import io.milvus.client.MilvusServiceClient;
//import io.milvus.grpc.SearchResults;
//import io.milvus.param.MetricType;
//import io.milvus.param.R;
//import io.milvus.param.dml.SearchParam;
//import io.milvus.response.SearchResultsWrapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class RetrievalService {
//
//    private static final String COLLECTION = "rag_documents";
//    private static final int TOP_K = 5;
//
//    @Autowired
//    private MilvusServiceClient milvusClient;
//
//    @Autowired
//    private EmbeddingService embeddingService;
//
//    public List<String> search(String question) {
//        float[] queryVector = embeddingService.embed(question);
//        if (queryVector.length == 0) return List.of();
//
//        List<Float> vectorList = new ArrayList<>(queryVector.length);
//        for (float v : queryVector) vectorList.add(v);
//
//        SearchParam searchParam = SearchParam.newBuilder()
//                .withCollectionName(COLLECTION)
//                .withVectorFieldName("embedding")
//                .withVectors(List.of(vectorList))
//                .withTopK(TOP_K)
//                .withMetricType(MetricType.COSINE)
//                .addOutField("chunk_text")
//                .addOutField("doc_id")
//                .build();
//
//        R<SearchResults> resp = milvusClient.search(searchParam);
//        if (resp.getStatus() != 0) {
//            System.err.println("检索失败: " + resp.getMessage());
//            return List.of();
//        }
//
//        SearchResultsWrapper wrapper = new SearchResultsWrapper(resp.getData().getResults());
//        List<SearchResultsWrapper.IDScore> idScores = wrapper.getIDScore(0);
//        List<String> results = new ArrayList<>();
//        for (int i = 0; i < idScores.size(); i++) {
//            Object field = wrapper.getFieldData("chunk_text", 0);
//            if (field instanceof List) {
//                List<?> fieldList = (List<?>) field;
//                if (i < fieldList.size()) {
//                    results.add(fieldList.get(i).toString());
//                }
//            }
//        }
//        return results;
//    }
//}
