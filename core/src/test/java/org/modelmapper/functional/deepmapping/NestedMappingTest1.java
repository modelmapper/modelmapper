package org.modelmapper.functional.deepmapping;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class NestedMappingTest1 extends AbstractTest {
  private static class Artist {
    String name;
  }

  private static class ArtistDTO {
    String name;
  }

  private static class Song {
    Artist artist;
  }

  private static class SongDTO1 {
    ArtistDTO artist;
  }

  private static class SongDTO2 {
    String artistName;
  }

  /**
   * Maps ArtistDTO/name to Arist/name
   */
  public void shouldMapArtistToArtistDTO() {
    Artist artist = new Artist();
    artist.name = "joe";
    ArtistDTO dto = modelMapper.map(artist, ArtistDTO.class);
    assertEquals(dto.name, artist.name);
  }

  /**
   * Maps SongDTO2/artistName from Artist/name
   */
  public void shouldMapArtistToSongDTO2() {
    Artist artist = new Artist();
    artist.name = "joe";
    SongDTO2 dto = modelMapper.map(artist, SongDTO2.class);
    assertEquals(dto.artistName, artist.name);
  }

  /**
   * Maps Song/artist/name from SongDTO/artistName
   */
  public void shouldMapSongDTO2ToSong() {
    SongDTO2 dto = new SongDTO2();
    dto.artistName = "joe";
    Song song = modelMapper.map(dto, Song.class);
    assertEquals(song.artist.name, dto.artistName);
  }

  /**
   * Maps Artist/name from SongDTO/artistName
   */
  public void shouldMapSongDTOToArtist() {
    SongDTO2 dto = new SongDTO2();
    dto.artistName = "joe";
    Artist artist = modelMapper.map(dto, Artist.class);
    assertEquals(artist.name, dto.artistName);
  }

  /**
   * Maps SongDTO1/artist/name from Song/artist/name
   */
  public void shouldMapSongToSongDTO1() {
    Song song = new Song();
    song.artist = new Artist();
    song.artist.name = "joe";
    SongDTO1 dto = modelMapper.map(song, SongDTO1.class);
    assertEquals(dto.artist.name, song.artist.name);
  }

  /**
   * Maps SongDTO/artistName from Song/artist/name
   */
  public void shouldMapSongToSongDTO2() {
    Song song = new Song();
    song.artist = new Artist();
    song.artist.name = "joe";
    SongDTO2 dto = modelMapper.map(song, SongDTO2.class);
    assertEquals(dto.artistName, song.artist.name);
  }
}
