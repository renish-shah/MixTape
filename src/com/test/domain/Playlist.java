package com.test.domain;

import java.util.Set;
import java.util.UUID;

public class Playlist {

	private String playlistId;
	private String userId;
	private Set<String> songIds;

	public Playlist(String userId, Set<String> songIds) {
		this.userId = userId;
		this.songIds = songIds;
		this.playlistId = UUID.randomUUID().toString();
	}
	
	public Playlist(String playlistId, String userId, Set<String> songIds) {
		this.playlistId = playlistId;
		this.userId = userId;
		this.songIds = songIds;
	}

	public String getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<String> getSongIds() {
		return songIds;
	}

	public void setSongIds(Set<String> songIds) {
		this.songIds = songIds;
	}

}
