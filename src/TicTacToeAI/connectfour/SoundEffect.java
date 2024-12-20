package TicTacToeAI.connectfour;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public enum SoundEffect {
  SNOWY("TicTacToeAI/audio/jumpclick.wav"),
  STORMY("TicTacToeAI/audio/selectclick.wav"),
  DIE("TicTacToeAI/audio/sad_trumpet.wav"),
  WIN("TicTacToeAI/audio/win.wav"),
  LOSE("TicTacToeAI/audio/lose.wav");

  public static enum Volume {
    MUTE, LOW, MEDIUM, HIGH
  }

  public static Volume volume = Volume.LOW;

  private Clip clip;

  SoundEffect(String soundFileName) {
    try {
      URL url = this.getClass().getClassLoader().getResource(soundFileName);
      if (url == null) {
        throw new IllegalArgumentException("Sound file not found: " + soundFileName);
      }
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public void play() {
    if (volume != Volume.MUTE) {
      if (clip.isRunning()) {
        clip.stop();
      }
      clip.setFramePosition(0);
      clip.start();
    }
  }

  static void init() {
    values(); // calls the constructor for all the elements
  }
}