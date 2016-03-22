package bf.io.openshop.entities;

public class Links {

    private String first;
    private String last;
    private String prev;
    private String next;
    private String self;

    public Links() {
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Links)) return false;

        Links links = (Links) o;

        if (getFirst() != null ? !getFirst().equals(links.getFirst()) : links.getFirst() != null) return false;
        if (getLast() != null ? !getLast().equals(links.getLast()) : links.getLast() != null) return false;
        if (getPrev() != null ? !getPrev().equals(links.getPrev()) : links.getPrev() != null) return false;
        if (getNext() != null ? !getNext().equals(links.getNext()) : links.getNext() != null) return false;
        return !(getSelf() != null ? !getSelf().equals(links.getSelf()) : links.getSelf() != null);

    }

    @Override
    public int hashCode() {
        int result = getFirst() != null ? getFirst().hashCode() : 0;
        result = 31 * result + (getLast() != null ? getLast().hashCode() : 0);
        result = 31 * result + (getPrev() != null ? getPrev().hashCode() : 0);
        result = 31 * result + (getNext() != null ? getNext().hashCode() : 0);
        result = 31 * result + (getSelf() != null ? getSelf().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Links{" +
                "first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", prev='" + prev + '\'' +
                ", next='" + next + '\'' +
                ", self='" + self + '\'' +
                '}';
    }
}
