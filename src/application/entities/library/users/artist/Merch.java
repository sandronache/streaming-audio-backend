package application.entities.library.users.artist;

import lombok.Getter;

import java.util.Objects;

/**
 * Class for a piece of merchandise
 */
@Getter
public final class Merch {
    private String name;
    private String description;
    private Integer price;

    /**
     * Constructor
     * @param name
     * @param description
     * @param price
     */
    public Merch(final String name, final String description,
                 final Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Merch merch = (Merch) o;
        return Objects.equals(name, merch.name)
                && Objects.equals(description, merch.description)
                && Objects.equals(price, merch.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setPrice(final Integer price) {
        this.price = price;
    }
}
