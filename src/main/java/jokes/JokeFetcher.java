package jokes;

import com.google.gson.Gson;
import utils.HttpUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JokeFetcher {
    Gson gson = new Gson();
    ExecutorService executableService = Executors.newFixedThreadPool(2);

    public <T> Object getJoke(String url, T typeOfDTO) throws ExecutionException, InterruptedException {
        return executableService.submit( () -> {
            try {
                return gson.fromJson(HttpUtils.fetchData(url), (Type) typeOfDTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).get();
    }
}
