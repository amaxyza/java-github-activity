package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserActivity {
    final private static Gson gson =  new Gson().newBuilder().setPrettyPrinting().create();
    private final String username;

    UserActivity(String username){
        this.username = username;
    }

    private Optional<String> requestActivity() throws URISyntaxException, IOException, InterruptedException {
            URI githubUserURI = new URI("https://api.github.com/users/" + username + "/events");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(githubUserURI)
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return (response.body().isEmpty())
                    ? Optional.empty()
                    : Optional.of(response.body());
    }

    private List<UserActivityResponse> responseToObject(String responseJson) {
        var userActivityListType = new TypeToken<List<UserActivityResponse>>() {}.getType();
        return gson.fromJson(responseJson, userActivityListType);
    }

    public void printUserActivity() {
        try {
            var responseString = requestActivity().orElseThrow();
            var responseObject =  responseToObject(responseString);

            var grouped = new LinkedHashMap<String, Integer>();
            responseObject.forEach(event -> {
                String key = event.type() + "|" + event.repo().name();
                grouped.merge(key, 1, Integer::sum);
            });

            grouped.forEach((key, count) -> {
                String[] parts = key.split("\\|", 2);
                String type = parts[0];
                String repo = parts[1];
                String message = switch (type) {
                    case "PushEvent" -> "Pushed " + count + " commit(s) to " + repo;
                    case "PullRequestEvent" -> "Opened " + count + " pull request(s) in " + repo;
                    case "IssuesEvent" -> "Opened " + count + " issue(s) in " + repo;
                    case "WatchEvent" -> "Starred " + repo;
                    case "ForkEvent" -> "Forked " + repo;
                    case "CreateEvent" -> "Created " + count + " branch(es)/tag(s) in " + repo;
                    case "DeleteEvent" -> "Deleted " + count + " branch(es)/tag(s) in " + repo;
                    case "IssueCommentEvent" -> "Commented " + count + " time(s) on issues in " + repo;
                    case "PullRequestReviewEvent" -> "Reviewed " + count + " pull request(s) in " + repo;
                    case "PullRequestReviewCommentEvent" -> "Commented " + count + " time(s) on pull request reviews in " + repo;
                    case "CommitCommentEvent" -> "Commented " + count + " time(s) on commits in " + repo;
                    case "ReleaseEvent" -> "Published " + count + " release(s) in " + repo;
                    case "PublicEvent" -> "Made " + repo + " public";
                    case "MemberEvent" -> "Added " + count + " member(s) to " + repo;
                    case "GollumEvent" -> "Updated the wiki " + count + " time(s) in " + repo;
                    default -> type + " " + count + " time(s) in " + repo;
                };
                System.out.println("- " + message);
            });
        }
        catch(URISyntaxException e) {
            System.err.println("Error with user URI creation.");
        }
        catch(NoSuchElementException e) {
            System.err.println("Request to API returned no value.");
        }
        catch(Exception e) {
            System.err.println("Error sending request from client: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
