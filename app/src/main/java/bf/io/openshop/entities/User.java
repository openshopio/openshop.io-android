package bf.io.openshop.entities;


import com.google.gson.annotations.SerializedName;

public class User {

    private long id;

    @SerializedName("fb_id")
    private String fbId;

    @SerializedName("access_token")
    private String accessToken;
    private String name;
    private String street;
    private String city;

    @SerializedName("house_number")
    private String houseNumber;
    private String zip;
    private String email;
    private String phone;
    private String gender;
    private String country;


    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (fbId != null ? !fbId.equals(user.fbId) : user.fbId != null) return false;
        if (accessToken != null ? !accessToken.equals(user.accessToken) : user.accessToken != null)
            return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (street != null ? !street.equals(user.street) : user.street != null) return false;
        if (city != null ? !city.equals(user.city) : user.city != null) return false;
        if (houseNumber != null ? !houseNumber.equals(user.houseNumber) : user.houseNumber != null)
            return false;
        if (zip != null ? !zip.equals(user.zip) : user.zip != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (phone != null ? !phone.equals(user.phone) : user.phone != null) return false;
        if (gender != null ? !gender.equals(user.gender) : user.gender != null) return false;
        return !(country != null ? !country.equals(user.country) : user.country != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (fbId != null ? fbId.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (houseNumber != null ? houseNumber.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fbId='" + fbId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", zip='" + zip + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
