package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.core.Constants;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 25/09/2014.
 */
public class Party extends AggregateRoot {
    protected String userId;
    protected Date date;
    protected Time time;
    protected int age;
    protected int gender;
    protected String title;
    protected Place place;
    protected String details;
    protected List<String> members = new ArrayList<String>();
    protected List<String> fans = new ArrayList<String>();
    protected List<String> comments = new ArrayList<String>();
    protected List<String> reasons = new ArrayList<String>();
    protected List<String> favorites = new ArrayList<String>();

    protected String distance;
    protected User user;

    private static final double NEARBY_THRESHOLD = 10.0;

    public Party() {
        date = new Date();
        time = new Time();
        place = new Place();
    }

    public Party(String userId,
                 String title,
                 Date date,
                 Time time,
                 int age,
                 int gender,
                 String place,
                 String details) {
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.age = age;
        this.gender = gender;
        this.place = new Place(place);
        this.details = details;
    }

    public Party(String objectId,
                 String createdAt,
                 String userId,
                 Date date,
                 Time time,
                 int age,
                 int gender,
                 String title,
                 Place place,
                 String details,
                 List<String> members,
                 List<String> fans,
                 List<String> comments,
                 List<String> reasons,
                 List<String> favorites) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.age = age;
        this.gender = gender;
        this.title = title;
        this.place = place;
        this.details = details;
        this.members.clear();
        this.members.addAll(members);
        this.fans.clear();
        this.fans.addAll(fans);
        this.comments.clear();
        this.comments.addAll(comments);
        this.reasons.clear();
        this.reasons.addAll(reasons);
        this.favorites.clear();
        this.favorites.addAll(favorites);
    }

    public String getUserId() {
        return userId;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public String getDateTime() {
        DateTime dt = new DateTime(date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute());
        return dt.toDateTime(DateTimeZone.UTC).toString(Constants.DateTime.FORMAT);
    }

    public int getAge() {
        return age;
    }

    public int getGender() {
        return gender;
    }

    public String getTitle() {
        return title;
    }

    public Place getPlace() {
        return place;
    }

    public String getDetails() {
        return details;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getFans() {
        return fans;
    }

    public List<String> getComments() { return comments; }

    public List<String> getReasons() {
        return reasons;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(double cLat, double cLong) {
        double pLat = place.getLatitude();
        double pLong = place.getLongitude();
        double lat1 = (Math.PI/180)*cLat;
        double lat2 = (Math.PI/180)*pLat;

        double lon1 = (Math.PI/180)*cLong;
        double lon2 = (Math.PI/180)*pLong;

        double R = 6371;
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
        DecimalFormat df = new DecimalFormat("#.#");
        distance = df.format(d);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }

    public boolean isMember(String userId) {
        if (members != null) {
            return members.contains(userId);
        }
        return false;
    }

    public int getCommentsCount() {
        if (comments != null) {
            return comments.size();
        }
        return 0;
    }

    public boolean isLiked(String userId) {
        if (fans != null) {
            return fans.contains(userId);
        }
        return false;
    }

    public int getLikesCount() {
        if (fans != null) {
            return fans.size();
        }
        return 0;
    }

    public boolean isFavorited(String userId) {
        if (favorites != null) {
            return favorites.contains(userId);
        }
        return false;
    }

    public int getFavoritesCount() {
        if (favorites != null) {
            return favorites.size();
        }
        return 0;
    }

    public boolean isExpired() {
        DateTime dt = new DateTime(date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute());
        if (dt.isBeforeNow()) {
            return true;
        }
        return false;
    }

    public boolean isNearby() {
        if (distance != null) {
            double dt = Double.parseDouble(distance);
            if (dt <= NEARBY_THRESHOLD) {
                return true;
            }
        }
        return false;
    }
}
