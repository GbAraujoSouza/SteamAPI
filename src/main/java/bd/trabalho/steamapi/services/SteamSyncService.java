package bd.trabalho.steamapi.services;

import bd.trabalho.steamapi.entity.Player;
import bd.trabalho.steamapi.repositories.GameRepository;
import bd.trabalho.steamapi.repositories.PlayerPlaysGameRepository;
import bd.trabalho.steamapi.repositories.PlayerRepository;
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
import java.util.*;

@Service
public class SteamSyncService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PlayerPlaysGameRepository playerPlaysGameRepository;
    private final HttpClient httpClient;

    // These values are injected from application.properties
    @Value("${steam.api.key}")
    private String apiKey;

    @Value("${steam.api.steamid}")
    private String steamIdToSync;

    public SteamSyncService(PlayerRepository playerRepository, GameRepository gameRepository, PlayerPlaysGameRepository playerPlaysGameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.playerPlaysGameRepository = playerPlaysGameRepository;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Transactional // Ensures all database operations in this method are part of a single transaction.
    public void syncAllDataForUser() {
        if (apiKey.equals("YOUR_API_KEY_HERE")) {
            System.err.println("FATAL ERROR: Please set your 'steam.api.key' in application.properties");
            return;
        }
        System.out.println("Starting sync for SteamID: " + steamIdToSync);
        syncPlayerSummary();
//        syncOwnedGames();
        System.out.println("Finished sync for SteamID: " + steamIdToSync);
    }

    private void syncPlayerSummary() {
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
            player.setSignDate(new Date(playerJson.getInt("timecreated")));
            player.setCountry(playerJson.optString("loccountrycode"));


            playersToSave.add(player);
        }

        playerRepository.saveAll(playersToSave);
        System.out.printf("Successfully saved or updated %d players.%n", playersToSave.size());



    }

//    private void syncOwnedGames() {
//        // First, ensure the player exists in our DB.
//        Optional<Player> playerOpt = playerRepository.findById(steamIdToSync);
//        if (playerOpt.isEmpty()) {
//            System.err.println("Cannot sync games. Player with ID " + steamIdToSync + " not found. Run player sync first.");
//            return;
//        }
//        Player player = playerOpt.get();
//
//        String url = String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&format=json&include_appinfo=true", apiKey, steamIdToSync);
//        String jsonData = fetchData(url);
//
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
//    }

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

