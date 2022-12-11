package org.stackexchange.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.common.persistence.IEntity;

// TODO: add site to the question tweet entity
@Entity
@Table(name = "question_tweet", uniqueConstraints = @UniqueConstraint(columnNames = { "questionId", "twitterAccount" }))
public class QuestionTweet implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "QT_ID")
    private long id;

    @Column(nullable = false)
    private String questionId;

    @Column(nullable = false)
    private String twitterAccount;

    @Column(nullable = true)
    private String stackAccount;

    @Column(nullable = true)
    private Date when;

    public QuestionTweet() {
        super();
    }

    public QuestionTweet(final String questionId, final String twitterAccount, final String stackAccount, final Date when) {
        super();

        this.questionId = questionId;
        this.twitterAccount = twitterAccount;
        this.stackAccount = stackAccount;
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

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(final String questionId) {
        this.questionId = questionId;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(final String twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    public String getStackAccount() {
        return stackAccount;
    }

    public void setStackAccount(final String stackAccount) {
        this.stackAccount = stackAccount;
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
        result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
        result = prime * result + ((stackAccount == null) ? 0 : stackAccount.hashCode());
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
        final QuestionTweet other = (QuestionTweet) obj;
        if (questionId == null) {
            if (other.questionId != null) {
                return false;
            }
        } else if (!questionId.equals(other.questionId)) {
            return false;
        }
        if (stackAccount == null) {
            if (other.stackAccount != null) {
                return false;
            }
        } else if (!stackAccount.equals(other.stackAccount)) {
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
        builder.append("QuestionTweet [questionId=").append(questionId).append(", twitterAccount=").append(twitterAccount).append(", stackAccount=").append(stackAccount).append(", when=").append(when).append("]");
        return builder.toString();
    }

}
