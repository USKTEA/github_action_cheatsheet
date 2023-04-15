package com.usktea.plainold.models.answer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Content {
    @Column(name = "content")
    private String value;

    public Content() {
    }

    public Content(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Content otherContent = (Content) object;

        return Objects.equals(value, otherContent.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public String getValue() {
        return value;
    }
}
