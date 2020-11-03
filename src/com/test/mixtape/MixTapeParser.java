package com.test.mixtape;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.test.constants.MixTapeConstants;
import com.test.domain.Playlist;
import com.test.domain.Song;
import com.test.domain.User;

public class MixTapeParser {

	static Map<String, Playlist> idToPlayListMap = new HashMap<>();
	static Map<String, Song> songIdToDetailMap = new HashMap<>();
	static Map<String, User> userIdToDetailsMap = new HashMap<>();

	public static void readDataFromSrcJson(String relativeFilePath) {

		System.out.println("Parsing Src file: " + relativeFilePath);
		Object obj = null;
		try {
			obj = new JSONParser().parse(new FileReader(relativeFilePath));

		} catch (IOException | ParseException e) {
			System.out.println("Exception while reading/parsing input json file" + e.getMessage());
			e.printStackTrace();
		}

		JSONObject jsonObj = (JSONObject) obj;

		JSONArray userArray = (JSONArray) jsonObj.get(MixTapeConstants.KEY_USERS);
		JSONArray playListJsonArray = (JSONArray) jsonObj.get(MixTapeConstants.KEY_PLAYLISTS);
		JSONArray songsJsonArray = (JSONArray) jsonObj.get(MixTapeConstants.KEY_SONGS);

		parsePlayListDetails(playListJsonArray);
		parseSongsArray(songsJsonArray);
		parseUserDetails(userArray);

	}

	private static void parseSongsArray(JSONArray songsJsonArray) {

		for (int i = 0; i < songsJsonArray.size(); i++) {
			JSONObject songJsonObj = (JSONObject) songsJsonArray.get(i);
			String songId = (String) songJsonObj.get(MixTapeConstants.KEY_ID);
			String artistName = (String) songJsonObj.get(MixTapeConstants.KEY_SONG_ARTIST);
			String title = (String) songJsonObj.get(MixTapeConstants.KEY_SONG_TITLE);

			Song song = new Song(songId, artistName, title);
			songIdToDetailMap.put(songId, song);
		}

	}

	private static void parsePlayListDetails(JSONArray playListArray) {

		for (int i = 0; i < playListArray.size(); i++) {

			JSONObject playListJsonObj = (JSONObject) playListArray.get(i);

			String playListId = (String) playListJsonObj.get(MixTapeConstants.KEY_ID);
			String userId = (String) playListJsonObj.get(MixTapeConstants.KEY_PLAYLIST_USER_ID);
			JSONArray songIdsJsonArray = (JSONArray) playListJsonObj.get(MixTapeConstants.KEY_PLAYLIST_SONG_IDS);

			Set<String> songIdSet = new HashSet<>();
			for (int songIdIndex = 0; songIdIndex < songIdsJsonArray.size(); songIdIndex++) {
				String songId = (String) songIdsJsonArray.get(songIdIndex);
				songIdSet.add(songId);
			}

			Playlist playlistObj = new Playlist(playListId, userId, songIdSet);
			idToPlayListMap.put(playListId, playlistObj);
		}
	}

	private static void parseUserDetails(JSONArray userArray) {
		for (int i = 0; i < userArray.size(); i++) {
			JSONObject userJsonObj = (JSONObject) userArray.get(i);
			String userId = (String) userJsonObj.get(MixTapeConstants.KEY_ID);
			String name = (String) userJsonObj.get(MixTapeConstants.KEY_USER_NAME);
			User user = new User(userId, name);
			userIdToDetailsMap.put(userId, user);
		}
	}

	public static void readDataFromChangesJson(String relativeFilePath) {

		System.out.println("Parsing Changes file: " + relativeFilePath);
		Object obj = null;
		try {
			obj = new JSONParser().parse(new FileReader(relativeFilePath));

		} catch (IOException | ParseException e) {
			System.out.println("Exception while reading/parsing input json file" + e.getMessage());
			e.printStackTrace();
		}

		JSONObject jsonObj = (JSONObject) obj;

		JSONArray playListJsonArray = (JSONArray) jsonObj.get(MixTapeConstants.KEY_CHANGES_CMD_ADD_PLAYLISTS);
		JSONArray playListIdArray = (JSONArray) jsonObj.get(MixTapeConstants.KEY_CHANGES_CMD_REMOVE_PLAYLIST);
		JSONObject addSongsToPlayListJsonObj = (JSONObject) jsonObj
				.get(MixTapeConstants.KEY_CHANGES_CMD_ADD_EXISTING_SONG_TO_EXISTING_PLAYLIST);

		addPlayLists(playListJsonArray);
		removePlayList(playListIdArray);
		addSongsToPlayList(addSongsToPlayListJsonObj);
	}

	public static void addPlayLists(JSONArray playListJsonArray) {
		for (int i = 0; i < playListJsonArray.size(); i++) {

			JSONObject playListJsonObj = (JSONObject) playListJsonArray.get(i);
			String userId = (String) playListJsonObj.get(MixTapeConstants.KEY_PLAYLIST_USER_ID);
			JSONArray songIdJsonArray = (JSONArray) playListJsonObj.get(MixTapeConstants.KEY_PLAYLIST_SONG_IDS);

			if (userIdToDetailsMap.get(userId) == null) {
				System.out.println("Please provide valid User Id: " + userId);
			}

			Set<String> songIdSet = new HashSet<>();
			for (int songIdIndex = 0; songIdIndex < songIdJsonArray.size(); songIdIndex++) {
				String id = (String) songIdJsonArray.get(songIdIndex);
				if (songIdToDetailMap.get(id) == null) {
					System.out.println("Please provide valid User Id: " + id);
				} else {
					songIdSet.add(id);
				}
			}

			Playlist playlist = new Playlist(userId, songIdSet);
			idToPlayListMap.put(playlist.getPlaylistId(), playlist);
		}

	}

	public static void removePlayList(JSONArray playListIdArray) {

		for (int i = 0; i < playListIdArray.size(); i++) {
			String id = (String) playListIdArray.get(i);
			if (idToPlayListMap.get(id) != null) {
				idToPlayListMap.remove(id);
			} else {
				System.out.println("Please provide valid Id: " + id);
			}
		}
	}

	public static void addSongsToPlayList(JSONObject addSongsToPlayListJsonObj) {

		String songIdToAdd = (String) addSongsToPlayListJsonObj.get(MixTapeConstants.KEY_CHANGES_SONG_ID);
		String playListIdToAdd = (String) addSongsToPlayListJsonObj.get(MixTapeConstants.KEY_CHANGES_PLAYLIST_ID);

		Playlist playListObj = idToPlayListMap.get(playListIdToAdd);
		if (songIdToDetailMap.get(songIdToAdd) == null || playListObj == null) {
			System.out.println("Please provide valid Ids");
		}

		playListObj.getSongIds().add(songIdToAdd);
		idToPlayListMap.put(playListIdToAdd, playListObj);
	}

	@SuppressWarnings("unchecked")
	public static void generateOutputJson(String outputFilePath) {

		JSONObject jsonOutputObj = new JSONObject();

		JSONArray userJsonArray = new JSONArray();

		for (User user : userIdToDetailsMap.values()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(MixTapeConstants.KEY_ID, user.getId());
			jsonObject.put(MixTapeConstants.KEY_USER_NAME, user.getName());
			userJsonArray.add(jsonObject);
		}
		jsonOutputObj.put(MixTapeConstants.KEY_USERS, userJsonArray);

		JSONArray playlistJsonArray = new JSONArray();
		for (Playlist playlist : idToPlayListMap.values()) {
			JSONObject playListJsonObject = new JSONObject();
			playListJsonObject.put(MixTapeConstants.KEY_ID, playlist.getPlaylistId());
			playListJsonObject.put(MixTapeConstants.KEY_PLAYLIST_USER_ID, playlist.getUserId());

			JSONArray songIdJsonArray = new JSONArray();
			for (String songId : playlist.getSongIds()) {
				songIdJsonArray.add(songId);
			}
			playListJsonObject.put(MixTapeConstants.KEY_PLAYLIST_SONG_IDS, songIdJsonArray);
			playlistJsonArray.add(playListJsonObject);
		}
		jsonOutputObj.put(MixTapeConstants.KEY_PLAYLISTS, playlistJsonArray);

		JSONArray songsJsonArray = new JSONArray();
		for (Song song : songIdToDetailMap.values()) {
			JSONObject songJsonObj = new JSONObject();
			songJsonObj.put(MixTapeConstants.KEY_ID, song.getId());
			songJsonObj.put(MixTapeConstants.KEY_SONG_ARTIST, song.getArtist());
			songJsonObj.put(MixTapeConstants.KEY_SONG_TITLE, song.getTitle());
			songsJsonArray.add(songJsonObj);
		}
		jsonOutputObj.put(MixTapeConstants.KEY_SONGS, songsJsonArray);

		PrintWriter pw = null;
		try {
			System.out.println("Writing output file: " + outputFilePath);
			pw = new PrintWriter(outputFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pw.write(jsonOutputObj.toJSONString());

		pw.flush();
		pw.close();
	}

}
