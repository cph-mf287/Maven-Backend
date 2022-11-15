package jokes;

import com.google.gson.Gson;
import dtos.JokeDTO;
import utils.HttpUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JokeFetcher {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);
    private static final Gson GSON = new Gson();

    public JokeDTO getJoke(String type, String fromUrl, String jokeUrl, String iconUrl) throws ExecutionException, InterruptedException {
        return EXECUTOR_SERVICE.submit( () -> {
            try {
                JokeDTO jokeDTO = GSON.fromJson(HttpUtils.fetchData(fromUrl), JokeDTO.class);
                jokeDTO.setType(type);
                jokeDTO.setUrl(jokeUrl);
                jokeDTO.setIcon(iconUrl);
                return jokeDTO;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).get();
    }
}
