package org.example.Entity;

public class Chunk
{
    private String docId;
    private int chunkIndex;
    private String text;

    public Chunk(String docId, int chunkIndex, String text)
    {
        this.docId = docId;
        this.chunkIndex = chunkIndex;
        this.text = text;
    }

    public String getDocId() { return docId; }
    public int getChunkIndex() { return chunkIndex; }
    public String getText() { return text; }
}
