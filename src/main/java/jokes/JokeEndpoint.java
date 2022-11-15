package jokes;

import com.google.gson.Gson;
import dtos.JokeDTO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

@Path("jokes")
public class JokeEndpoint {
    private static final JokeFetcher FETCHER = new JokeFetcher();
    private static final Gson GSON = new Gson();

    @GET
    @Path("dad-joke")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDadJoke() throws ExecutionException, InterruptedException {
        JokeDTO jokeDTO = FETCHER.getJoke(
                "Dad Joke",
                "https://icanhazdadjoke.com",
                "https://icanhazdadjoke.com/j/",
                "https://icanhazdadjoke.com/static/smile.svg"
        );
        String jokeJSON = GSON.toJson(jokeDTO);
        System.out.println(jokeJSON);
        return Response.ok(jokeJSON).build();
    }

    @GET
    @Path("chuck-norris")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChuckJoke() throws ExecutionException, InterruptedException {
        JokeDTO jokeDTO = FETCHER.getJoke(
                "Chuck Norris Joke",
                "https://api.chucknorris.io/jokes/random",
                "https://api.chucknorris.io/jokes/",
                "https://api.chucknorris.io/img/chucknorris_logo_coloured_small@2x.png"
        );
        String jokeJSON = GSON.toJson(jokeDTO);
        System.out.println(jokeJSON);
        return Response.ok(jokeJSON).build();
    }
}
