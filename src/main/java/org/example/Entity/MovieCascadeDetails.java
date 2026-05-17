package org.example.Entity;

import java.util.List;

public class MovieCascadeDetails extends Movie_details {

    private Movie_Score score;
    private List<Movie_Resource> resources;

    public Movie_Score getScore() {
        return score;
    }

    public void setScore(Movie_Score score) {
        this.score = score;
    }

    public List<Movie_Resource> getResources() {
        return resources;
    }

    public void setResources(List<Movie_Resource> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "MovieCascadeDetails{" +
                "score=" + score +
                ", resources=" + resources +
                ", base=" + super.toString() +
                '}';
    }
}
