package OOP;

public enum Seed {
  CROSS("Snowy"), NOUGHT("Stormy"), NO_SEED(" ");

  private String icon;

  private Seed(String icon) {
    this.icon = icon;
  }

  public String getIcon() {
    return icon;
  }
}
