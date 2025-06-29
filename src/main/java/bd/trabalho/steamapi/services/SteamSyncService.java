package bd.trabalho.steamapi.services;

import bd.trabalho.steamapi.entity.Game;
import bd.trabalho.steamapi.entity.Player;
import bd.trabalho.steamapi.entity.PlayerPlaysGame;
import bd.trabalho.steamapi.entity.Tag;
import bd.trabalho.steamapi.repositories.GameRepository;
import bd.trabalho.steamapi.repositories.PlayerPlaysGameRepository;
import bd.trabalho.steamapi.repositories.PlayerRepository;
import bd.trabalho.steamapi.repositories.TagRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SteamSyncService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PlayerPlaysGameRepository playerPlaysGameRepository;
    private final TagRepository tagRepository;
    private final HttpClient httpClient;

    // These values are injected from application.properties
    @Value("${steam.api.key}")
    private String apiKey;

    @Value("${steam.api.steamid}")
    private String steamIdToSync;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    public SteamSyncService(PlayerRepository playerRepository, GameRepository gameRepository, PlayerPlaysGameRepository playerPlaysGameRepository, TagRepository tagRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.playerPlaysGameRepository = playerPlaysGameRepository;
        this.tagRepository = tagRepository;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Transactional // Ensures all database operations in this method are part of a single transaction.
    public void syncAllDataForUser() throws InterruptedException {
        if (apiKey.equals("YOUR_API_KEY_HERE")) {
            System.err.println("FATAL ERROR: Please set your 'steam.api.key' in application.properties");
            return;
        }
        System.out.println("Starting sync for SteamID: " + steamIdToSync);
        syncPlayerSummary();
        syncOwnedGames();
        System.out.println("Finished sync for SteamID: " + steamIdToSync);
    }

    private void syncPlayerSummary() throws InterruptedException {
        String url = String.format("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=", apiKey);
        String friendsUrl = String.format("https://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=%s&steamid=%s&relationship=all", apiKey, steamIdToSync);

        String jsonFriendsData = fetchData(friendsUrl);

        JSONObject friendsPayload = new JSONObject(jsonFriendsData);
        JSONArray friends = friendsPayload.getJSONObject("friendslist").getJSONArray("friends");

        // 1. Create a list to hold all the friend steam IDs
        List<String> friendIds = new ArrayList<>();

        // 2. Loop through the JSON array and add each friend's steamid to the list
        for (int i = 0; i < friends.length(); i++) {
            JSONObject friend = friends.getJSONObject(i);
            friendIds.add(friend.getString("steamid"));
        }
        // Add my own id to list
        friendIds.add(steamIdToSync);

        String friendsIdsStringQuery = String.join(",", friendIds);
        Thread.sleep(5000);
        String payload = fetchData(url+friendsIdsStringQuery);
        JSONObject meAndFriends = new JSONObject(payload);

        JSONArray playersArray = meAndFriends.getJSONObject("response").getJSONArray("players");

        List<Player> playersToSave = new ArrayList<>();
        for (int i = 0; i < playersArray.length(); i++) {
            JSONObject playerJson = playersArray.getJSONObject(i);
            Player player = playerRepository.findById(playerJson.getString("steamid")).orElse(new Player());

            // Set all the properties
            player.setSteamId(playerJson.getString("steamid"));
            player.setNickname(playerJson.getString("personaname"));
            player.setRealName(playerJson.optString("realname", null));
            player.setProfileImageUrl(playerJson.getString("avatar"));
            player.setSignDate(new Date(playerJson.optInt("timecreated", 0)));
            player.setCountry(playerJson.optString("loccountrycode"));


            playersToSave.add(player);
        }

        playerRepository.saveAll(playersToSave);
        System.out.printf("Successfully saved or updated %d players.%n", playersToSave.size());



    }

    private void syncOwnedGames() throws InterruptedException {
        // First, ensure the player exists in our DB.
        List<Player> players = (List<Player>) playerRepository.findAll();


        for  (Player player : players) {
            Thread.sleep(5000);
            String url = String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&format=json&include_appinfo=&steamid=", apiKey);
            String jsonData = fetchData(url+player.getSteamId());

            JSONObject root = new JSONObject(jsonData).getJSONObject("response");
            JSONArray games = root.getJSONArray("games");

            List<Game> gamesToSave = new ArrayList<>();
            for(int i = 0; i < games.length(); i++) {
                JSONObject game = games.getJSONObject(i);
                String app_id = game.optString("appid");
                String app_name = game.optString("name");
                Thread.sleep(5000);
                String gameUrl = String.format("https://store.steampowered.com/api/appdetails?appids=%s", app_id);
                String data = fetchData(gameUrl);

                JSONObject gamePayload = new JSONObject(data).getJSONObject(app_id).getJSONObject("data");
                Game g = gameRepository.findById(gamePayload.optString(app_id)).orElse(new Game());

                g.setAppId(app_id);
                g.setName(app_name);
                g.setDescription(gamePayload.optString("short_description"));

                double price = 0.0;
                JSONObject priceObject = gamePayload.optJSONObject("price_overview", null);
                if (priceObject != null) {
                    price = priceObject.optDouble("initial") / 100.0;
                }
                g.setPrice(price);
//                String release = gamePayload.getJSONObject("release_date").getString("date");
                g.setReleaseDate(gamePayload.getJSONObject("release_date").getString("date"));

                gamesToSave.add(g);

                JSONArray tags = gamePayload.optJSONArray("categories");

                for (int j = 0; j < tags.length(); j++) {
                    JSONObject tagObject = tags.getJSONObject(j);
                    int tagId = tagObject.getInt("id");
                    String tagName = tagObject.getString("description");

                    Tag t = tagRepository.findById(tagId).orElse(new Tag());
                    t.setName(tagName);
                    t.setMarkerId(tagId);
                    tagRepository.save(t);

                    g.addTag(t);
                }

                // Save relation 'plays'
                playerPlaysGameRepository.save(new PlayerPlaysGame(player, g, gamePayload.getInt("playtime_forever")));

                // Save relation 'owns'
                player.getGames().add(g);

            }
            gameRepository.saveAll(gamesToSave);
        }

//        if (jsonData == null) return;
//
//        JSONObject root = new JSONObject(jsonData).getJSONObject("response");
//        if (!root.has("games")) {
//            System.out.println("User " + player.getPersonaName() + "'s game library is private or empty.");
//            return;
//        }
//
//        JSONArray games = root.getJSONArray("games");
//        System.out.printf("Found %d games to process for user %s.%n", root.getInt("game_count"), player.getPersonaName());
//
//        for (int i = 0; i < games.length(); i++) {
//            JSONObject gameJson = games.getJSONObject(i);
//            Integer appId = gameJson.getInt("appid");
//            String name = gameJson.getString("name");
//            int playtime = gameJson.getInt("playtime_forever");
//
//            // Find or create the Game entity
//            Game game = gameRepository.findById(appId).orElseGet(() -> gameRepository.save(new Game(appId, name)));
//
//            // Find or create the link between Player and Game
//            PlayerGame playerGame = playerGameRepository.findByPlayerAndGame(player, game)
//                    .orElseGet(() -> new PlayerGame(player, game, playtime));
//
//            // Always update the playtime
//            playerGame.setPlaytimeForever(playtime);
//
//            playerGameRepository.save(playerGame);
//        }
    }

    private String fetchData(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.printf("Error fetching %s. Status: %d%n", url, response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

