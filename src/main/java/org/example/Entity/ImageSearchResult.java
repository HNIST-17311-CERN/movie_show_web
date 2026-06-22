package org.example.Entity;

import java.util.List;

public class ImageSearchResult {

    private String analysis;
    private MovieGuess movieGuess;
    private List<String> genreGuess;
    private String eraGuess;
    private VisualFeatures visualFeatures;

    public static class MovieGuess {
        private String name;
        private String year;
        private String confidence;
        private String reason;
        private MovieMatched matchedMovie;
        private List<MovieMatched> similarMovies;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }

        public String getConfidence() { return confidence; }
        public void setConfidence(String confidence) { this.confidence = confidence; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public MovieMatched getMatchedMovie() { return matchedMovie; }
        public void setMatchedMovie(MovieMatched matchedMovie) { this.matchedMovie = matchedMovie; }

        public List<MovieMatched> getSimilarMovies() { return similarMovies; }
        public void setSimilarMovies(List<MovieMatched> similarMovies) { this.similarMovies = similarMovies; }
    }

    public static class MovieMatched {
        private Long id;
        private String name;
        private String cover;
        private String director;
        private String type;
        private String releaseDate;
        private String description;
        private Double similarity;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCover() { return cover; }
        public void setCover(String cover) { this.cover = cover; }

        public String getDirector() { return director; }
        public void setDirector(String director) { this.director = director; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getReleaseDate() { return releaseDate; }
        public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Double getSimilarity() { return similarity; }
        public void setSimilarity(Double similarity) { this.similarity = similarity; }
    }

    public static class VisualFeatures {
        private boolean hasText;
        private String textContent;
        private boolean hasActor;
        private String actorDesc;
        private String colorTone;
        private String sceneType;

        public boolean isHasText() { return hasText; }
        public void setHasText(boolean hasText) { this.hasText = hasText; }

        public String getTextContent() { return textContent; }
        public void setTextContent(String textContent) { this.textContent = textContent; }

        public boolean isHasActor() { return hasActor; }
        public void setHasActor(boolean hasActor) { this.hasActor = hasActor; }

        public String getActorDesc() { return actorDesc; }
        public void setActorDesc(String actorDesc) { this.actorDesc = actorDesc; }

        public String getColorTone() { return colorTone; }
        public void setColorTone(String colorTone) { this.colorTone = colorTone; }

        public String getSceneType() { return sceneType; }
        public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    }

    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }

    public MovieGuess getMovieGuess() { return movieGuess; }
    public void setMovieGuess(MovieGuess movieGuess) { this.movieGuess = movieGuess; }

    public List<String> getGenreGuess() { return genreGuess; }
    public void setGenreGuess(List<String> genreGuess) { this.genreGuess = genreGuess; }

    public String getEraGuess() { return eraGuess; }
    public void setEraGuess(String eraGuess) { this.eraGuess = eraGuess; }

    public VisualFeatures getVisualFeatures() { return visualFeatures; }
    public void setVisualFeatures(VisualFeatures visualFeatures) { this.visualFeatures = visualFeatures; }
}
