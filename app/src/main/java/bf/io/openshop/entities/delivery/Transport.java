package bf.io.openshop.entities.delivery;

public class Transport {

    private String icon;
    private String text;

    public Transport() {
    }

    public Transport(String icon, String text) {
        this.icon = icon;
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transport)) return false;

        Transport transport = (Transport) o;

        if (getIcon() != null ? !getIcon().equals(transport.getIcon()) : transport.getIcon() != null) return false;
        return !(getText() != null ? !getText().equals(transport.getText()) : transport.getText() != null);

    }

    @Override
    public int hashCode() {
        int result = getIcon() != null ? getIcon().hashCode() : 0;
        result = 31 * result + (getText() != null ? getText().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "icon='" + icon + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
