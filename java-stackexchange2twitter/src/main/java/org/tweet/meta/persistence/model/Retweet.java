package org.tweet.meta.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.common.persistence.IEntity;

@Entity
@Table(name = "retweet", uniqueConstraints = @UniqueConstraint(columnNames = { "tweetId", "twitterAccount" }))
public class Retweet implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "QT_ID")
    private long id;

    @Column(nullable = false)
    private long tweetId;

    @Column(nullable = false)
    private String twitterAccount;

    @Column(nullable = false)
    private String text;

    @Column(nullable = true)
    private Date when;

    public Retweet() {
        super();
    }

    public Retweet(final long tweetId, final String twitterAccount, final String text, final Date when) {
        super();

        this.tweetId = tweetId;
        this.twitterAccount = twitterAccount;
        this.text = text;
        this.when = when;
    }

    // API

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(final long id) {
        this.id = id;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(final String twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(final long tweetId) {
        this.tweetId = tweetId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(final Date when) {
        this.when = when;
    }

    //

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + (int) (tweetId ^ (tweetId >>> 32));
        result = prime * result + ((twitterAccount == null) ? 0 : twitterAccount.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Retweet other = (Retweet) obj;
        if (text == null) {
            if (other.text != null) {
                return false;
            }
        } else if (!text.equals(other.text)) {
            return false;
        }
        if (tweetId != other.tweetId) {
            return false;
        }
        if (twitterAccount == null) {
            if (other.twitterAccount != null) {
                return false;
            }
        } else if (!twitterAccount.equals(other.twitterAccount)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Retweet [tweetId=").append(tweetId).append(", twitterAccount=").append(twitterAccount).append(", text=").append(text).append("]");
        return builder.toString();
    }

}
