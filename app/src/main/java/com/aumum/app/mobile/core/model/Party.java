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
    protected String deletedAt;
    protected String userId;
    protected String title;
    protected Date date;
    protected Time time;
    protected String address;
    protected Double latitude;
    protected Double longitude;
    protected String location;
    protected String details;
    protected String groupId;
    protected List<String> members = new ArrayList<String>();
    protected List<String> likes = new ArrayList<String>();
    protected List<String> comments = new ArrayList<String>();
    protected List<String> reasons = new ArrayList<String>();
    protected List<String> favorites = new ArrayList<String>();
    protected List<String> images = new ArrayList<String>();
    protected List<String> subscriptions;

    protected String distance;
    protected User user;

    public Party() {
        date = new Date();
        time = new Time();
    }

    public Party(String userId,
                 String title,
                 Date date,
                 Time time,
                 Place place,
                 String location,
                 String details,
                 List<String> images,
                 List<String> subscriptions) {
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.address = place.getLocation();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.details = details;
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
        if (subscriptions != null) {
            this.subscriptions = new ArrayList<String>();
            this.subscriptions.clear();
            this.subscriptions.addAll(subscriptions);
        }
    }

    public Party(String objectId,
                 String createdAt,
                 String userId,
                 String title,
                 Date date,
                 Time time,
                 String address,
                 Double latitude,
                 Double longitude,
                 String location,
                 String details,
                 String groupId,
                 List<String> members,
                 List<String> likes,
                 List<String> comments,
                 List<String> reasons,
                 List<String> favorites,
                 List<String> images) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.details = details;
        this.groupId = groupId;
        if (members != null) {
            this.members.clear();
            this.members.addAll(members);
        }
        if (likes != null) {
            this.likes.clear();
            this.likes.addAll(likes);
        }
        if (comments != null) {
            this.comments.clear();
            this.comments.addAll(comments);
        }
        if (reasons != null) {
            this.reasons.clear();
            this.reasons.addAll(reasons);
        }
        if (favorites != null) {
            this.favorites.clear();
            this.favorites.addAll(favorites);
        }
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
    }

    public String getDeletedAt() {
        return deletedAt;
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

    public String getDateTimeText() {
        return date.getDateText() + " " + time.getTimeText();
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getDetails() {
        return details;
    }

    public String getGroupId() {
        return groupId;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getLikes() {
        return likes;
    }

    public List<String> getComments() { return comments; }

    public List<String> getReasons() {
        return reasons;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public List<String> getImages() {
        return images;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(double cLat, double cLong) {
        if (latitude != null && longitude != null) {
            double lat1 = (Math.PI / 180) * cLat;
            double lat2 = (Math.PI / 180) * latitude;

            double lon1 = (Math.PI / 180) * cLong;
            double lon2 = (Math.PI / 180) * longitude;

            double R = 6371;
            double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
            DecimalFormat df = new DecimalFormat("#.#");
            distance = df.format(d);
        }
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
        if (likes != null) {
            return likes.contains(userId);
        }
        return false;
    }

    public int getLikesCount() {
        if (likes != null) {
            return likes.size();
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

    public boolean isNearBy() {
        if (distance != null) {
            return Float.parseFloat(distance) < 10;
        }
        return false;
    }

    public boolean isFarAway() {
        if (distance != null) {
            return Float.parseFloat(distance) > 100;
        }
        return false;
    }

    public void addReason(String reasonId) {
        if (reasons != null && !reasons.contains(reasonId)) {
            reasons.add(reasonId);
        }
    }

    public void addComment(String commentId) {
        if (comments != null && !comments.contains(commentId)) {
            comments.add(commentId);
        }
    }

    public void removeComment(String commentId) {
        if (comments != null && comments.contains(commentId)) {
            comments.remove(commentId);
        }
    }

    public void addMember(String memberId) {
        if (members != null && !members.contains(memberId)) {
            members.add(memberId);
        }
    }

    public void removeMember(String memberId) {
        if (members != null && members.contains(memberId)) {
            members.remove(memberId);
        }
    }

    public void addFavorite(String userId) {
        if (favorites != null && !favorites.contains(userId)) {
            favorites.add(userId);
        }
    }

    public void removeFavorite(String userId) {
        if (favorites != null && favorites.contains(userId)) {
            favorites.remove(userId);
        }
    }

    public void addLike(String userId) {
        if (likes != null && !likes.contains(userId)) {
            likes.add(userId);
        }
    }

    public void removeLike(String userId) {
        if (likes != null && likes.contains(userId)) {
            likes.remove(userId);
        }
    }
}
